package top.swzhao.project.workflow.core.serviceimpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.swzhao.project.workflow.common.contants.FlowKvConstants;
import top.swzhao.project.workflow.common.mapper.FlowSubProcessMapper;
import top.swzhao.project.workflow.common.model.bo.OperResult;
import top.swzhao.project.workflow.common.model.po.FlowSubProcess;
import top.swzhao.project.workflow.common.service.FlowSubProcessService;

import java.util.Collection;
import java.util.List;

/**
 * @author swzhao
 * @date 2023/12/11 9:01 下午
 * @Discreption <>
 */
@Service
@Slf4j
public class FlowSubProcessServiceImpl extends ServiceImpl<FlowSubProcessMapper, FlowSubProcess> implements FlowSubProcessService {

    @Autowired
    private FlowSubProcessMapper flowSubProcessMapper;



    @Override
    public OperResult batchCreate(List<? extends FlowSubProcess> flowSubProcesses) {
        try {
            boolean batchCreate = saveBatch((Collection<FlowSubProcess>) flowSubProcesses);
            if (!batchCreate) {
                return new OperResult(OperResult.OPT_FAIL, "批量创建子流程失败");
            }
            return new OperResult(OperResult.OPT_SUCCESS, "批量创建成功");
        }catch (Exception e) {
            log.error(getClass().getSimpleName().concat(".").concat(Thread.currentThread().getStackTrace()[0].getClassName()).concat(" 异常，入参flowSubProcesses:{}， 原因：{}"), flowSubProcesses, e.getMessage(), e);
            return new OperResult(OperResult.OPT_FAIL, "批量创建子流程异常");
        }
    }

    @Override
    public OperResult deleteByProcessId(String processId) {
        try {
            if (StringUtils.isBlank(processId)) {
                return new OperResult(OperResult.OPT_FAIL, "id为空");
            }
            QueryWrapper<FlowSubProcess> flowSubProcessQueryWrapper = new QueryWrapper<>();
            flowSubProcessQueryWrapper.lambda().eq(FlowSubProcess::getId, processId);
            flowSubProcessMapper.delete(flowSubProcessQueryWrapper);
            return new OperResult(OperResult.OPT_SUCCESS, "删除子流程完成");
        }catch (Exception e) {
            log.error(getClass().getSimpleName().concat(".").concat(Thread.currentThread().getStackTrace()[0].getClassName()).concat(" 异常，入参processId:{}， 原因：{}"), processId, e.getMessage(), e);
            return new OperResult(OperResult.OPT_FAIL, "批量创建子流程异常");
        }
    }

    @Override
    public OperResult batchUpdate(List<FlowSubProcess> flowSubProcesses) {
        try {
            boolean batchByIdResult = updateBatchById(flowSubProcesses);
            if (!batchByIdResult) {
                log.error(getClass().getSimpleName().concat(".").concat(Thread.currentThread().getStackTrace()[0].getClassName()).concat(" 失败，入参flowSubProcesses:{}， 原因：{}"), flowSubProcesses);
                return new OperResult(OperResult.OPT_FAIL, "更新失败");
            }
            return new OperResult(OperResult.OPT_SUCCESS, "更新成功");
        }catch (Exception e) {
            log.error(getClass().getSimpleName().concat(".").concat(Thread.currentThread().getStackTrace()[0].getClassName()).concat(" 异常，入参flowSubProcesses:{}， 原因：{}"), flowSubProcesses, e.getMessage(), e);
            return new OperResult(OperResult.OPT_FAIL, "批量更新子流程异常");
        }
    }

    @Override
    public OperResult<List<FlowSubProcess>> listSubProcessByCondition(FlowSubProcess condition, String order) {
        try {
            QueryWrapper<FlowSubProcess> flowSubProcessQueryWrapper = new QueryWrapper<>();
            if (StringUtils.equals(order, FlowKvConstants.STR_KEY_DESC)) {
                flowSubProcessQueryWrapper.orderByDesc(FlowKvConstants.STR_KEY_SORT);
            }else {
                flowSubProcessQueryWrapper.orderByAsc(FlowKvConstants.STR_KEY_SORT);
            }
            List<FlowSubProcess> flowSubProcesses = flowSubProcessMapper.selectList(flowSubProcessQueryWrapper);
            if (CollectionUtils.isEmpty(flowSubProcesses)) {
                return new OperResult<>(OperResult.OPT_FAIL, "获取结果为null");
            }
            return new OperResult<>(OperResult.OPT_SUCCESS, "获取成功", flowSubProcesses);
        }catch (Exception e) {
            log.error(getClass().getSimpleName().concat(".").concat(Thread.currentThread().getStackTrace()[0].getClassName()).concat(" 异常，入参condition:{}，order:{}, 原因：{}"), condition, order, e.getMessage(), e);
            return new OperResult<>(OperResult.OPT_FAIL, "查询子流程异常");
        }
    }
}
