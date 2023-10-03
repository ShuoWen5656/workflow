package top.swzhao.project.workflow.common.utils;

import top.swzhao.project.workflow.common.model.dto.FlowSubProcessSortDto;
import top.swzhao.project.workflow.common.model.dto.FlowSubTplDto;
import top.swzhao.project.workflow.common.model.dto.Variable;
import top.swzhao.project.workflow.common.model.po.FlowSubProcess;
import top.swzhao.project.workflow.common.model.po.FlowVariable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author swzhao
 * @data 2023/10/3 17:58
 * @Discreption <>
 */
public class ConvertUtils {

    //public static List<FlowSubProcessSortDto> convert2FlowSubProcessSortDto(List<FlowSubProcess> flowSubProcesses, List<FlowSubTplDto> flowSubTplDtos) {
    //
    //}



    /**
     * 将库中取出来的变量加载为map
     * @param flowVariables
     * @return
     */
    public static Map<String, Variable> convert2VariableFormFlowVariable(List<FlowVariable> flowVariables) {
        Map<String, Variable> variableMap = new HashMap<>(16);
        for (FlowVariable flowVariable : flowVariables) {
            variableMap.put(flowVariable.getName(), new Variable(flowVariable.getName(), flowVariable.getClassType(), flowVariable.getVariableContent()));
        }
        return variableMap;
    }
}
