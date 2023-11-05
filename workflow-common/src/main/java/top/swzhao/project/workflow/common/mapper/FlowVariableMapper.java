package top.swzhao.project.workflow.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import top.swzhao.project.workflow.common.model.po.FlowVariable;

import java.util.List;

/**
 * @author swzhao
 * @date 2023/11/5 11:00 下午
 * @Discreption <>
 */
@Mapper
public interface FlowVariableMapper extends BaseMapper<FlowVariable> {

    /**
     * 变量创建或更新
     * @param flowVariables
     * @return
     */
    @Insert("<script>" +
            "INSERT INTO FLOW_variable (id, name, class_type, variable_content, type, process_id) \n" +
            "VALUES " +
            "<foreach collection='flowVariables' item='flowVariable' index='index' separator=','>" +
            "(UUID(), #{flowVariable.name}, #{flowVariable.classType}, #{flowVariable.variableContent}, #{flowVariable.type}, #{flowVariable.processId})" +
            "</foreach>" +
            "ON DUPLICATE KEY UPDATE \n" +
            "class_type = values(class_type), variable_content = values(variable_content), update_time = NOW() \n" +
            "</script>")
    int insertOrUpdate(@Param("flowVariables") List<FlowVariable> flowVariables);
}
