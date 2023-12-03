package top.swzhao.project.workflow.common;

import top.swzhao.project.workflow.common.model.bo.GlobalFlowParam;
import top.swzhao.project.workflow.common.model.bo.TempFlowParam;

/**
 * @author swzhao
 * @date 2023/11/6 9:26 下午
 * @Discreption <> 工作流模板类
 * 注意：凡是继承了该接口的类都会被引擎启动时记录在数据库的FLOW_sub_tpl 表中
 */
public interface OmpFlowable {


    /**
     * 启动
     * @param tempFlowParam
     * @param globalFlowParam
     */
    void start(TempFlowParam tempFlowParam, GlobalFlowParam globalFlowParam);

    /**
     * 回滚
     * @param tempFlowParam
     * @param globalFlowParam
     */
    void rollback(TempFlowParam tempFlowParam, GlobalFlowParam globalFlowParam);

    /**
     * 执行之后的资源回收等操作(可选)
     * @param tempFlowParam
     * @param globalFlowParam
     */
    default void afterStart(TempFlowParam tempFlowParam, GlobalFlowParam globalFlowParam){}

    /**
     * 执行之前数据准备(可选)
     * @param tempFlowParam
     * @param globalFlowParam
     */
    default void preStart(TempFlowParam tempFlowParam, GlobalFlowParam globalFlowParam){}

}
