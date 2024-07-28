package top.swzhao.project.workflow.core.handleimpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import top.swzhao.project.workflow.common.exception.ErrorCodes;
import top.swzhao.project.workflow.common.exception.OmpFlowException;
import top.swzhao.project.workflow.common.exception.process.ProcessModificateException;
import top.swzhao.project.workflow.common.exception.variable.VariableQueryException;
import top.swzhao.project.workflow.common.handler.FlowHandler;
import top.swzhao.project.workflow.common.model.bo.FlowParam;
import top.swzhao.project.workflow.common.model.bo.OperResult;
import top.swzhao.project.workflow.common.model.dto.FlowProcessSubProcessDto;
import top.swzhao.project.workflow.common.model.dto.FlowSubProcessSortDto;
import top.swzhao.project.workflow.common.model.po.FlowProcess;
import top.swzhao.project.workflow.common.model.po.FlowSubProcess;
import top.swzhao.project.workflow.common.model.po.FlowVariable;
import top.swzhao.project.workflow.common.service.FlowProcessService;
import top.swzhao.project.workflow.common.service.FlowSubProcessService;
import top.swzhao.project.workflow.common.service.FlowVariableService;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author swzhao
 * @date 2023/11/22 4:48 下午
 * @Discreption <> 业务处理器
 */
@Service
@Slf4j
public class FlowHandlerImpl implements FlowHandler {


    @Autowired
    private FlowProcessService flowProcessService;

    @Autowired
    private FlowSubProcessService flowSubProcessService;

    @Autowired
    private FlowVariableService flowVariableService;

    @Override
    public OperResult dealProcess(List<FlowSubProcessSortDto> flowSubProcessSortDtos, FlowProcess flowProcess) throws ProcessModificateException {
        FlowProcess processTemp = new FlowProcess(flowProcess.getId(), flowProcess.getState(), flowProcess.getCurSubId());
        processTemp.setUpdateTime(new Date(System.currentTimeMillis()));
        OperResult updateProcessResult = flowProcessService.update(processTemp);
        if (!updateProcessResult.success()) {
            log.error(getClass().getSimpleName().concat(".").concat(Thread.currentThread().getStackTrace()[0].getClassName()).concat(" 失败，入参{}, 返回值：{}"), processTemp, updateProcessResult);
            throw new ProcessModificateException(ErrorCodes.PROCESS_ERROR, "更新process失败", flowProcess, flowSubProcessSortDtos);
        }
        return updateProcessResult;
    }

    @Override
    public OperResult dealSubProcess(List<FlowSubProcessSortDto> flowSubProcessSortDtos, FlowProcess flowProcess) throws ProcessModificateException {
        List<FlowSubProcess> flowSubProcesses = new ArrayList<>();
        for (FlowSubProcessSortDto flowSubProcessSortDto : flowSubProcessSortDtos) {
            FlowSubProcess container = new FlowSubProcess();
            container.setId(flowSubProcessSortDto.getId());
            container.setState(flowSubProcessSortDto.getState());
            container.setMethodName(flowSubProcessSortDto.getMethodName());
            container.setErrorType(flowSubProcessSortDto.getErrorType());
            container.setErrorCode(flowSubProcessSortDto.getErrorCode());
            container.setErrorStack(flowSubProcessSortDto.getErrorStack());
            container.setUpdateTime(new Date(System.currentTimeMillis()));
            flowSubProcesses.add(container);
        }
        OperResult operResult = flowSubProcessService.batchUpdate(flowSubProcesses);
        if (!operResult.success()) {
            log.error(getClass().getSimpleName().concat(".").concat(Thread.currentThread().getStackTrace()[0].getClassName()).concat(" 失败，入参{}, 返回值：{}"), flowSubProcesses, operResult);
            throw new ProcessModificateException(ErrorCodes.PROCESS_ERROR, "更新subprocess失败", flowProcess, flowSubProcessSortDtos);
        }
        return operResult;
    }

    /**
     * 初始化变量
     * 1、先从库中查询出当前subprocess的变量进行初始化
     * 2、将外界传入的在进行覆盖
     * @param flowParam
     * @param variables
     * @param type
     * @param flowProcessSubProcessDto
     * @return
     * @throws OmpFlowException
     */
    @Override
    public OperResult dealFlowParam(FlowParam flowParam, Map<String, Object> variables, Integer type, FlowProcessSubProcessDto flowProcessSubProcessDto) throws OmpFlowException {
        List<FlowSubProcessSortDto> flowSubProcessSortDtoList = flowProcessSubProcessDto.getFlowSubProcessSortDtoList();
        if (!CollectionUtils.isEmpty(flowSubProcessSortDtoList)) {
            FlowSubProcessSortDto flowSubProcessSortDto = flowSubProcessSortDtoList.get(0);
            FlowVariable flowVariable = new FlowVariable();
            flowVariable.setProcessId(flowSubProcessSortDto.getProcessId());
            flowVariable.setType(type);
            OperResult<List<FlowVariable>> listOperResult = flowVariableService.batchListByCondition(flowVariable);
            if (!listOperResult.success()) {
                log.error(getClass().getSimpleName().concat(".").concat(Thread.currentThread().getStackTrace()[0].getClassName()).concat(" 失败，入参{}, 返回值：{}"), flowVariable, listOperResult);
                throw new VariableQueryException(ErrorCodes.VARIABLE_ERROR, "流程变量查询失败");
            }
            List<FlowVariable> flowVariables = listOperResult.getData();
            flowParam.setInputKV(flowVariables);
        }
        // 这种方式其实会循环两次，不推荐,推荐使用Entry
        if (!CollectionUtils.isEmpty(variables)) {
            for (String key : variables.keySet()) {
                flowParam.setInputKV(key, variables.get(key));
            }
        }
        return new OperResult(OperResult.OPT_SUCCESS, "处理成功");
    }





}
