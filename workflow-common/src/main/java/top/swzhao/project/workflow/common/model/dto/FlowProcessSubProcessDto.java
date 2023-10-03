package top.swzhao.project.workflow.common.model.dto;

import lombok.Data;
import top.swzhao.project.workflow.common.model.po.FlowProcess;

import java.util.List;

/**
 * @author swzhao
 * @data 2023/10/3 17:41
 * @Discreption <> 聚合模式：当前执行的总任务和执行的子任务列表
 */
@Data
public class FlowProcessSubProcessDto {

    private FlowProcess flowProcess;

    private List<FlowSubProcessSortDto> flowSubProcessSortDtoList;

    public FlowProcessSubProcessDto() {
    }

    public FlowProcessSubProcessDto(FlowProcess flowProcess, List<FlowSubProcessSortDto> flowSubProcessSortDtoList) {
        this.flowProcess = flowProcess;
        this.flowSubProcessSortDtoList = flowSubProcessSortDtoList;
    }
}
