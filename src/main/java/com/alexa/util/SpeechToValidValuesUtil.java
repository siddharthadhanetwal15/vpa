package main.java.com.alexa.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dhanetwa on 3/17/2018.
 */
public class SpeechToValidValuesUtil {

    static Map<String, String> awsRegionMap = new HashMap<>();
    static {
        awsRegionMap.put("northern virginia", "us-east-1");
        awsRegionMap.put("ohio", "us-east-2");
        awsRegionMap.put("northern california", "us-west-1");
        awsRegionMap.put("oregon", "us-west-2");
        awsRegionMap.put("tokyo", "ap-northeast-1");
        awsRegionMap.put("seoul", "ap-northeast-2");
        awsRegionMap.put("mumbai", "ap-south-1");
        awsRegionMap.put("singapore", "ap-southeast-1");
        awsRegionMap.put("sydney", "ap-southeast-2");
        awsRegionMap.put("beijing", "cn-north-1");
        awsRegionMap.put("ireland", "eu-west-1");
        awsRegionMap.put("london", "eu-west-2");
        awsRegionMap.put("sao paulo", "sa-east-1");
    }

    static Map<String, String> amiIdMap = new HashMap<>();
    static {
        amiIdMap.put("linux ami", "ami-1853ac65");
        amiIdMap.put("ubuntu server", "ami-66506c1c");
        amiIdMap.put("microsoft windows server", "ami-cab14db7");
        amiIdMap.put("microsoft windows server 2008", "ami-05669b78");
    }


    static Map<String, String> subnetIdMap = new HashMap<>();
    static {
        subnetIdMap.put("north virgina subnet", "subnet-f0c1099b");
        subnetIdMap.put("lambda subnet", "subnet-f01434df");
    }
    public static String getAWSRegion(String cityName){
        String region = awsRegionMap.get(cityName);
        return region;
    }

    public static String getAMIId(String amiName){
        String amiId = amiIdMap.get(amiName);
        return amiId;
    }

    public static String getSubnetId(String subnetName){
        String subnetId = subnetIdMap.get(subnetName);
        return subnetId;
    }
}
