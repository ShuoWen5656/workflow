package top.swzhao.project.workflow.common.exception.variable;

import top.swzhao.project.workflow.common.model.dto.FlowSubProcessSortDto;
import top.swzhao.project.workflow.common.model.po.FlowProcess;

import java.util.List;

/**
 * @author swzhao
 * @date 2023/10/20 9:29 下午
 * @Discreption <>
 */
public class VariableQueryException extends VariableException {
    public VariableQueryException(Integer errorCode) {
        super(errorCode);
    }

    public VariableQueryException(Integer errorCode, String message) {
        super(errorCode, message);
    }

    public VariableQueryException(Integer errorCode, FlowProcess flowProcess, List<FlowSubProcessSortDto> flowSubProcessSortDtos) {
        super(errorCode, flowProcess, flowSubProcessSortDtos);
    }
}
