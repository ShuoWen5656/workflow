package top.swzhao.project.workflow.core.serviceimpl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.swzhao.project.workflow.common.mapper.FlowSubProcessMapper;
import top.swzhao.project.workflow.common.mapper.FlowSubTplMapper;
import top.swzhao.project.workflow.common.model.bo.OperResult;
import top.swzhao.project.workflow.common.model.po.FlowSubTpl;
import top.swzhao.project.workflow.common.service.FlowSubTplService;

import java.util.List;

/**
 * @author swzhao
 * @date 2023/12/11 9:16 下午
 * @Discreption <>
 */
@Service
@Slf4j
public class FlowSubTplServiceImpl implements FlowSubTplService {

    @Autowired
    FlowSubTplMapper flowSubTplMapper;


    @Override
    public OperResult insertOrUpdate(List<FlowSubTpl> flowSubTpls) {
        try {
            if (CollectionUtils.isEmpty(flowSubTpls)) {
                return new OperResult(OperResult.OPT_FAIL, "操作成功");
            }
            flowSubTplMapper.insertOrUpdate(flowSubTpls);
            return new OperResult(OperResult.OPT_SUCCESS, "操作成功");
        }catch (Exception e) {
            log.error(getClass().getSimpleName().concat(".").concat(Thread.currentThread().getStackTrace()[0].getClassName()).concat(" 异常，入参flowSubTpls:{}， 原因：{}"), flowSubTpls, e.getMessage(), e);
            return new OperResult(OperResult.OPT_FAIL, "操作异常");
        }
    }
}
