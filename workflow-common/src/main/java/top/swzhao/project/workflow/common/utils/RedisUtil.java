package top.swzhao.project.workflow.common.utils;

/**
 * @author swzhao
 * @date 2023/12/17 12:27 下午
 * @Discreption <> redis连接池工具类
 */
public class RedisUtil {


    /**
     * 删除keys
     * @param keys
     * @param dbIndex
     * @return
     */
    public static boolean delList(String[] keys, int dbIndex) {
        return false;
    }

    public static void luaExistKeyDel(String key, String uuid) {

    }

    public static boolean setRedisLock(String key, String value, int timeout) {
        return false;
    }
}
