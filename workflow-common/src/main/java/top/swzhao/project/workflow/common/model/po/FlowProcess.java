package top.swzhao.project.workflow.common.model.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.ToString;

import java.sql.Date;

/**
 * @author swzhao
 * @data 2023/10/3 16:30
 * @Discreption <>
 */
@Data
@ToString
@TableName("FLOW_process")
public class FlowProcess {

    public FlowProcess() {
    }


    public FlowProcess(String id, String name, String tplId, String state) {
        this.id = id;
        this.name = name;
        this.tplId = tplId;
        this.state = state;
    }

    public FlowProcess(String id, String state, String curSubId) {
        this.id = id;
        this.state = state;
        this.curSubId = curSubId;
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
    @TableField("tpl_id")
    private String tplId;

    /**
     * 进程总状态：0：未开始 1：执行中 2：已完成 3：失败 4：回滚中 5：已回滚 6：回滚失败
     */
    @TableField("state")
    private String state;

    /**
     * 当前运行到哪个子任务的id
     * 策略：新增时如果字段是null或者“”， sql语句都不会加该字段，库中使用默认值
     */
    @TableField(value = "cur_sub_id", insertStrategy = FieldStrategy.NOT_NULL)
    private String curSubId;

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
