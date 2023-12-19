package top.swzhao.project.workflow.common.exception.variable;

import top.swzhao.project.workflow.common.model.dto.FlowSubProcessSortDto;
import top.swzhao.project.workflow.common.model.po.FlowProcess;

import java.util.List;

/**
 * @author swzhao
 * @date 2023/10/20 9:28 下午
 * @Discreption <>
 */
public class VariableCreateException extends VariableException  {
    public VariableCreateException(Integer errorCode, String 变量入库失败, FlowProcess flowProcess, List<FlowSubProcessSortDto> flowSubProcessSortDtos) {
        super(errorCode);
    }

    public VariableCreateException(Integer errorCode, String message) {
        super(errorCode, message);
    }

    public VariableCreateException(Integer errorCode, FlowProcess flowProcess, List<FlowSubProcessSortDto> flowSubProcessSortDtos) {
        super(errorCode, flowProcess, flowSubProcessSortDtos);
    }
}
