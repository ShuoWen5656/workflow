package top.swzhao.project.workflow.common.exception.variable;

import top.swzhao.project.workflow.common.exception.EngineException;
import top.swzhao.project.workflow.common.model.dto.FlowSubProcessSortDto;
import top.swzhao.project.workflow.common.model.po.FlowProcess;

import java.util.List;

/**
 * @author swzhao
 * @date 2023/10/20 9:28 下午
 * @Discreption <>
 */
public class VariableException extends EngineException {
    public VariableException(Integer errorCode) {
        super(errorCode);
    }

    public VariableException(Integer errorCode, String message) {
        super(errorCode, message);
    }

    public VariableException(Integer errorCode, FlowProcess flowProcess, List<FlowSubProcessSortDto> flowSubProcessSortDtos) {
        super(errorCode, "", flowProcess, flowSubProcessSortDtos);
    }
}
