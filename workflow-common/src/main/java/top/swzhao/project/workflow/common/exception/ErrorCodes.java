package top.swzhao.project.workflow.common.exception;

import java.util.HashMap;
import java.util.Map;

/**
 * @author swzhao
 * @date 2023/10/18 10:48 下午
 * @Discreption <> 错误码代码
 */
public class ErrorCodes {

    /*****************************************************************
     *                      错误码类型：通用类型                        *
     *****************************************************************/
    public static final Integer ENGINE_INIT_ERROR = 581001;
    public static final Integer TEMPLATE_ERROR = 581002;
    public static final Integer PROCESS_ERROR = 581003;
    public static final Integer VARIABLE_ERROR = 581004;
    public static final Integer EXECUTE_ERROR = 581005;
    public static final Integer UNKNOWN_ERROR = 581006;
    public static final Integer PROCESS_STATE_ERROR = 581007;


    /*****************************************************************
     *                      错误码类型：业务类型                        *
     *****************************************************************/
    /**
     * 在业务没有主动抛出异常的情况下出现的问题，比如业务自己没有预料到的空指针异常
     */
    public static final Integer BUSSINESS_CODE = 582001;


    public static final Map<Integer, String> ERROR_CODE_MAP = new HashMap<>();

    static {
        ERROR_CODE_MAP.put(ENGINE_INIT_ERROR, "引擎初始化异常:");
        ERROR_CODE_MAP.put(TEMPLATE_ERROR, "模板错误：");
        ERROR_CODE_MAP.put(PROCESS_ERROR, "流程错误:");
        ERROR_CODE_MAP.put(VARIABLE_ERROR, "变量错误:");
        ERROR_CODE_MAP.put(EXECUTE_ERROR, "业务错误:");
        ERROR_CODE_MAP.put(UNKNOWN_ERROR, "子流程执行错误:");
        ERROR_CODE_MAP.put(PROCESS_STATE_ERROR, "未知异常:");
        ERROR_CODE_MAP.put(BUSSINESS_CODE, "流程状态错误:");
    }

    public static Map<Integer, String> getErrorCodeMap() {
        return ERROR_CODE_MAP;
    }

    public static String convert2Msg(Integer code) {
        return ERROR_CODE_MAP.get(code) == null ? ERROR_CODE_MAP.get(BUSSINESS_CODE) : ERROR_CODE_MAP.get(code);
    }





}
