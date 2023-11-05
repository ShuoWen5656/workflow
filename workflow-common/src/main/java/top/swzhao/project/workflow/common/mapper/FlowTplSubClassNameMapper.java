package top.swzhao.project.workflow.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import top.swzhao.project.workflow.common.model.dto.FlowSubTplDto;
import top.swzhao.project.workflow.common.model.po.FlowTplSubClassName;

import java.util.List;

/**
 * @author swzhao
 * @date 2023/11/5 10:54 下午
 * @Discreption <>
 */
@Mapper
public interface FlowTplSubClassNameMapper extends BaseMapper<FlowTplSubClassName> {


    /**
     * 根据模板id获取当前模板下配置好的子任务模板列表
     * @param tplId
     * @return
     */
    @Select("select u2.id as id, name, type, u2.class_name as ClassName, description, u2.create_time as createTime, u2.update_time as updateTime, u1.sort \n" +
            "from FLOW_tpl_sub_class_name u1 inner join FLOW_sub_tpl u2 on u1.sub_class_name = u2.class_name \n" +
            "where u1.tpl_id = #{tplId}\n")
    List<FlowSubTplDto> listFlowSubTplByTplId(@Param("tplId") String tplId);
}
