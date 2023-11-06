package top.swzhao.project.workflow.common.service;

import top.swzhao.project.workflow.common.model.bo.OperResult;

/**
 * @author swzhao
 * @date 2023/11/6 9:29 下午
 * @Discreption <>
 */
public interface DdlService {

    /**
     * 判断数据库中是否存在该表
     * @param tableName
     * @param databaseName
     * @return
     */
    OperResult<Boolean> isTableExist(String tableName, String databaseName);


    /**
     * 创建某个表
     * 1、根据表明去resource中对应的创建sql文件，并使用{@link org.apache.ibatis.jdbc.ScriptRunner} 执行
     * @param tableName
     * @return
     */
    OperResult createTableByName(String tableName);

}
