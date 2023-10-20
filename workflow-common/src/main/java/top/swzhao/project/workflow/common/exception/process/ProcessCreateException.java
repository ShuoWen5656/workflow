package top.swzhao.project.workflow.common.exception.process;

/**
 * @author swzhao
 * @date 2023/10/20 9:22 下午
 * @Discreption <>
 */
public class ProcessCreateException extends ProcessException{

    private String processId;

    public ProcessCreateException(Integer errorCode) {
        super(errorCode);
    }

    public ProcessCreateException(Integer errorCode, String message) {
        super(errorCode, message);
    }

    public ProcessCreateException(Integer errorCode, String message, String processId) {
        super(errorCode, message);
        this.processId = processId;
    }

    public String getProcessId() {
        return processId;
    }
}
