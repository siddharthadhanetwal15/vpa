package main.java.com.alexa.vo;

/**
 * Created by dhanetwa on 4/11/2018.
 */
public class InputsVO {
    private String uuid;
    private String name;
    private String valueDelimiter;
    private String description;
    private String encrypted;
    private String multiValue;
    private String mandatory;
    private String sources;
    private String type;
    private String validationId;
    private String defaultValue;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValueDelimiter() {
        return valueDelimiter;
    }

    public void setValueDelimiter(String valueDelimiter) {
        this.valueDelimiter = valueDelimiter;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEncrypted() {
        return encrypted;
    }

    public void setEncrypted(String encrypted) {
        this.encrypted = encrypted;
    }

    public String getMultiValue() {
        return multiValue;
    }

    public void setMultiValue(String multiValue) {
        this.multiValue = multiValue;
    }

    public String getMandatory() {
        return mandatory;
    }

    public void setMandatory(String mandatory) {
        this.mandatory = mandatory;
    }

    public String getSources() {
        return sources;
    }

    public void setSources(String sources) {
        this.sources = sources;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValidationId() {
        return validationId;
    }

    public void setValidationId(String validationId) {
        this.validationId = validationId;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
}
