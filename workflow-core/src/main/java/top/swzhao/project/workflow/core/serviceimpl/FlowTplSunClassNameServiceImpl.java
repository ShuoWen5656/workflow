package top.swzhao.project.workflow.core.serviceimpl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.swzhao.project.workflow.common.mapper.FlowTplSubClassNameMapper;
import top.swzhao.project.workflow.common.model.bo.OperResult;
import top.swzhao.project.workflow.common.model.dto.FlowSubTplDto;
import top.swzhao.project.workflow.common.model.po.FlowTplSubClassName;
import top.swzhao.project.workflow.common.service.FlowTplSubClassNameService;

import java.util.ArrayList;
import java.util.List;

/**
 * @author swzhao
 * @date 2023/12/11 9:21 下午
 * @Discreption <>
 */
@Service
@Slf4j
public class FlowTplSunClassNameServiceImpl implements FlowTplSubClassNameService {

    @Autowired
    private FlowTplSubClassNameMapper flowTplSubClassNameMapper;


    @Override
    public OperResult<List<FlowSubTplDto>> listFlowSubTplByTplId(String tplId) {
        try {
            if (StringUtils.isBlank(tplId)) {
                return new OperResult<>(OperResult.OPT_FAIL, "获取失败， tplId为空");
            }
            List<FlowSubTplDto> flowSubTplDtos = flowTplSubClassNameMapper.listFlowSubTplByTplId(tplId);
            if (CollectionUtils.isEmpty(flowSubTplDtos)) {
                return new OperResult<>(OperResult.OPT_FAIL, "获取失败", new ArrayList<>());
            }
            return new OperResult<>(OperResult.OPT_SUCCESS, "获取成功", flowSubTplDtos);
        } catch (Exception e) {
            log.error(getClass().getSimpleName().concat(".").concat(Thread.currentThread().getStackTrace()[0].getClassName()).concat(" 异常，入参tplId:{}， 原因：{}"), tplId, e.getMessage(), e);
            return new OperResult<>(OperResult.OPT_FAIL, "获取异常");
        }
    }
}
