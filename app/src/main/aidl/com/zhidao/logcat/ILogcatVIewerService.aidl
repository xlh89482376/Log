package com.zhidao.logcat;

/**
 * AIDL for LogcatViewerService service.
 */
interface ILogcatViewerService {

	/**
         * Change logcat source buffer
         * @param buffer it can be 'main', 'radio', 'events' Check {@link com.fatangare.logcatviewer.utils.Constants} class.
         */
        void changeLogcatSource(String buffer);

        /**
         * Restart {@link com.fatangare.logcatviewer.service.LogcatViewerService} service.
         */
        void restart();

        /**
         * Stop {@link com.fatangare.logcatviewer.service.LogcatViewerService} service.
         */
        void stop();

        /**
         * Start saving logcat logs to given file for given filter-text.
         * File is stored in android.os.Environment.DIRECTORY_DOWNLOADS+ "/LogcatViewer/"+ getPackageName() directory.
         * Logs are saved to file after every {@link com.fatangare.logcatviewer.service.LogcatViewerService#LOG_SAVING_INTERVAL}.
         * @param logFilename file to which logs are saved.
         * @param filterText text by which logs should be filtered. It can be tag, package or some text.
         */
        void startRecording(String logFilename, String filterText);

        /**
         * Stop saving logcat logs.
         */
        void stopRecording();

        /**
         * Is 'saving logcat logs to file' active?
         * @return true if yes else false.
         */
        boolean isRecording();

        /**
         * Stop listening to logcat logs.
         */
        void pause();

        /**
         * Resume listening to logcat logs.
         */
        void resume();
}
