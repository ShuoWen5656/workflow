package top.swzhao.project.workflow.core.serviceimpl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import top.swzhao.project.workflow.common.contants.FlowKvConstants;
import top.swzhao.project.workflow.common.mapper.DdlMapper;
import top.swzhao.project.workflow.common.model.bo.OperResult;
import top.swzhao.project.workflow.common.service.DdlService;
import top.swzhao.project.workflow.core.utils.SpringUtils;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.util.Objects;

/**
 * @author swzhao
 * @date 2023/12/2 1:32 下午
 * @Discreption <> 表结构相关服务
 */
@Service
@Slf4j
public class DdlServiceImpl implements DdlService{

    @Autowired
    DdlMapper ddlMapper;

    @Autowired
    SpringUtils springUtils;

    @Override
    public OperResult<Boolean> isTableExist(String tableName, String databaseName) {
        try {
            if (StringUtils.isBlank(tableName)) {
                return new OperResult<>(OperResult.OPT_FAIL, "tableName为空");
            }
            int tableExist = ddlMapper.isTableExist(tableName, databaseName);
            return new OperResult(OperResult.OPT_SUCCESS, "获取成功", tableExist > FlowKvConstants.NUM_VALUE_O);
        }catch (Exception e) {
            log.error(getClass().getSimpleName().concat(".").concat(Thread.currentThread().getStackTrace()[0].getClassName()).concat(" 失败，入参tableName:{}, databaseName:{}, 返回值：{}"), tableName, databaseName, e);
            return new OperResult<>(OperResult.OPT_FAIL, "获取异常", false);
        }
    }

    @Override
    public OperResult createTableByName(String tableName) {
        Reader reader = null;
        ScriptRunner scriptRunner = null;
        try {
            // 拿出来当前环境的数据源连接池
            ApplicationContext applicationContext = springUtils.getApplicationContext();
            DataSource dataSource = (DataSource) applicationContext.getBean(FlowKvConstants.STR_KEY_DATASOURCE_FLOW);
            Connection connection = dataSource.getConnection();
            // 将连接交给ibatis的脚本执行器
            scriptRunner = new ScriptRunner(connection);
            // 将要执行的脚本加载到内存
            String fileName = String.format(FlowKvConstants.STR_CONTAIN_CONSTANT, tableName);
            reader = Resources.getResourceAsReader(fileName);
            // 执行脚本
            scriptRunner.runScript(reader);
            // 释放资源
            scriptRunner.closeConnection();
            reader.close();
        }catch (Exception e) {
            log.error(getClass().getSimpleName().concat(".").concat(Thread.currentThread().getStackTrace()[0].getClassName()).concat(" 异常，入参tableName:{}， 原因：{}"), tableName, e.getMessage(), e);
            return new OperResult(OperResult.OPT_FAIL, "创建失败");
        }finally {
            try {
                // 关闭流
                if (Objects.nonNull(reader)) {
                    reader.close();
                }
                // 关闭流
                if (Objects.nonNull(scriptRunner)) {
                    scriptRunner.closeConnection();
                }
            }catch (IOException e) {
                log.error(getClass().getSimpleName().concat(".").concat(Thread.currentThread().getStackTrace()[0].getClassName()).concat(" 流关闭异常"), e);
            }
        }
        return new OperResult(OperResult.OPT_SUCCESS, "执行成功");
    }




}
