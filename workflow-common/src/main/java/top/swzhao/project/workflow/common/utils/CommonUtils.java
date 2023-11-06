package top.swzhao.project.workflow.common.utils;

import org.apache.commons.collections.CollectionUtils;

import java.util.Arrays;
import java.util.List;

/**
 * @author swzhao
 * @date 2023/10/20 9:30 下午
 * @Discreption <>
 */
public class CommonUtils {

    public static void main(String[] args) {
        List<Integer> a = Arrays.asList(1, 1, 1, 1, 1, 1, 2, 3, 4, 5);
        List<Integer> b = Arrays.asList(1, 1, 1, 1, 2, 3, 8);
        CollectionUtils.subtract(b, a);
    }
}
