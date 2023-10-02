package top.swzhao.project.workflow.common.enums;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author swzhao
 * @data 2023/10/2 23:02
 * @Discreption <> 流程状态枚举变量
 */
public enum  ProcessStateEnum {


    PROCESS_STATE_UNSTART(0, "未开始"),
    PROCESS_STATE_START(1, "执行中"),
    PROCESS_STATE_FINISH(2, "已完成"),
    PROCESS_STATE_FAIL(3, "失败"),
    PROCESS_STATE_ROLLBACK(4, "回滚中"),
    PROCESS_STATE_ROLLBACK_FINISH(5, "已回滚"),
    PROCESS_STATE_ROLLBACK_FAIL(6, "回滚失败"),
    PROCESS_STATE_SKIP(7, "跳过");


    private Integer code;
    private String name;


    ProcessStateEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }




    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }


    public static List<Integer> listActiveState() {
        return new ArrayList<>(Arrays.asList(PROCESS_STATE_UNSTART.getCode(),
                PROCESS_STATE_START.getCode(),
                PROCESS_STATE_ROLLBACK.getCode(),
                PROCESS_STATE_ROLLBACK_FINISH.getCode(),
                PROCESS_STATE_ROLLBACK_FAIL.getCode()));
    }




    public static String getNameByCode(Integer code) {
        for (ProcessStateEnum processStateEnum : ProcessStateEnum.values()) {
            if (processStateEnum.getCode().equals(code)) {
                return processStateEnum.getName();
            }
        }
        return null;
    }

}
