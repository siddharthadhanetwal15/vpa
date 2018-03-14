package main.java.com.alexa.vo;

public class FlowInputVO<T> {
    String flowUuid;
    T inputs;

    public String getFlowUuid() {
        return flowUuid;
    }

    public void setFlowUuid(String flowUuid) {
        this.flowUuid = flowUuid;
    }

    public T getInputs() {
        return inputs;
    }

    public void setInputs(T inputs) {
        this.inputs = inputs;
    }
}
