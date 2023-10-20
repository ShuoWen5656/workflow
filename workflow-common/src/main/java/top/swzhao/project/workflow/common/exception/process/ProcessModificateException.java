package top.swzhao.project.workflow.common.exception.process;

import top.swzhao.project.workflow.common.exception.EngineException;
import top.swzhao.project.workflow.common.model.dto.FlowSubProcessSortDto;
import top.swzhao.project.workflow.common.model.po.FlowProcess;

import java.util.List;

/**
 * @author swzhao
 * @date 2023/10/20 9:24 下午
 * @Discreption <>
 */
public class ProcessModificateException extends EngineException {
    public ProcessModificateException(Integer errorCode) {
        super(errorCode);
    }

    public ProcessModificateException(Integer errorCode, String message) {
        super(errorCode, message);
    }

    public ProcessModificateException(Integer errorCode, FlowProcess flowProcess, List<FlowSubProcessSortDto> flowSubProcessSortDtos) {
        super(errorCode, flowProcess, flowSubProcessSortDtos);
    }
}
