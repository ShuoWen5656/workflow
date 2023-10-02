package top.swzhao.project.workflow.common.model.dto;

import lombok.Data;
import lombok.ToString;

/**
 * @author swzhao
 * @data 2023/10/2 23:29
 * @Discreption <> 入参变量
 */
@Data
@ToString
public class Variable {

    /**
     * 变量的名称
     */
    private String name;

    /**
     * 变量的类型reference
     */
    private String className;

    /**
     * 变量序列化后的json字符串
     */
    private String jsonString;



    public Variable(String name, String className, String jsonString) {
        this.name = name;
        this.className = className;
        this.jsonString = jsonString;
    }
}
