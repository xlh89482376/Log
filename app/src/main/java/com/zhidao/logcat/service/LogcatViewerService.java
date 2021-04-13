package com.zhidao.logcat.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.zhidao.logcat.utils.Constants;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;


/**
 * Service to listen logcat logs.
 */
public class LogcatViewerService extends Service {
    private static final String LOG_TAG = "LogcatViewerService";


    private static Handler mHandler;

    /**
     * Logcat source buffer.
     */
    private String mLogcatSource = Constants.LOGCAT_SOURCE_BUFFER_MAIN;

    private String mRecordingFilename;

    private Vector<String> mRecordingData;

    private int mRecordedLogEntriesCount;

    //Threads
    private volatile boolean mShouldLogcatRunnableBeKilled = false;
    private volatile boolean mIsLogcatRunnableRunning = false;

    private boolean mIsRecording = false;

    private boolean mIsPaused = false;

    private String mFilterText;

    private static final int LOG_SAVING_INTERVAL = 5000; //5s

    public static final int MSG_LOGCAT_RUN_FAILURE = 1;

    public static final int MSG_LOGCAT_READ_FAILURE = 2;

    public static final int MSG_NEW_LOG_ENTRY = 3;


    private final Runnable mLogcatRunnable = () -> {
        mIsLogcatRunnableRunning = true;
        //Run logcat subscriber to subscribe for logcat log entries
        runLogcatSubscriber();
        //If reached here, it means thread is killed.
        mIsLogcatRunnableRunning = false;
        return;
    };

    private final Runnable mRecordLogEntryRunnable = new Runnable() {
        @Override
        public void run() {
            //save log entries
            recordLogData();
            //wait for LOG_SAVING_INTERVAL before next 'record' operation.
            mHandler.postDelayed(mRecordLogEntryRunnable, LOG_SAVING_INTERVAL);
        }
    };


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int i = super.onStartCommand(intent, flags, startId);
        Log.i(LOG_TAG, "onStartCommand:service is started.");
        return i;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        //Kill mLogcatRunnable thread.
        requestToKillLogcatRunnableThread();

        //Till thread is not killed, wait
        while (mIsLogcatRunnableRunning) {
            Log.d(LOG_TAG, "onUnbind:Waiting to kill LogcatRunnable thread");
        }

        //Stop LogcatViewerService service.
        stopSelf();
        return false;
    }

    /**
     * Handler to sendMessage messages from service to View.
     * @param handler handler from view
     */
    public static void setHandler(Handler handler) {
        mHandler = handler;
    }

    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /**
     * Subscribe logcat to listen logcat log entries.
     */
    private void runLogcatSubscriber() {
        Process process = null;

        //Execute logcat system command
        try {
            process = Runtime.getRuntime().exec("/system/bin/logcat -b " + mLogcatSource);
        } catch (IOException e) {
            sendMessage(MSG_LOGCAT_RUN_FAILURE);
        }

        //Read logcat log entries
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String logEntry;

            //Till request to kill thread is not received, keep reading log entries
            while (!shouldLogcatRunnableBeKilled()) {
                //if paused, ignore entries.
                if (mIsPaused) {
                    continue;
                }

                //Read log entry.
                logEntry = reader.readLine();

                //Send log entry to view.
                sendLogEntry(logEntry);

                //If recording is on, save log entries in mRecordingData in order to save them
                // after every LOG_SAVING_INTERVAL interval
                if (mIsRecording) {
                    if(TextUtils.isEmpty(mFilterText) ||
                            (!TextUtils.isEmpty(mFilterText) && logEntry.toLowerCase().contains(mFilterText.toLowerCase()))) {
                        mRecordingData.add(logEntry);
                    }
                }
            }

            Log.d(LOG_TAG, "Preparing to terminate LogcatRunnable thread");
            //If recording is on, save log entries and reset recording related fields.
            if (mIsRecording) {
                recordLogData();

                mHandler.removeCallbacks(mRecordLogEntryRunnable);
                mIsRecording = false;
                mRecordingData.removeAllElements();
                mRecordingFilename = null;

            }

            //Release resources
            reader.close();
            process.destroy();

        } catch (IOException e) {
            //Fail to read logcat log entries
            sendMessage(MSG_LOGCAT_READ_FAILURE);
        }

        Log.d(LOG_TAG, "Terminating LogcatRunnable thread");
        return;
    }

    /**
     * Make request to kill LogcatRunnable thread.
     */
    private synchronized void requestToKillLogcatRunnableThread() {
        mShouldLogcatRunnableBeKilled = true;
    }

    /**
     * Check if request to kill LogcatRunnable thread is made.
     * @return true if request is made else false.
     */
    private synchronized boolean shouldLogcatRunnableBeKilled() {
        return mShouldLogcatRunnableBeKilled;
    }

    /**
     * Send handler messages to view - communication from service to view.
     * @param msg message constant - starting with MSG_
     */
    private void sendMessage(int msg) {
        Message.obtain(mHandler, msg, "error").sendToTarget();
    }

    /**
     * Send logcat log entry to view.
     * @param logEntry log entry.
     */
    private void sendLogEntry(String logEntry) {
        Message.obtain(mHandler, MSG_NEW_LOG_ENTRY, logEntry).sendToTarget();
    }

    /**
     * Save log data to file
     */
    private void recordLogData() {
        try {
            int size = mRecordingData.size();
            //no entry to save, so return
            if(size == 0){
                return;
            }

            //Since logcat keeps adding logentries to mRecordingData, keep it in local field.
            Vector<String> recordingData = new Vector<>(mRecordingData);

            //Get log directory.
            File logDir = Constants.getRecordDir(this);
            logDir.mkdirs();

            //Get log file.
            File logFile = new File(logDir, mRecordingFilename);

            //Get writer to write in log file. Enable 'Append' mode.
            FileWriter logFileWriter = new FileWriter(logFile, true);

            //Write to log file.
            for (int i = 0; i < size; i++) {
                logFileWriter.append(recordingData.elementAt(i) + "\n");
                //Once saved, delete it from mRecordingData.
                mRecordingData.removeElementAt(0);
            }

            //Release resources.
            recordingData.removeAllElements();
            logFileWriter.close();

        } catch (Exception e) {
            Log.e(LOG_TAG, "recordLogData:Error writing the log to file. Exception: " + e.toString());
        }
    }


    //AIDL to communicate from view to service.
    private final ILogcatViewerService.Stub mBinder = new ILogcatViewerService.Stub() {

        public void changeLogcatSource(String logcatSource) {
            mLogcatSource = logcatSource;
            restart();
        }

        public void restart() {
            //request to kill thread
            requestToKillLogcatRunnableThread();

            //Till thread is not killed, wait
            while (mIsLogcatRunnableRunning) {
                Log.d(LOG_TAG, "restart:Waiting to kill LogcatRunnable thread");
            }

            //since request to kill is completed, set mShouldLogcatRunnableBeKilled to false
            mShouldLogcatRunnableBeKilled = false;

            //Start new LogcatRunnable thread.
            Thread thr = new Thread(mLogcatRunnable);
            thr.start();
        }

        public void stop() {
            Log.d(LOG_TAG, "stop:request to stop LogcatViewerService service is made.");
            //Kill mLogcatRunnable thread.
            requestToKillLogcatRunnableThread();

            //Till thread is not killed, wait
            while (mIsLogcatRunnableRunning) {
                Log.d(LOG_TAG, "stop:Waiting to kill LogcatRunnable thread");
            }

            //Stop LogcatViewerService service.
            stopSelf();
        }

        public void startRecording(String recordingFilename, String filterText) {
            mRecordingData = new Vector<>();
            mIsRecording = true;
            mRecordingFilename = recordingFilename;
            mFilterText = filterText;
            mHandler.postDelayed(mRecordLogEntryRunnable, LOG_SAVING_INTERVAL);
        }

        public void stopRecording() {
            mHandler.removeCallbacks(mRecordLogEntryRunnable);
            mIsRecording = false;
            recordLogData();
            mRecordingData.removeAllElements();
            mRecordingFilename = null;
        }

        public boolean isRecording() {
            return mIsRecording;
        }

        public void pause() {
            mIsPaused = true;
        }

        public void resume() {
            mIsPaused = false;
        }
    };
}
