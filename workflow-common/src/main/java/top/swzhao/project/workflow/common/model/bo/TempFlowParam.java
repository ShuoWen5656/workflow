package top.swzhao.project.workflow.common.model.bo;

import com.alibaba.fastjson.TypeReference;
import org.apache.commons.lang3.StringUtils;

/**
 * @author swzhao
 * @data 2023/10/3 18:29
 * @Discreption <>
 */
public class TempFlowParam {


    private FlowParam flowParam;

    public TempFlowParam(FlowParam flowParam) {
        this.flowParam = flowParam;
    }

    public TempFlowParam() {
        this.flowParam = new FlowParam();
    }


    public Object getInputKV(String key) {
        return flowParam.getInputKV(key);
    }

    public Object getOutputKV(String key) {
        return flowParam.getOutputKV(key);
    }

    public <T> Object getInputKV(String key, TypeReference<T> typeReference) {
        return flowParam.getInputKV(key, typeReference);
    }

    public <T> Object getOutputKV(String key, TypeReference<T> typeReference) {
        return flowParam.getOutputKV(key, typeReference);
    }

    public FlowParam getFlowParam() {
        return flowParam;
    }
}
