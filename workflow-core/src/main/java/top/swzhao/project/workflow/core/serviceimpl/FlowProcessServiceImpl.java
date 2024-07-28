package top.swzhao.project.workflow.core.serviceimpl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.swzhao.project.workflow.common.contants.FlowKvConstants;
import top.swzhao.project.workflow.common.mapper.FlowProcessMapper;
import top.swzhao.project.workflow.common.model.bo.OperResult;
import top.swzhao.project.workflow.common.model.po.FlowProcess;
import top.swzhao.project.workflow.common.service.FlowProcessService;

/**
 * @author swzhao
 * @date 2023/12/11 8:15 下午
 * @Discreption <>
 */
@Service
@Slf4j
public class FlowProcessServiceImpl implements FlowProcessService {

    @Autowired
    FlowProcessMapper flowProcessMapper;


    @Override
    public OperResult<FlowProcess> getById(String id) {
        try {
            if (StringUtils.isBlank(id)) {
                return new OperResult<>(OperResult.OPT_FAIL, "id为空");
            }
            FlowProcess flowProcess = flowProcessMapper.selectById(id);
            if (flowProcess == null) {
                return new OperResult<>(OperResult.OPT_FAIL, "获取process为null");
            }
            return new OperResult<>(OperResult.OPT_SUCCESS, "获取成功", flowProcess);
        }catch (Exception e) {
            log.error(getClass().getSimpleName().concat(".").concat(Thread.currentThread().getStackTrace()[0].getClassName()).concat(" 异常，入参id:{}， 原因：{}"), id, e.getMessage(), e);
            return new OperResult<>(OperResult.OPT_FAIL, "获取异常");
        }
    }

    @Override
    public OperResult createProcess(FlowProcess flowProcess) {
        try {
            if (flowProcess == null) {
                return new OperResult(OperResult.OPT_FAIL, "创建对象为空");
            }
            int insert = flowProcessMapper.insert(flowProcess);
            if (insert < FlowKvConstants.NUM_VALUE_I) {
                return new OperResult(OperResult.OPT_FAIL, "创建流程失败，变更记录数为0");
            }
            return new OperResult(OperResult.OPT_SUCCESS, "创建成功");
        }catch (Exception e) {
            log.error(getClass().getSimpleName().concat(".").concat(Thread.currentThread().getStackTrace()[0].getClassName()).concat(" 异常，入参flowProcess:{}， 原因：{}"), flowProcess, e.getMessage(), e);
            return new OperResult(OperResult.OPT_FAIL, "创建流程异常");
        }
    }

    @Override
    public OperResult deleteById(String processId) {
        try {
            if (StringUtils.isBlank(processId)) {
                return new OperResult(OperResult.OPT_FAIL, "id为空");
            }
            flowProcessMapper.deleteById(processId);
            return new OperResult(OperResult.OPT_SUCCESS, "删除流程成功");
        }catch (Exception e) {
            log.error(getClass().getSimpleName().concat(".").concat(Thread.currentThread().getStackTrace()[0].getClassName()).concat(" 异常，入参processId:{}， 原因：{}"), processId, e.getMessage(), e);
            return new OperResult(OperResult.OPT_FAIL, "删除流程异常");
        }
    }

    @Override
    public OperResult update(FlowProcess flowProcess) {
        try {
            if (StringUtils.isBlank(flowProcess.getId())) {
                return new OperResult(OperResult.OPT_FAIL, "更新失败，id为空");
            }
            int num = flowProcessMapper.updateById(flowProcess);
            log.info("更新成功, 条数：{}", num);
            return new OperResult(OperResult.OPT_SUCCESS, "更新成功");
        }catch (Exception e) {
            log.error(getClass().getSimpleName().concat(".").concat(Thread.currentThread().getStackTrace()[0].getClassName()).concat(" 异常，入参flowProcess:{}， 原因：{}"), flowProcess, e.getMessage(), e);
            return new OperResult(OperResult.OPT_FAIL, "更新流程异常");
        }
    }
}
