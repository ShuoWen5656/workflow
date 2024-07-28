package top.swzhao.project.workflow.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import top.swzhao.project.workflow.common.model.po.FlowSubTpl;

import java.util.List;

/**
 * @author swzhao
 * @date 2023/11/5 10:46 下午
 * @Discreption <>
 */
@Mapper
public interface FlowSubTplMapper extends BaseMapper<FlowSubTpl> {

    @Insert("<script>" +
            "INSERT INTO FLOW_sub_tpl(id, name, type, class_name, description) \n" +
            "VALUES " +
            "<foreach collection='flowSubTpls' item='flowSubTpl' index='index' separator=','>" +
            "(UUID(), #{flowSubTpl.name}, #{flowSubTpl.type}, #{flowSubTpl.className}, '${flowSubTpl.description}')" +
            "</foreach>" +
            "ON DUPLICATE KEY UPDATE \n" +
            "name = values(name), type = values(type), description = values(description), update_time = values(update_time) \n" +
            "</script>")
    void insertOrUpdate(@Param("flowSubTpls")List<FlowSubTpl> flowSubTpls);

}
