package com.zhidao.logcommon.utils;

import com.zhidao.logcommon.utils.log.LoggerController;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Xuanlh
 * @desc
 * @date 2021/2/7
 */

public class ThreadPoolManager {

    private static final String TAG = ThreadPoolManager.class.getSimpleName();

    private static ThreadPoolManager mInstance;


    /**
     * 核心线程池的数量，同时能够执行的线程数量
     */
    private int corePoolSize = 2;
    /**
     * 最大线程池数量，表示当缓冲队列满的时候能继续容纳的等待任务的数量
     */
    private int maximumPoolSize;
    /**
     * 存活时间
     */
    private long keepAliveTime = 60;
    private TimeUnit unit = TimeUnit.SECONDS;
    private ThreadPoolExecutor executor;

    public static ThreadPoolManager getInstance() {
        if (mInstance == null) {
            synchronized (ThreadPoolManager.class) {
                if (mInstance == null) {
                    mInstance = new ThreadPoolManager();
                }
            }
        }
        return mInstance;
    }

    private ThreadPoolManager() {
/**
 * 给corePoolSize赋值：当前设备可用处理器核心数*2 + 1,能够让cpu的效率得到最大程度执行（有研究论证的）
 */
        maximumPoolSize = Runtime.getRuntime().availableProcessors() * 2 + 1;
        //虽然maximumPoolSize用不到，但是需要赋值，否则报错
//        maximumPoolSize = corePoolSize;
        executor = new ThreadPoolExecutor(
                //当某个核心任务执行完毕，会依次从缓冲队列中取出等待任务
                corePoolSize,
                //5,先corePoolSize,然后new LinkedBlockingQueue<Runnable>(),然后maximumPoolSize,但是它的数量是包含了corePoolSize的
                maximumPoolSize,
                //表示的是maximumPoolSize当中等待任务的存活时间
                keepAliveTime,
                unit,
                //缓冲队列，用于存放等待任务，Linked的先进先出
                new LinkedBlockingQueue<Runnable>(),
                //创建线程的工厂
                //  Executors.defaultThreadFactory(),
                new DefaultThreadFactory(Thread.NORM_PRIORITY, "autopilot-async-pool-"),
                //用来对超出maximumPoolSize的任务的处理策略
                new ThreadPoolExecutor.AbortPolicy()
        );
    }

    /**
     * 执行任务
     *
     * @param runnable
     */
    public void execute(Runnable runnable) {
        if (executor == null) {
            executor = new ThreadPoolExecutor(
                    //当某个核心任务执行完毕，会依次从缓冲队列中取出等待任务
                    corePoolSize,
                    //5,先corePoolSize,然后new LinkedBlockingQueue<Runnable>(),然后maximumPoolSize,但是它的数量是包含了corePoolSize的
                    maximumPoolSize,
                    //表示的是maximumPoolSize当中等待任务的存活时间
                    keepAliveTime,
                    unit,
                    //缓冲队列，用于存放等待任务，Linked的先进先出
                    new LinkedBlockingQueue<Runnable>(),
                    //创建线程的工厂
                    //  Executors.defaultThreadFactory(),
                    new DefaultThreadFactory(Thread.NORM_PRIORITY, "async-pool-"),
                    //用来对超出maximumPoolSize的任务的处理策略
                    new ThreadPoolExecutor.AbortPolicy()
            );
        }
        if (runnable != null) {
            executor.execute(runnable);
        }
        LoggerController.w(TAG, "===>execute" + executor.getQueue().size());

    }
    /**
     * 建议停止正在执行的任务
     */
    public void shutDownNow() {
        if (executor != null) {
            executor.shutdownNow();
        }
    }

    /**
     * release
     */
    public void release() {
        shutDownNow();
        executor = null;
        mInstance = null;
    }

    /**
     * 移除队列里面的任务
     */
    public void remove() {
        if (null != executor) {
            BlockingQueue<Runnable> queue = executor.getQueue();
            if (null != queue) {
                for (Runnable runnable : queue) {
                    if (null == runnable) {
                        continue;
                    }
                    remove(runnable);
                }
            }
        }
    }


    /**
     * 移除任务
     */
    public void remove(Runnable runnable) {
        if (runnable != null) {
            executor.remove(runnable);
        }
    }

    //取消线程
    public void cancel(Runnable r) {
        if (executor != null) {
            boolean isRemoveSuccess = executor.getQueue().remove(r);
            LoggerController.w(TAG, "===>execute" + executor.getQueue().size());
        }
    }

    /**
     * 创建线程的工厂，设置线程的优先级，group，以及命名
     */
    private static class DefaultThreadFactory implements ThreadFactory {
        /**
         * 线程池的计数
         */
        private static final AtomicInteger poolNumber = new AtomicInteger(1);

        /**
         * 线程的计数
         */
        private final AtomicInteger threadNumber = new AtomicInteger(1);

        private final ThreadGroup group;
        private final String namePrefix;
        private final int threadPriority;

        DefaultThreadFactory(int threadPriority, String threadNamePrefix) {
            this.threadPriority = threadPriority;
            this.group = Thread.currentThread().getThreadGroup();
            namePrefix = threadNamePrefix + poolNumber.getAndIncrement() + "-thread-";
            LoggerController.w(TAG, "===>DefaultThreadFactory" + namePrefix);
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
            if (t.isDaemon()) {
                t.setDaemon(false);
            }
            t.setPriority(threadPriority);
            return t;
        }
    }
}
