package top.swzhao.project.workflow.common.engine;

import top.swzhao.project.workflow.common.exception.OmpFlowException;
import top.swzhao.project.workflow.common.model.bo.OperResult;
import top.swzhao.project.workflow.common.service.*;

import java.util.Map;

/**
 * @author swzhao
 * @date 2023/11/6 8:30 下午
 * @Discreption <> 唯一暴露给客户端的引擎入口
 */
public interface OmpFlowEngine {


    /**
     * 基于某个模板id启动一个流程
     * @param tplId
     * @param variables variables中object必须存在get和set方法
     * @return
     * @throws OmpFlowException
     */
    OperResult start(String tplId, Map<String, Object> variables) throws OmpFlowException;


    /**
     * 重启某个流程，注意为清空所有子任务状态和错误以及全局变量
     * @param processId
     * @param variables
     * @return
     * @throws OmpFlowException
     */
    OperResult reStart(String processId, Map<String, Object> variables) throws OmpFlowException;


    /**
     * 重新从当前报错节点执行
     * @param processId
     * @param variables
     * @return
     * @throws OmpFlowException
     */
    OperResult reRun(String processId, Map<String, Object> variables) throws OmpFlowException;

    /**
     * 跳过当前报错节点继续执行
     * @param processId
     * @param variables
     * @return
     * @throws OmpFlowException
     */
    OperResult skipCurrentAndRun(String processId, Map<String, Object> variables) throws OmpFlowException;

    /**
     * 回滚某个子流程
     * @param processId
     * @param variables
     * @return
     * @throws OmpFlowException
     */
    OperResult rollback(String processId, Map<String, Object> variables) throws OmpFlowException;

    /**
     * 回滚某个子流程
     * @param processId
     * @param variables
     * @return
     * @throws OmpFlowException
     */
    OperResult rollbackSubProcess(String processId, Map<String, Object> variables) throws OmpFlowException;

    /*************************************************************************************
     *                            引擎资源统一提供入口                                      *
     *************************************************************************************/


    /**
     * 获取FlowTplService实例
     * @return
     */
    FlowTplService getFlowTplService();

    /**
     * 获取getFlowSubTplService实例
     * @return
     */
    FlowSubTplService getFlowSubTplService();

    /**
     * 获取FlowTplSubClassNameService实例
     * @return
     */
    FlowTplSubClassNameService getFlowTplSubClassNameService();

    /**
     * 获取FlowProcessService
     * @return
     */
    FlowProcessService getFlowProcessService();

    /**
     * 获取FlowSubProcessService 实例
     * @return
     */
    FlowSubProcessService getFlowSubProcessService();

    /**
     * 获取FlowDicService实例
     * @return
     */
    FlowDicService getFlowDicService();

    /**
     * 获取FlowVariableService
     * @return
     */
    FlowVariableService getFlowVariableService();


}
