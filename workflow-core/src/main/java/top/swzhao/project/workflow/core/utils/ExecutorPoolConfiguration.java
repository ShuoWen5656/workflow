package top.swzhao.project.workflow.core.utils;

import java.util.concurrent.*;

/**
 * @author swzhao
 * @data 2023/11/8 21:57
 * @Discreption <>  线程配置类，建造者
 */
public class ExecutorPoolConfiguration {

    /**
     * 核心线程数 - 10
     */
    private int sizeCore = 10;

    /**
     * 最大线程数 - 20
     */
    private int sizeMax = 20;


    /**
     * 最大线程存活空闲时间 - 默认 s
     */
    private TimeUnit timeUnit = TimeUnit.SECONDS;

    /**
     * 多余空闲时间等待 - 10 s
     */
    private long keepAliveTime = 10;

    /**
     * 阻塞队列 - 1024长度
     */
    private BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(1024);

    /**
     * 线程工厂
     */
    private ThreadFactory threadFactory = new FlowThreadFactory();

    /**
     * 阻塞队列满处理策略
     */
    private RejectedExecutionHandler rejectedExecutionException = new ThreadPoolExecutor.AbortPolicy();


    public ExecutorPoolConfiguration setSizeCore(int sizeCore) {
        this.sizeCore = sizeCore;
        return this;
    }

    public ExecutorPoolConfiguration setSizeMax(int sizeMax) {
        this.sizeMax = sizeMax;
        return this;
    }

    public ExecutorPoolConfiguration setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
        return this;
    }

    public ExecutorPoolConfiguration setKeepAliveTime(long keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
        return this;
    }

    public ExecutorPoolConfiguration setQueue(BlockingQueue<Runnable> queue) {
        this.queue = queue;
        return this;
    }

    public ExecutorPoolConfiguration setThreadFactory(ThreadFactory threadFactory) {
        this.threadFactory = threadFactory;
        return this;
    }

    public ExecutorPoolConfiguration setRejectedExecutionException(RejectedExecutionHandler rejectedExecutionException) {
        this.rejectedExecutionException = rejectedExecutionException;
        return this;
    }
    public int getSizeCore() {
        return sizeCore;
    }

    public int getSizeMax() {
        return sizeMax;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public long getKeepAliveTime() {
        return keepAliveTime;
    }

    public BlockingQueue<Runnable> getQueue() {
        return queue;
    }

    public ThreadFactory getThreadFactory() {
        return threadFactory;
    }

    public RejectedExecutionHandler getRejectedExecutionException() {
        return rejectedExecutionException;
    }



    public ThreadPoolExecutor build() {
        return new ThreadPoolExecutor(sizeCore, sizeMax, keepAliveTime, timeUnit, queue, threadFactory, rejectedExecutionException);
    }

}
