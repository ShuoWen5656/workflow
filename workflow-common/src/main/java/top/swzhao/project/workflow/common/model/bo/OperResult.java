package top.swzhao.project.workflow.common.model.bo;

/**
 * @author swzhao
 * @data 2023/10/2 23:32
 * @Discreption <> 公共结果封装类
 */
public class OperResult<T> {

    public static final Integer OPT_SUCCESS = 1;
    public static final Integer OPT_FAIL = 0;


    /**
     * 结果返回值
     */
    private T data;

    /**
     * 结果码
     */
    private Integer code;

    /**
     * 结果信息
     */
    private String msg;


    public boolean success() {
        return OperResult.OPT_SUCCESS.equals(code);
    }

    public OperResult(T data, Integer code, String msg) {
        this.data = data;
        this.code = code;
        this.msg = msg;
    }


    public OperResult(T data, Integer code) {
        this.data = data;
        this.code = code;
    }

    public OperResult(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
