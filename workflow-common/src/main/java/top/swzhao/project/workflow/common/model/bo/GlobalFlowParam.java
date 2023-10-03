package top.swzhao.project.workflow.common.model.bo;

import com.alibaba.fastjson.TypeReference;
import org.apache.commons.lang3.StringUtils;

/**
 * @author swzhao
 * @data 2023/10/3 18:29
 * @Discreption <>
 */
public class GlobalFlowParam<T> {

    /**
     * 核心
     */
    private FlowParam flowParam;

    /**
     * 禁止修改key - processId
     */
    public static final String FORBID_KEY_PROCESS_ID = "processId";

    public GlobalFlowParam() {
        this.flowParam = new FlowParam();
    }

    public GlobalFlowParam(FlowParam flowParam) {
        this.flowParam = flowParam;
    }

    public void setKV(String key, Object value) {
        if (flowParam.containsInputKey(key)
                && StringUtils.equals(FORBID_KEY_PROCESS_ID, key)) {
            return;
        }
        flowParam.setInputKV(key, value);
    }

    public Object getKV(String key) {
        return flowParam.getInputKV(key);
    }

    public <T> Object getKV(String key, TypeReference<T> typeReference) {
        return flowParam.getInputKV(key, typeReference);
    }

    public FlowParam getFlowParam() {
        return flowParam;
    }
}
