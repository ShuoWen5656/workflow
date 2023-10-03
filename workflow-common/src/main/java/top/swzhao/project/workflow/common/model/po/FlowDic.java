package top.swzhao.project.workflow.common.model.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.ToString;

import java.sql.Date;

/**
 * @author swzhao
 * @data 2023/10/2 23:14
 * @Discreption <>
 */
@Data
@ToString
@TableName("FLOW_dic")
public class FlowDic {

    /**
     * 主键
     */
    @TableId(type = IdType.INPUT)
    private String id;

    /**
     * 类型
     */
    @TableField("type")
    private String type;

    /**
     * key
     * 这里使用了数据库的关键字，所以需要添加``避开
     */
    @TableField("`key`")
    private String key;

    /**
     * value
     */
    @TableField("value")
    private String value;

    /**
     * value - 国际化
     */
    @TableField("en_value")
    private String enValue;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;

    /**
     * 修改时间
     */
    @TableField("update_time")
    private Date updateTime;

}
