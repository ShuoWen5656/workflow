package top.swzhao.project.workflow.common.handler;

import top.swzhao.project.workflow.common.exception.OmpFlowException;
import top.swzhao.project.workflow.common.exception.process.ProcessModificateException;
import top.swzhao.project.workflow.common.model.bo.FlowParam;
import top.swzhao.project.workflow.common.model.bo.OperResult;
import top.swzhao.project.workflow.common.model.dto.FlowProcessSubProcessDto;
import top.swzhao.project.workflow.common.model.dto.FlowSubProcessSortDto;
import top.swzhao.project.workflow.common.model.po.FlowProcess;

import java.util.List;
import java.util.Map;

/**
 * @author swzhao
 * @date 2023/11/6 9:56 下午
 * @Discreption <> process相关处理器
 */
public interface FlowHandler {

    /**
     * 初始process到指定状态，当前运行子任务为指定子任务id
     * @param flowSubProcessSortDtos
     * @param flowProcess
     * @return
     * @throws ProcessModificateException
     */
    OperResult dealProcess(List<FlowSubProcessSortDto> flowSubProcessSortDtos, FlowProcess flowProcess) throws ProcessModificateException;

    /**
     * 将修改好的子任务列表传入
     * 仅支持修改 state、methodName、errorType、errorCode、errorStack
     * @param flowSubProcessSortDtos
     * @param flowProcess
     * @return
     * @throws ProcessModificateException
     */
    OperResult dealSubProcess(List<FlowSubProcessSortDto> flowSubProcessSortDtos, FlowProcess flowProcess) throws ProcessModificateException;

    /**
     * 处理变量
     * 1、从库中先取出来
     * 2、将外界的覆盖进去
     * @param flowParam
     * @param variables
     * @param type
     * @param flowProcessSubProcessDto
     * @return
     * @throws OmpFlowException
     */
    OperResult dealFlowParam(FlowParam flowParam, Map<String, Object> variables, Integer type, FlowProcessSubProcessDto flowProcessSubProcessDto) throws OmpFlowException;




}
