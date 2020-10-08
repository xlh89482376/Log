package com.zhidao.logcat;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


final class LogcatManager {

    /** 日志捕捉监听对象 */
    private static volatile Listener sListener;
    /** 日志捕捉标记 */
    private static volatile boolean FLAG_WORK;
    /** 备用存放集合 */
    private static final List<LogcatInfo> LOG_BACKUP = new ArrayList<>();

    /**
     * 开始捕捉
     */
    static void start(Listener listener) {
        FLAG_WORK = true;
        new Thread(new LogRunnable()).start();
        sListener = listener;
    }

    /**
     * 继续捕捉
     */
    static void resume() {
        FLAG_WORK = true;
        final Listener listener = sListener;
        if (listener != null && !LOG_BACKUP.isEmpty()) {
            for (LogcatInfo info : LOG_BACKUP) {
                if (info != null) {
                    listener.onReceiveLog(info);
                }
            }
        }
        LOG_BACKUP.clear();
    }

    /**
     * 暂停捕捉
     */
    static void pause() {
        FLAG_WORK = false;
    }

    /**
     * 停止捕捉
     */
    static void destroy() {
        FLAG_WORK = false;
        // 把监听对象置空，不然会导致内存泄漏
        sListener = null;
    }

    /**
     * 清空日志
     */
    static void clear() {
        try {
            new ProcessBuilder("logcat", "-c").start();
//            FLAG_WORK = true;
//            new Thread(new LogRunnable()).start();

            if (FLAG_WORK) {
                new Thread(new LogRunnable()).start();
            }
        } catch (IOException ignored) {}
    }

    private static class LogRunnable implements Runnable {

        @Override
        public void run() {
            BufferedReader reader = null;
            try {
                Process process = new ProcessBuilder("logcat", "-v", "threadtime").start();
//                Process process = new ProcessBuilder()
//                        .command("logcat", "-v")
//                        .redirectErrorStream(true)
//                        .start();
                reader = new BufferedReader(new InputStreamReader(process.getInputStream()));



                String line;

                while ((line = reader.readLine()) != null) {

                    synchronized (LogcatManager.class) {
                        if (LogcatInfo.IGNORED_LOG.contains(line)) {
                            continue;
                        }
                        LogcatInfo info = LogcatInfo.create(line);
                        if (info == null) {
                            continue;
                        }
                        if (FLAG_WORK) {
                            final Listener listener = sListener;
                            if (listener != null) {
                                listener.onReceiveLog(info);
                            }
                        } else {
                            // 这里可能会出现下标异常
                            LOG_BACKUP.add(info);
                        }

//                        LogcatInfo info = LogcatInfo.create(line);
//                        if (info == null) {
//                            continue;
//                        }
//                        final Listener listener = sListener;
//                        listener.onReceiveLog(info);
                    }
                }
                pause();
            } catch (IOException ignored) {
                pause();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException ignored) {}
                }
            }
        }
    }

    public interface Listener {
        void onReceiveLog(LogcatInfo info);
    }
}
