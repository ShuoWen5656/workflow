package top.swzhao.project.workflow.common.model.bo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import lombok.extern.slf4j.Slf4j;
import top.swzhao.project.workflow.common.model.dto.Variable;
import top.swzhao.project.workflow.common.model.po.FlowVariable;
import top.swzhao.project.workflow.common.utils.ConvertUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author swzhao
 * @data 2023/10/3 17:44
 * @Discreption <> 子任务之间流程交互参数的封装类，可用于全局变量和临时变量
 */
@Slf4j
public class FlowParam {


    /**
     * 输入变量容器，执行之前需要装入的变量放在这里
     */
    private Map<String, Variable> input;

    /**
     * 输出变量容器，执行之后输出的变量放在这里
     */
    private Map<String, Variable> output;

    public static final int KEY_INPUT = 1;

    public static final int KEY_OUTPUT = 2;

    public FlowParam() {
        this.input = new ConcurrentHashMap<>();
        this.output = new ConcurrentHashMap<>();
    }

    public boolean containsInputKey(String key) {
        return input.containsKey(key);
    }

    public boolean containsOutputKey(String key) {
        return output.containsKey(key);
    }

    /**
     * 单个添加
     * @param key
     * @param object
     */
    public void setInputKV(String key, Object object){
        input.put(key, new Variable(key, object.getClass().getName(), JSON.toJSONString(object)));
    }

    /**
     * 批量添加,通常从加载库中变量使用
     * @param flowVariables
     */
    public void setInputKV(List<FlowVariable> flowVariables){
        setFlowVariables(KEY_INPUT, flowVariables);
    }

    /**
     * 单个添加
     * @param key
     * @param object
     */
    public void setOutputKV(String key, Object object){
        output.put(key, new Variable(key, object.getClass().getName(), JSON.toJSONString(object)));
    }

    /**
     * 批量添加,通常从加载库中变量使用
     * @param flowVariables
     */
    public void setOutputKV(List<FlowVariable> flowVariables){
        setFlowVariables(KEY_OUTPUT, flowVariables);
    }



    /**
     * 通用加载变量方法
     * @param type
     * @param flowVariables
     */
    private void setFlowVariables(int type, List<FlowVariable> flowVariables){
        Map<String, Variable> container = type == KEY_INPUT ? this.input : this.output;
        container.putAll(ConvertUtils.convert2VariableFormFlowVariable(flowVariables));
    }


    public Object getInputKV(String key) {
        return getkv(key, KEY_INPUT, null);
    }

    public <T> Object getInputKV(String key, TypeReference<T> typeReference) {
        return getkv(key, KEY_INPUT, typeReference);
    }

    public Object getOutputKV(String key) {
        return getkv(key, KEY_OUTPUT, null);
    }


    public <T> Object getOutputKV(String key, TypeReference<T> typeReference) {
        return getkv(key, KEY_OUTPUT, typeReference);
    }



    /**
     * 从map中解析出来对应的变量
     * @param key
     * @param type
     * @param typeReference
     * @param <T>
     * @return
     */
    private <T> Object getkv(String key, int type, TypeReference<T> typeReference) {
        Map<String, Variable> map = type == KEY_INPUT ? this.input : this.output;
        if (map.containsKey(key)) {
            Variable variable = map.get(key);
            String className = variable.getClassName();
            String jsonString = variable.getJsonString();
            try {
                Class<?> classType = Class.forName(className);
                return typeReference == null ? JSON.parseObject(jsonString, classType)
                        : JSON.parseObject(jsonString, typeReference);
            }catch (Exception e) {
                log.error("【引擎异常】获取参数值异常：", e);
                return null;
            }
        }
        return null;
    }

    /**
     * 交换输入输出，并清空输出
     */
    public void switchInputOutput() {
        Map<String, Variable> tmp = this.input;
        this.input = this.output;
        this.output = tmp;
        output.clear();
    }

    public Map<String, Variable> getInput() {
        return input;
    }

    public Map<String, Variable> getOutput() {
        return output;
    }
}
