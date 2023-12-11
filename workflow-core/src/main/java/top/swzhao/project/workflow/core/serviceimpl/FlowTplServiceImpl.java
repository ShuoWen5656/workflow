package top.swzhao.project.workflow.core.serviceimpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.swzhao.project.workflow.common.mapper.FlowTplMapper;
import top.swzhao.project.workflow.common.model.bo.OperResult;
import top.swzhao.project.workflow.common.model.po.FlowTpl;
import top.swzhao.project.workflow.common.service.FlowTplService;

/**
 * @author swzhao
 * @date 2023/12/11 9:19 下午
 * @Discreption <>
 */
@Service
@Slf4j
public class FlowTplServiceImpl  implements FlowTplService {


    @Autowired
    private FlowTplMapper flowTplMapper;



    @Override
    public OperResult<FlowTpl> getTplById(String id) {
        try {
            FlowTpl flowTpl = flowTplMapper.selectById(id);
            return new OperResult<>(OperResult.OPT_SUCCESS, "获取成功", flowTpl);
        }catch (Exception e) {
            log.error(getClass().getSimpleName().concat(".").concat(Thread.currentThread().getStackTrace()[0].getClassName()).concat(" 异常，入参id:{}， 原因：{}"), id, e.getMessage(), e);
            return new OperResult(OperResult.OPT_FAIL, "操作异常");
        }
    }
}
