package top.swzhao.project.workflow.common.utils;

import org.springframework.beans.BeanUtils;
import top.swzhao.project.workflow.common.contants.FlowKvConstants;
import top.swzhao.project.workflow.common.exception.ErrorCodes;
import top.swzhao.project.workflow.common.exception.template.TemplateNotFoundException;
import top.swzhao.project.workflow.common.model.dto.FlowSubProcessSortDto;
import top.swzhao.project.workflow.common.model.dto.FlowSubTplDto;
import top.swzhao.project.workflow.common.model.dto.Variable;
import top.swzhao.project.workflow.common.model.po.FlowSubProcess;
import top.swzhao.project.workflow.common.model.po.FlowVariable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author swzhao
 * @data 2023/10/3 17:58
 * @Discreption <>
 */
public class ConvertUtils {

    public static List<FlowSubProcessSortDto> convert2FlowSubProcessSortDto(List<FlowSubProcess> flowSubProcesses, List<FlowSubTplDto> flowSubTplDtos) throws TemplateNotFoundException {

        ArrayList<FlowSubProcessSortDto> flowSubProcessSortDtos = new ArrayList<>();
        Map<String, FlowSubTplDto> flowSubTplDtoMap = flowSubTplDtos.stream()
                .collect(Collectors.toMap(u -> String.format(FlowKvConstants.STR_CONTAIN_BAR_FORMAT, u.getId(), u.getSort()), v -> v, (k1, k2) -> k1));

        for (FlowSubProcess flowSubProcess : flowSubProcesses) {
            FlowSubProcessSortDto flowSubProcessSortDto = new FlowSubProcessSortDto();
            BeanUtils.copyProperties(flowSubProcess, flowSubProcessSortDto);
            String qkey = String.format(FlowKvConstants.STR_CONTAIN_BAR_FORMAT, flowSubProcess.getSubTplId(), flowSubProcess.getSort());
            FlowSubTplDto flowSubTplDto = flowSubTplDtoMap.get(qkey);
            if (flowSubTplDto == null) {
                throw new TemplateNotFoundException(ErrorCodes.TEMPLATE_ERROR, "找不到流程对应的子模板");
            }
            flowSubProcessSortDto.setClassName(flowSubTplDto.getClassName());
            flowSubProcessSortDto.setSubTplName(flowSubTplDto.getName());
            flowSubProcessSortDtos.add(flowSubProcessSortDto);
        }
        return flowSubProcessSortDtos;
    }



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
