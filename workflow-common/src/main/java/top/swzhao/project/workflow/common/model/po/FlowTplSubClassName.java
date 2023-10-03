package top.swzhao.project.workflow.common.model.po;

import com.baomidou.mybatisplus.annotation.*;

import java.sql.Date;

/**
 * @author swzhao
 * @data 2023/10/3 17:05
 * @Discreption <>
 */
@TableName("FLOW_tpl_sub_class_name")
public class FlowTplSubClassName {

    /**
     * 主键 - id
     */
    @TableId(type = IdType.INPUT)
    @TableField("id")
    private String id;

    /**
     * 模板id
     */
    @TableField("tbl_id")
    private String tplId;

    /**
     * 子任务对应类名
     */
    @TableField("sub_class_name")
    private String subClassName;

    /**
     * 子模板在模板中的运行顺序
     */
    @TableField("sort")
    private Integer sort;

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
