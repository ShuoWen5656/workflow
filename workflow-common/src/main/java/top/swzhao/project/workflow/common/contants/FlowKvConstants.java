package top.swzhao.project.workflow.common.contants;

/**
 * @author swzhao
 * @data 2023/10/2 22:06
 * @Discreption <> 通用常量
 */
public class FlowKvConstants {


    /**
     *  字符串 - key类型 - status
     */
    public static final String STR_KEY_STATUS = "status";
    /**
     * 字符串 - key类型 - type
     */
    public static final String STR_KEY_TYPE = "type";
    /**
     * 字符串 - key类型 - sort
     */
    public static final String STR_KEY_SORT = "sort";
    /**
     * 字符串 - key类型 - asc
     */
    public static final String STR_KEY_ASC = "asc";
    /**
     * 字符串 - key类型 - desc
     */
    public static final String STR_KEY_DESC = "desc";
    /**
     * 字符串- key类型 - flowTpl
     */
    public static final String STR_KEY_FLOW_TPL = "fowTpl";
    /**
     * 字符串- key类型 - subTplList
     */
    public static final String STR_KEY_SUB_TPL_LIST= "subTplList";
    /**
     * 字符串 - key类型 - flowProcessSubProcessDto
     */
    public static final String STR_KEY_PROCESS_SUB_PROCESS_DTO = "flowProcessSubProcessDto";
    /**
     * 字符串 - key类型 - process_id
     */
    public static final String STR_KEY_SUB_PROCESS_ID = "process_id";
    /**
     * 字符串 - key类型 - flowProcess
     */
    public static final String STR_KEY_FLOWPROCESS = "flowProcess";
    /**
     * 字符串 - key类型 - fLowTpl
     */
    public static final String STR_KEY_SUB_TPL_DTOS = "flowSubTpiDtos";
    /**
     * 字符 - key类型 - id
     */
    public static final String STR_KEY_ID = "id";
    /**
     * 字符串 - key类型 - createTime
     */
    public static final String STR_KEY_CREATE_TIME = "createTime";
    /**
     * 字符串 - key类型 - updateTime
     */
    public static final String STR_KEY_UPDATE_TIME = "updateTime";
    /**
     * 字符串 - value型 - url
     */
    public static final String STR_VALUE_URL = "url";
    /**
     * 字符串 - value类型 - 空字符串
     */
    public static final String STR_VALUE_EMP = "";
    /**
     * 字符串 - value型 - 问
     */
    public static final String STR_VALUE_QM ="?";
    /**
     * 字符串 - value类型 - 正斜杠
     */
    public static final String STR_VALUE_CLASH ="/";
    /**
     * 字符串 - value类型 - 逗号
     */
    public static final String STR_VALUE_COMMA = ",";
    /**
     * 字符串 - value类型 - 点
     */
    public static final String STR_VALUE_SPOT = "\\.";
    /**
     * 字符串 - value类型 - thread_pool
     */
    public static final String STR_VALUE_THREAD_POOL = "thread_pool";
    /**
     * 字符串 - value类型 - core_num
     */
    public static final String STR_VALUE_CORE_NUM = "core_num";
    /**
     * 字符串 - value类型 - max_num
     */
    public static final String STR_VALUE_MAX_NUM = "max_num";
    /**
     * 字符串 - value类型 - keep_alive_num
     */
    public static final String STR_VALUE_KEEP_ALIVE_NUM = "keep_alive_num";
    /**
     * 字符串 - value类型 - queue_length
     */
    public static final String STR_VALUE_QUEUE_LENGTH = "queue_length";
    /**
     * 字符串 - value类型 - start
     */
    public static final String STR_VALUE_START = "start";
    /**
     * 字符串 - value类型 - rollback
     */
    public static final String STR_VALUE_ROLLBACK = "rollback";
    /**
     * 整型 - value类型 - 0
     */
    public static final Integer NUM_VALUE_O = 0;
    /**
     * 型 - value类型 - 1
     */
    public static final Integer NUM_VALUE_I = 1;
    /**
     * 型 - value类型 - -1
     */
    public static final Integer NUM_VALUE_NEGATIVE_1 = -1;
    /**
     * 字符串 - value类型 - 持久层配: mapper包坐标
     */
    public static final String STR_KEY_BASE_PACKAGE = "top.swzhao.project.workflow.common.mapper";
    /**
     * 字符串 - value类型 - 久层配:dataSourceFlow
     */
    public static final String STR_KEY_DATASOURCE_FLOW = "dataSourceFlow";
    /**
     * 字符串 - value类型 - 久层配:sqlSessionFactoryFlow
     */
    public static final String STR_KEY_SESSION_FACTORY_FLOW = "sqlSessionFactoryFlow";
    /**
     * 字符串 - value类型 - 久层配: datasource配前缀
     */
    public static final String STR_KEY_DATASOURCE_PREFIX = "spring.datasource.flow";
    /**
     * 字符串 - value类型 - 久层配:xm文件位
     */
    public static final String STR_KEY_MAPPER_XML_LOCATION = "classpath*:flowmapper/*.xml";
    /**
     * 字符串 - value类型 - 久层配: transactionflow
     */
    public static final String STR_KEY_TRANSACTION_MANAGER_FLOW = "transactionManagerFlow";
    /**
     * 字符串 - value类型 - 久层配:sglSessionTemplateFlow
     */
    public static final String STR_KEYSESSION_TEMPLATE_FLOW = "sqlSessionTemplateFlow";
    /**
     * 字符串 - format类型 - process命名
     */
    public static final String STR_CONTAIN_PROCESS_NAME = "%s-process-%s";
    /**
     * 字符串 - format类型 - sub-process命名
     */
    public static final String STR_CONTAIN_SUB_PROCESS_NAME = "%s-sub-process-%s";
    /**
     * 字符串 - format类型 - sql脚本路
     */
    public static final String STR_CONTAIN_CONSTANT = "sglscript/%s.sql";
    /**
     * 字串 - format类型 - 杠器
     */
    public static final String STR_CONTAIN_BAR_FORMAT = "%s-%s";
}
