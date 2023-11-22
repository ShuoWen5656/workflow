package top.swzhao.project.workflow.common.model.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.sql.Date;

/**
 * @author swzhao
 * @data 2023/10/3 16:46
 * @Discreption <>
 */
@Data
@TableName("FLOW_sub_tpl")
public class FlowSubTpl {


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
     * 子任务模板类型
     */
    @TableField("type")
    private Integer type;


    /**
     * 子任务模板对应的className，全限定名
     * 这个类名是唯一键，因为id随机生成，因此用户将模板和子模板关联的逻辑为 模板id - 子模板className
     */
    @TableField("class_name")
    private String className;

    /**
     * 描述
     */
    @TableField("description")
    private String description;

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
