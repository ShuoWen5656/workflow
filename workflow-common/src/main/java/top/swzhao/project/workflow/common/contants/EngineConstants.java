package top.swzhao.project.workflow.common.contants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author swzhao
 * @data 2023/9/10 15:12
 * @Discreption <> 引擎常亮，字典变量组
 */
public class EngineConstants {

    /*******************************************************************************************************************
     *                                                     key常量                                                      *
     *******************************************************************************************************************/

    /**
     * 字符串 - jdbc配置的三种表达方式
     */
    public static final String STR_JDBC_URL1 = "spring.datasource.flow.jdbc-url";
    public static final String STR_JDBC_URL2 = "spring.datasource.flow.jdbc_url";
    public static final String STR_JDBC_URL3 = "spring.datasource.flow.jdbcUrl";


    /**
     * 字符串 - 引擎初始化表常
     */
    public static final String STR_TABLE_NAME_FLOW_TPL = "FLOW_tpl";
    public static final String STR_TABLE_NAME_FLOW_SUB_TPL = "FLOW_sub_tpl";
    public static final String STR_TABLE_NAME_FLOW_TPL_SUBCLASS_NAME = "FLOW_tpl_sub_class_name";
    public static final String STR_TABLE_NAME_PROCESS= "FLOW_process";
    public static final String STR_TABLE_NAME_SUB_PROCESS= "FLOW_sub_process";
    public static final String STR_TABLE_NAME_FLOW_DIC = "FLOW_dic";
    public static final String STR_TABLE_NAME_FLOW_VARIABLE = "FLOW_variable";


    /**
     * Integer - 执行变量类型：
     * 0：全局变量
     * 1：临时变量
     * 2：回滚全局变量
     * 3、回滚临时变量
     */
    public static final Integer STR_VALUE_VARIABLE_TYPE_GLOBAL = 0;
    public static final Integer STR_VALUE_VARIABLE_TYPE_TEMP = 1;
    public static final Integer STR_VALUE_VARIABLE_TYPE_GLOBAL_ROLLBACK = 2;
    public static final Integer STR_VALUE_VARIABLE_TYPE_TEMP_ROLLBACK = 3;


    /**
     * Integer - 执行异常类型：
     * 0：业务异常
     * 1：引擎异常
     * 3：未知异常
     */
    public static final Integer STR_VALUE_ERROR_TYPE_BUSINESS = 0;
    public static final Integer STR_VALUE_ERROR_ENGINE = 1;
    public static final Integer STR_VALUE_ERROR_UNKNOWN = 2;

    /**
     * 字符串 - 当前进程处理状态
     */
    public static final String STR_VALUE_METHOD_NAME_START = "start";
    public static final String STR_VALUE_METHOD_NAME_ROLLBACK = "rollback";


    /**
     * 字符串 - 执行动作
     */
    public static final String STR_VALUE_ENGINE_START = "start";
    public static final String STR_VALUE_ENGINE_RE_START = "restart";
    public static final String STR_VALUE_ENGINE_RE_RUN = "rerun";
    public static final String STR_VALUE_ENGINE_SKIP = "skip";
    public static final String STR_VALUE_ENGINE_ROLLBACK = "rollback";
    public static final String STR_VALUE_ENGINE_ROLLBACK_SUB_PROCESS = "rollbackSubProcess";



    /*******************************************************************************************************************
     *                                                     自定义常量组                                                  *
     *******************************************************************************************************************/

    /**
     * 获取jdbcurl定义key集合
     * @return
     */
    public static String[] listJDBCUrl() {
        return new String[] {EngineConstants.STR_JDBC_URL1,
                             EngineConstants.STR_JDBC_URL2,
                             EngineConstants.STR_JDBC_URL3};
    }

    /**
     * 获取引擎所需的所有表名称
     * @return
     */
    public static String[] listTableName(){
        return new String[]{EngineConstants.STR_TABLE_NAME_FLOW_DIC,
        EngineConstants.STR_TABLE_NAME_FLOW_SUB_TPL,
        EngineConstants.STR_TABLE_NAME_FLOW_VARIABLE,
        EngineConstants.STR_TABLE_NAME_FLOW_TPL,
        EngineConstants.STR_TABLE_NAME_FLOW_TPL_SUBCLASS_NAME,
        EngineConstants.STR_TABLE_NAME_PROCESS,
        EngineConstants.STR_TABLE_NAME_SUB_PROCESS};
    }

    /**
     * 获取引擎开始动作组
     * @return
     */
    public static List<String> listEngineStartAction(){
        return new ArrayList<>(Arrays.asList(STR_VALUE_ENGINE_START,
                STR_VALUE_ENGINE_RE_START,
                STR_VALUE_ENGINE_RE_RUN,
                STR_VALUE_ENGINE_SKIP));
    }


    /**
     * 获取引擎回滚动作组
     * @return
     */
    public static List<String> listEngineRollBackAction(){
        return new ArrayList<>(Arrays.asList(STR_VALUE_ENGINE_ROLLBACK,
                STR_VALUE_ENGINE_ROLLBACK_SUB_PROCESS));

    }

}
