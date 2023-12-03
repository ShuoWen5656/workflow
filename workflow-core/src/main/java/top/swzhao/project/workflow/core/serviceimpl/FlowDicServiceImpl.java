package top.swzhao.project.workflow.core.serviceimpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import top.swzhao.project.workflow.common.mapper.FlowDicMapper;
import top.swzhao.project.workflow.common.model.bo.OperResult;
import top.swzhao.project.workflow.common.model.po.FlowDic;
import top.swzhao.project.workflow.common.service.FlowDicService;

import java.util.ArrayList;
import java.util.List;

/**
 * @author swzhao
 * @date 2023/12/3 4:09 下午
 * @Discreption <>
 */
@Service
@Slf4j
public class FlowDicServiceImpl implements FlowDicService {

    FlowDicMapper flowDicMapper;


    @Override
    public OperResult<List<FlowDic>> listDicByType(String type) {
        try {
            if (StringUtils.isBlank(type)) {
                return new OperResult<>(OperResult.OPT_FAIL, String.format("type为s%", type));
            }
            QueryWrapper<FlowDic> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(FlowDic::getType, type);
            List<FlowDic> flowDics = flowDicMapper.selectList(queryWrapper);
            if (CollectionUtils.isEmpty(flowDics)) {
                return new OperResult<>(OperResult.OPT_SUCCESS, "获取成功", new ArrayList<>());
            }
            return new OperResult<>(OperResult.OPT_SUCCESS, "获取成功", flowDics);
        }catch (Exception e) {
            log.error(getClass().getSimpleName().concat(".").concat(Thread.currentThread().getStackTrace()[0].getClassName()).concat(" 异常， 入参:type:{}"), type, e);
            return new OperResult<>(OperResult.OPT_FAIL, "获取异常");
        }
    }
}
