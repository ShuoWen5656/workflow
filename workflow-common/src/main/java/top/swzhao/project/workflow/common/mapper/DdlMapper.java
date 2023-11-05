package top.swzhao.project.workflow.common.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * @author swzhao
 * @date 2023/11/4 8:22 下午
 * @Discreption <> ！！！该mapper只能用于处理表结构，不涉及dml，因此不对外暴露
 */
@Mapper
public interface DdlMapper {


    /**
     * 判断表是否在指定的数据库中
     * @param tableName
     * @param tableSchema
     * @return
     */
    @Select("<script>" +
            "select count(1) from information_schema.TABLES" +
            "where" +
            "1 = 1" +
            "<if test='tableSchema != null and tableSchema != \"\"'" +
            "and TABLE_SCHEMA = '${tableSchema}" +
            "</if>" +
            "and TABLE_NAME = '${tableName}'" +
            "</script>")
    int isTableExist(@Param(value = "tableName") String tableName, @Param(value = "tableSchema") String tableSchema);

    /**
     * 运行查询sql
     * tips: 切记！仅内部使用
     * @param statement
     * @return
     */
    @Select("${statement}")
    List<Map<String, Object>> selectSql(String statement);


    /**
     * 运行更新sql
     * tips:切记！仅内部使用
     * @param statement
     * @return
     */
    @Select("${statement}")
    int updateSql(String statement);

}
