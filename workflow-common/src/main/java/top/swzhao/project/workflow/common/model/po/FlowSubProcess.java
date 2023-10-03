package top.swzhao.project.workflow.common.model.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.sql.Date;

/**
 * @author swzhao
 * @data 2023/10/3 16:39
 * @Discreption <>
 */
@Data
@TableName("FLOW_sub_process")
public class FlowSubProcess {

    public FlowSubProcess() {
    }

    public FlowSubProcess(String id, String name, String subTplId, String state, String processId, String sort) {
        this.id = id;
        this.name = name;
        this.subTplId = subTplId;
        this.state = state;
        this.processId = processId;
        this.sort = sort;
    }

    /**
     * 主键 - id
     */
    @TableId(type = IdType.INPUT)
    @TableField("id")
    private String id;

    /**
     * 任务名称
     */
    @TableField("name")
    private String name;


    /**
     * 进程对应的模板名称
     */
    @TableField("sub_tpl_id")
    private String subTplId;

    /**
     * 进程总状态：0：未开始 1：执行中 2：已完成 3：失败 4：回滚中 5：已回滚 6：回滚失败 7: 跳过
     */
    @TableField("state")
    private String state;

    /**
     * 失败code
     */
    @TableField("error_code")
    private Integer errorCode;

    /**
     * 失败类型：-1：无异常 0：业务异常 1：引擎异常 2：未知异常
     */
    @TableField("error_type")
    private Integer errorType;

    /**
     * 堆栈信息
     */
    @TableField("error_stack")
    private String errorStack;

    /**
     * 当前子任务所属父任务
     */
    @TableField("process_id")
    private String processId;

    /**
     * 执行方向
     */
    @TableField("sort")
    private String sort;

    /**
     * 执行的方法名称
     */
    @TableField("method_name")
    private String methodName;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;



}
