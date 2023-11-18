package top.swzhao.project.workflow.core.utils;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author swzhao
 * @date 2023/11/18 10:45 下午
 * @Discreption <>
 */
public class FlowThreadFactory implements ThreadFactory {


    /**
     * 池子序号
     */
    private static final AtomicInteger POOL_NUM = new AtomicInteger();

    /**
     * 线程组
     */
    private ThreadGroup group;

    /**
     * 线程序号
     */
    private static final AtomicInteger THREAD_NUM = new AtomicInteger();

    /**
     * 定制化线程名称
     */
    private String namePreFix;

    public FlowThreadFactory() {
        SecurityManager securityManager = System.getSecurityManager();
        group = (securityManager != null) ? securityManager.getThreadGroup() : Thread.currentThread().getThreadGroup();
        namePreFix = String.format("OMP_FLOW-pool-%s-thread-", POOL_NUM.getAndIncrement());
    }

    @Override
    public Thread newThread(Runnable r) {
        return new Thread(group, r, namePreFix + THREAD_NUM.getAndIncrement(), 0);
     }

}
