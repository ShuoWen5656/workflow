package top.swzhao.project.workflow.common.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import top.swzhao.project.workflow.common.model.po.FlowSubProcess;

/**
 * @author swzhao
 * @data 2023/10/3 17:39
 * @Discreption <> 当前子任务的执行信息
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class FlowSubProcessSortDto extends FlowSubProcess {

    private String subTplName;

    private String className;

    public FlowSubProcessSortDto() {
        super();
    }

    public FlowSubProcessSortDto(String id, String name, String subTplId, String state, String processId, String sort, String subTplName, String className) {
        super(id, name, subTplId, state, processId, sort);
        this.subTplName = subTplName;
        this.className = className;
    }
}
