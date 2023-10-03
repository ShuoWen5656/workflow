package top.swzhao.project.workflow.common.model.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.sql.Date;

/**
 * @author swzhao
 * @data 2023/10/3 16:53
 * @Discreption <>
 */
@Data
@TableName("FLOW_tpl")
public class FlowTpl {

    /**
     * 主键 - id
     */
    @TableId(type = IdType.INPUT)
    @TableField("id")
    private String id;

    /**
     * 模板唯一名称（需要有意义）
     */
    @TableField("tbl_name")
    private String tplName;

    /**
     * 模板名称 - 用户取
     */
    @TableField("name")
    private String name;

    /**
     * 模板类型
     */
    @TableField("type")
    private Integer type;

    /**
     * 模板描述
     */
    @TableField("description")
    private String description;

    /**
     * 模板版本
     */
    @TableField("version")
    private String version;

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
