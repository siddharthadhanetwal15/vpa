package main.java.com.alexa.vo;

import java.util.ArrayList;

/**
 * Created by dhanetwa on 3/18/2018.
 */
public class ExecutionLogVO {
    private ExecutionSummaryVO executionSummary;
    private FlowOutputVO flowOutput;
    private ArrayList<FlowVariablesVO> flowVars;
    private String executionLogLevel;

    public ArrayList<FlowVariablesVO> getFlowVars() {
        return flowVars;
    }

    public void setFlowVars(ArrayList<FlowVariablesVO> flowVars) {
        this.flowVars = flowVars;
    }

    public String getExecutionLogLevel() {
        return executionLogLevel;
    }

    public void setExecutionLogLevel(String executionLogLevel) {
        this.executionLogLevel = executionLogLevel;
    }

    public ExecutionSummaryVO getExecutionSummary() {
        return executionSummary;
    }

    public void setExecutionSummary(ExecutionSummaryVO executionSummary) {
        this.executionSummary = executionSummary;
    }

    public FlowOutputVO getFlowOutput() {
        return flowOutput;
    }

    public void setFlowOutput(FlowOutputVO flowOutput) {
        this.flowOutput = flowOutput;
    }
}
