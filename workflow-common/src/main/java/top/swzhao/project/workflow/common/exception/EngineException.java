package top.swzhao.project.workflow.common.exception;

import lombok.Data;
import top.swzhao.project.workflow.common.model.dto.FlowSubProcessSortDto;
import top.swzhao.project.workflow.common.model.po.FlowProcess;

import java.util.List;

/**
 * @author swzhao
 * @date 2023/10/19 10:30 下午
 * @Discreption <> 引擎自己使用的RuntimeException，线程运行和初始化时抛出
 */
@Data
public class EngineException  extends RuntimeException{

    private Integer errorCode;

    private FlowProcess flowProcess;

    private List<FlowSubProcessSortDto> flowSubProcessSortDtos;

    public EngineException(Integer errorCode) {
        super(ErrorCodes.convert2Msg(errorCode));
        this.errorCode = errorCode;
    }

    public EngineException(Integer errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public EngineException(Integer errorCode, String msg, FlowProcess flowProcess, List<FlowSubProcessSortDto> flowSubProcessSortDtos) {
        super(ErrorCodes.convert2Msg(errorCode).concat(msg));
        this.errorCode = errorCode;
        this.flowProcess = flowProcess;
        this.flowSubProcessSortDtos = flowSubProcessSortDtos;
    }




}
