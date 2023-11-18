package top.swzhao.project.workflow.core.utils;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author swzhao
 * @date 2023/11/18 10:56 下午
 * @Discreption <> 获取一个线程池
 */
public class ExecutorPoolUtils {

    /**
     * 默认全局线程池
     */
    private static volatile ThreadPoolExecutor pool;

    public static ThreadPoolExecutor getPool() {
        if (pool != null) {
            return pool;
        }
        synchronized (ExecutorPoolUtils.class) {
            if (pool == null) {
                pool = new ExecutorPoolConfiguration().build();
            }
            return pool;
        }
    }

    public static ThreadPoolExecutor setPool(ThreadPoolExecutor pool1) {
        pool = pool1;
        return pool;
    }


}
