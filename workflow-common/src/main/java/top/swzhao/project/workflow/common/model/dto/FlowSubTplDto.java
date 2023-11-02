package top.swzhao.project.workflow.common.model.dto;

import lombok.Data;
import top.swzhao.project.workflow.common.model.po.FlowSubTpl;

/**
 * @author swzhao
 * @data 2023/10/3 17:38
 * @Discreption <> 当前执行模板的增强
 */
@Data
public class FlowSubTplDto extends FlowSubTpl {

    /**
     * 执行顺序
     */
    private Integer sort;
}
