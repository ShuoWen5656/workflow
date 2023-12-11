package top.swzhao.project.workflow.core.serviceimpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.swzhao.project.workflow.common.mapper.FlowVariableMapper;
import top.swzhao.project.workflow.common.model.bo.OperResult;
import top.swzhao.project.workflow.common.model.po.FlowVariable;
import top.swzhao.project.workflow.common.service.FlowVariableService;

import java.util.List;

/**
 * @author swzhao
 * @date 2023/12/11 9:26 下午
 * @Discreption <>
 */
@Service
@Slf4j
public class FlowVariableServiceImpl extends ServiceImpl<FlowVariableMapper, FlowVariable> implements FlowVariableService {

    @Autowired
    private FlowVariableMapper flowVariableMapper;


    @Override
    public OperResult batchCreate(List<FlowVariable> flowVariableList) {
        try {
            if (CollectionUtils.isEmpty(flowVariableList)) {
                return new OperResult(OperResult.OPT_SUCCESS, "批量常见成功");
            }
            flowVariableMapper.insertOrUpdate(flowVariableList);
            return new OperResult(OperResult.OPT_SUCCESS, "批量创建成功");
        }catch (Exception e) {
            log.error(getClass().getSimpleName().concat(".").concat(Thread.currentThread().getStackTrace()[0].getClassName()).concat(" 异常，入参flowVariableList:{}， 原因：{}"), flowVariableList, e.getMessage(), e);
            return new OperResult<>(OperResult.OPT_FAIL, "批量创建异常");
        }
    }

    @Override
    public OperResult<List<FlowVariable>> batchListByCondition(FlowVariable condition) {
        try {
            QueryWrapper<FlowVariable> flowVariableQueryWrapper = new QueryWrapper<>();
            List<FlowVariable> flowVariables = flowVariableMapper.selectList(flowVariableQueryWrapper);
            if (flowVariables == null) {
                log.error(getClass().getSimpleName().concat(".").concat(Thread.currentThread().getStackTrace()[0].getClassName()).concat(" 失败，入参condition:{}， 原因：{}"), condition);
                return new OperResult<>(OperResult.OPT_FAIL, "查询失败");
            }
            return new OperResult<>(OperResult.OPT_SUCCESS, "查询成功", flowVariables);
        }catch (Exception e) {
            log.error(getClass().getSimpleName().concat(".").concat(Thread.currentThread().getStackTrace()[0].getClassName()).concat(" 异常，入参condition:{}， 原因：{}"), condition, e.getMessage(), e);
            return new OperResult<>(OperResult.OPT_FAIL, "查询异常");
        }
    }
}
