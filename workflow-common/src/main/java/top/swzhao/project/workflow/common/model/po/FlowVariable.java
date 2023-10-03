package top.swzhao.project.workflow.common.model.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.sql.Date;

/**
 * @author swzhao
 * @data 2023/10/3 17:13
 * @Discreption <>
 */
@TableName("FLOW_variable")
@Data
public class FlowVariable {


    /**
     * 主键 - id
     */
    @TableId(type = IdType.INPUT)
    @TableField("id")
    private String id;

    /**
     * 变量定义名称
     */
    @TableField("name")
    private String name;

    /**
     * 变量的类全称
     */
    @TableField("class_type")
    private String classType;

    /**
     * 变量内容
     */
    @TableField("variable_content")
    private String variableContent;

    /**
     * 变量类型：0：全局变量 1：临时变量 2：回滚变量（全局） ：回滚变量（临时）
     */
    @TableField("type")
    private Integer type;

    /**
     * 所属任务
     */
    @TableField("process_id")
    private String processId;

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
