package main.java.com.alexa.vo;

public class S3BucketVO extends FlowInputVO {
    String bucketName;
    String awsRegion;
    String providerSAP;
    String providerUsername;
    String providerPassword;

    public String getProviderSAP() {
        return providerSAP;
    }

    public void setProviderSAP(String providerSAP) {
        this.providerSAP = providerSAP;
    }

    public String getProviderUsername() {
        return providerUsername;
    }

    public void setProviderUsername(String providerUsername) {
        this.providerUsername = providerUsername;
    }

    public String getProviderPassword() {
        return providerPassword;
    }

    public void setProviderPassword(String providerPassword) {
        this.providerPassword = providerPassword;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getAwsRegion() {
        return awsRegion;
    }

    public void setAwsRegion(String awsRegion) {
        this.awsRegion = awsRegion;
    }
}
