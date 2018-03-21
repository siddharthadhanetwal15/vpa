package main.java.com.alexa.vo;

/**
 * Created by dhanetwa on 3/18/2018.
 */
public class ExecutionSummaryVO {
    private String executionId;
    private String status;
    private String resultStatusType;
    private String resultStatusName;
    private String pauseReason;
    private String cancelReason;

    public String getExecutionId() {
        return executionId;
    }

    public void setExecutionId(String executionId) {
        this.executionId = executionId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getResultStatusType() {
        return resultStatusType;
    }

    public void setResultStatusType(String resultStatusType) {
        this.resultStatusType = resultStatusType;
    }

    public String getResultStatusName() {
        return resultStatusName;
    }

    public void setResultStatusName(String resultStatusName) {
        this.resultStatusName = resultStatusName;
    }

    public String getPauseReason() {
        return pauseReason;
    }

    public void setPauseReason(String pauseReason) {
        this.pauseReason = pauseReason;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }
}
