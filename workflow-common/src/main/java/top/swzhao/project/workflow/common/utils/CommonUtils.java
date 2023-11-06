package top.swzhao.project.workflow.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import top.swzhao.project.workflow.common.contants.FlowKvConstants;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

import org.apache.commons.collections.CollectionUtils;

import java.util.Arrays;
import java.util.List;

/**
 * @author swzhao
 * @date 2023/10/20 9:30 下午
 * @Discreption <> 部分公共方法
 */
public class CommonUtils {


    /**
     * 从编译单元的reference中获取当前编译单元的驼峰名称
     * @param classRef
     * @return
     */
    public static String change2CamelFromClassRef(String classRef) {
        if (StringUtils.isBlank(classRef)) {
            return FlowKvConstants.STR_VALUE_EMP;
        }
        String[] split = classRef.split(FlowKvConstants.STR_VALUE_SPOT);
        String simpleName = split[split.length - 1];
        return simpleName.substring(0,1).toLowerCase().concat(simpleName.substring(FlowKvConstants.NUM_VALUE_I));
    }

    /**
     * fastjson深拷贝
     * @param obj
     * @param reference
     * @param <T>
     * @return
     */
    public static <T> T deepCopyForJson(T obj, TypeReference<T> reference) {
        return JSONObject.parseObject(JSON.toJSONString(obj), reference);
    }

    /**
     * java序列化深拷贝
     * @param obj
     * @param <T>
     * @return
     */
    public static <T extends Serializable> T deepCopyForSerialize(T obj) {
        return SerializationUtils.clone(obj);
    }

    /**
     * stream时根据对象中某个key值进行去重时使用的断言表达
     * @param keyExtractor
     * @param <T>
     * @return
     */
    public static <T> Predicate<T> distinctByCustomKey(Function<? super T, Object> keyExtractor) {
        ConcurrentHashMap<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> {
            if (keyExtractor.apply(t) == null) {
                return false;
            }
            return seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
        };
    }

    /**
     * 将obj转成嵌套泛型的对象
     * @param obj
     * @param reference
     * @param <T>
     * @return
     */
    public static <T> T convertToDeclareClass(Object obj, TypeReference<T> reference) {
        return JSONObject.parseObject(JSON.toJSONString(obj), reference);
    }



    public static void main(String[] args) {
        List<Integer> a = Arrays.asList(1, 1, 1, 1, 1, 1, 2, 3, 4, 5);
        List<Integer> b = Arrays.asList(1, 1, 1, 1, 2, 3, 8);
        CollectionUtils.subtract(b, a);
    }
}
