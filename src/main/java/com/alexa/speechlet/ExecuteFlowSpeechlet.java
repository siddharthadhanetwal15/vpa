package com.alexa.speechlet;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.slu.Slot;
import com.amazon.speech.speechlet.*;
import com.amazon.speech.speechlet.dialog.directives.DelegateDirective;
import com.amazon.speech.speechlet.dialog.directives.DialogIntent;
import com.amazon.speech.speechlet.dialog.directives.DialogSlot;
import com.amazon.speech.speechlet.dialog.directives.ElicitSlotDirective;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SimpleCard;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import main.java.com.alexa.util.SpeechToValidValuesUtil;
import main.java.com.alexa.vo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.*;

public class ExecuteFlowSpeechlet implements Speechlet {
    private static final Logger log = LoggerFactory.getLogger(ExecuteFlowSpeechlet.class);
    public static final String PROVIDER_SAP = "https://ec2.amazonaws.com:443";
    public static final String PROVIDER_USERNAME = "";
    public static final String PROVIDER_PASSWORD = "";
    public static final String CREATE_BUCKET_FLOW_UUID = "d689396e-1f48-4f1f-bd78-882f554c26ce";
    public static final String DEPLOY_INSTANCE_FLOW_UUID = "2bfe954e-7a22-480e-b32c-5e05da76446f";
    public static final String OO_URL = "https://16.77.19.19:8445/oo/";
    public static final String V2_EXECUTIONS = "rest/v2/executions";
    public static final String V2_EXECUTION_LOG = "execution-log";
    public static final String REST_V2 = "rest/v2/";
    public static final String OO_USERNAME = "admin";
    public static final String OO_PASSWORD = "admin";

    @Override
    public void onSessionStarted(final SessionStartedRequest request, final Session session)
            throws SpeechletException {
        log.info("onSessionStarted requestId={}, sessionId={}", request.getRequestId(),
                session.getSessionId());
        // any initialization logic goes here
    }

    @Override
    public SpeechletResponse onLaunch(final LaunchRequest request, final Session session)
            throws SpeechletException {
        log.info("onLaunch requestId={}, sessionId={}", request.getRequestId(),
                session.getSessionId());
        return getWelcomeResponse();
    }

    @Override
    public void onSessionEnded(final SessionEndedRequest request, final Session session)
            throws SpeechletException {
        log.info("onSessionEnded requestId={}, sessionId={}", request.getRequestId(),
                session.getSessionId());
        // any cleanup logic goes here
    }

    @SuppressWarnings("rawtypes")
    @Override
    public SpeechletResponse onIntent(final IntentRequest request, final Session session)
            throws SpeechletException {
        log.info("onIntent requestId={}, sessionId={}", request.getRequestId(),
                session.getSessionId());

        Intent intent = request.getIntent();
        String intentName = (intent != null) ? intent.getName() : null;

        //Get Dialog State
        IntentRequest.DialogState dialogueState = request.getDialogState();
        log.debug("Intent Name: "+intentName);
        if ("BookFlightIntent".equals(intentName)) {
            List<FlowsLibraryVO> flowsLibraryVOList = getAllFlows();
            List<InputsVO> inputsVOList = getFlowInputs(CREATE_BUCKET_FLOW_UUID);
            List<String> mandatoryInputsSlots = new ArrayList<>();
            ListIterator<InputsVO> inputsVOListIterator = inputsVOList.listIterator();
            while(inputsVOListIterator.hasNext()){
                if(inputsVOListIterator.next().getMandatory().equals("true")){
                    mandatoryInputsSlots.add(inputsVOListIterator.next().getName());
                }
            }
            log.debug("dialogue state: " + dialogueState);
            SpeechletResponse speechletResponse = new SpeechletResponse();
            Map<String, Slot> slotMap = intent.getSlots();

            for (Map.Entry<String, Slot> slotEntry : slotMap.entrySet()) {
                int i = 0;
                log.debug("slot entry name : " + slotEntry.getValue().getName());
                log.debug("slot entry value : " + slotEntry.getValue().getValue());
                if(slotEntry.getValue().getValue() == null){
                    PlainTextOutputSpeech plainTextOutputSpeech = new PlainTextOutputSpeech();
                    //plainTextOutputSpeech.setText("What is "+slotEntry.getValue().getName()+" ?");
                    plainTextOutputSpeech.setText("What is "+ mandatoryInputsSlots.get(i) +" ?");
                    ElicitSlotDirective elicitSlotDirective = new ElicitSlotDirective();
                    elicitSlotDirective.setSlotToElicit(slotEntry.getValue().getName());
                    //elicitSlotDirective.setUpdatedIntent(dialogIntent);
                    speechletResponse.setOutputSpeech(plainTextOutputSpeech);
                    List<Directive> directiveList = new ArrayList<Directive>();
                    directiveList.add(elicitSlotDirective);
                    speechletResponse.setDirectives(directiveList);
                    speechletResponse.setShouldEndSession(false);
                    return speechletResponse;
                }
            }
            log.debug(intent.getSlot("Source").getValue());
            log.debug(intent.getSlot("Destination").getValue());
            PlainTextOutputSpeech plainTextOutputSpeech = new PlainTextOutputSpeech();
            plainTextOutputSpeech.setText("Your flight has been booked from "+intent.getSlot("Source").getValue()+" to "+intent.getSlot("Destination").getValue());
            speechletResponse.setOutputSpeech(plainTextOutputSpeech);
            return speechletResponse;

        } else if ("AMAZON.HelpIntent".equals(intentName)) {
            return getHelpResponse();
        }else if ("AMAZON.StopIntent".equals(intentName)) {
            return getStopResponse();
        }else if ("AMAZON.CancelIntent".equals(intentName)) {
            return getCancelResponse();
        } else {
            log.debug("when no matching flow/intent found");
            throw new SpeechletException("Invalid Intent");
        }
    }

    public static void main(String[] args) {
        ExecuteFlowSpeechlet executeFlowSpeechlet = new ExecuteFlowSpeechlet();
        List<FlowsLibraryVO> flowsLibraryVOList = executeFlowSpeechlet.getAllFlows();
        flowsLibraryVOList = executeFlowSpeechlet.filterLeafFlows(flowsLibraryVOList);
        String flowName = "Create Azure File";
        List<FlowsLibraryVO> resultedFlows = executeFlowSpeechlet.searchFlows(flowName, flowsLibraryVOList);
        List<InputsVO> inputsVOList = executeFlowSpeechlet.getFlowInputs(CREATE_BUCKET_FLOW_UUID);
        List<String> mandatoryInputsSlots = new ArrayList<>();
        ListIterator<InputsVO> inputsVOListIterator = inputsVOList.listIterator();
        while(inputsVOListIterator.hasNext()){
            if(inputsVOListIterator.next().getMandatory().equals("true")){
                mandatoryInputsSlots.add(inputsVOListIterator.next().getName());
            }
        }

    }

    public List<FlowsLibraryVO> searchFlows(String flowName, List<FlowsLibraryVO> flowsLibraryVOList){
        ListIterator<FlowsLibraryVO> libraryVOListIterator = flowsLibraryVOList.listIterator()!=null?flowsLibraryVOList.listIterator():null;
        while(libraryVOListIterator!=null && libraryVOListIterator.hasNext()){
            if(!libraryVOListIterator.next().getName().equalsIgnoreCase(flowName)){
                libraryVOListIterator.remove();
            }
        }
        return flowsLibraryVOList;
    }


    public List<FlowsLibraryVO> filterLeafFlows(List<FlowsLibraryVO> flowsLibraryVOList){
        ListIterator<FlowsLibraryVO> libraryVOListIterator = flowsLibraryVOList.listIterator()!=null?flowsLibraryVOList.listIterator():null;
        while(libraryVOListIterator!=null && libraryVOListIterator.hasNext()){
            if(!libraryVOListIterator.next().getLeaf()){
                libraryVOListIterator.remove();
            }
        }
        return flowsLibraryVOList;
    }

    public List<FlowsLibraryVO> getAllFlows(){
        int status = 0;
        String flowLibrary;
        FlowsLibraryVO flowsLibraryVO = null;
        List<FlowsLibraryVO> flowsLibraryVOList = null;
        TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }
            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }
            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }
        };
        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            // Create all-trusting host name verifier
            HostnameVerifier allHostsValid = new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };
            // Install the all-trusting host verifier
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

            URL url = new URL(OO_URL+REST_V2+"flows/library");
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setDoOutput(true);
            con.setDoInput(true);
            con.setRequestProperty("Authorization", "Basic YWRtaW46YWRtaW4=");
            con.setRequestProperty("Content-Type", "application/json");
            status = con.getResponseCode();
            log.info(String.valueOf(status));
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (con.getInputStream())));
            flowLibrary = br.readLine();
            log.debug("execution log: " + flowLibrary);
            GsonBuilder gson = new GsonBuilder();
            flowsLibraryVOList = gson.create().fromJson(flowLibrary, new TypeToken<List<FlowsLibraryVO>>(){}.getType());
            log.debug("flow inputs list: "+flowsLibraryVOList.get(0).getName());
        } catch (Exception ex){
            log.error(String.valueOf(ex));
        }
        return flowsLibraryVOList;
    }
    public List<InputsVO> getFlowInputs(String flowUUID){
        int status = 0;
        String flowInputs;
        InputsVO inputsVO = null;
        List<InputsVO> inputsVOList = null;
        TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }
            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }
            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }
        };
        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            // Create all-trusting host name verifier
            HostnameVerifier allHostsValid = new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };
            // Install the all-trusting host verifier
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

            URL url = new URL(OO_URL+REST_V2+"flows/"+flowUUID+"/inputs");
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setDoOutput(true);
            con.setDoInput(true);
            con.setRequestProperty("Authorization", "Basic YWRtaW46YWRtaW4=");
            con.setRequestProperty("Content-Type", "application/json");
            status = con.getResponseCode();
            log.info(String.valueOf(status));
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (con.getInputStream())));
            flowInputs = br.readLine();
            log.debug("execution log: " + flowInputs);
            GsonBuilder gson = new GsonBuilder();
            inputsVOList = gson.create().fromJson(flowInputs, new TypeToken<List<InputsVO>>(){}.getType());
            //flowInputsVO = gson.create().fromJson(flowInputs, FlowInputsVO.class);
            log.debug("flow inputs list: "+inputsVOList.get(0).getName());
        } catch (Exception ex){
            log.error(String.valueOf(ex));
        }
        return inputsVOList;
    }

    private SpeechletResponse getExecuteFlowResponse(Intent intent) {
        Map<String, Slot> slotMap = intent.getSlots();

        for (Map.Entry<String, Slot> slotEntry : slotMap.entrySet()) {
            slotEntry.getKey();
            slotEntry.getValue();
        }

        String speechText = "";
        SimpleCard card = new SimpleCard();
        // Create the plain text output.
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();

        FlowInputVO flowInputVO = new FlowInputVO();
        flowInputVO.setFlowUuid(DEPLOY_INSTANCE_FLOW_UUID);
        EC2InstanceVO ec2InstanceVO = new EC2InstanceVO();
        ec2InstanceVO.setProviderSAP(PROVIDER_SAP);
        ec2InstanceVO.setProviderUsername(PROVIDER_USERNAME);
        ec2InstanceVO.setProviderPassword(PROVIDER_PASSWORD);
        flowInputVO.setInputs(ec2InstanceVO);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String instanceData = gson.toJson(flowInputVO);
        log.info(instanceData);
        FlowStatusVO flowStatusVO = callOOFlowExecutorAPI(instanceData);
        if(flowStatusVO.getStatus() == 201) {
            //speechText = instanceName + " instance has been deployed in " +instanceRegion+ " with " + keyPairName+ " key pair value.";
            log.debug(flowStatusVO.getExecutionId());
            ExecutionLogVO executionLogVO = checkExecutionStatus(flowStatusVO.getExecutionId());
            String statusName = executionLogVO.getExecutionSummary().getResultStatusName();
            log.debug("status name: "+statusName);
            if(statusName.equals("success")){
                String instanceId = executionLogVO.getFlowOutput().getInstanceId();
                String availabilityZone = executionLogVO.getFlowOutput().getAvailabilityZone();
                log.debug("inside success");
                if((instanceId != null && instanceId.length() != 0)&&(availabilityZone != null && availabilityZone.length() != 0)) {
                    log.debug("inside success if");
                    speechText = "Flow executed successfully";
                    card.setTitle("Flow executed successfully");
                    card.setContent(speechText);
                    // Create the plain text output.
                    speech.setText(speechText);
                    return SpeechletResponse.newTellResponse(speech, card);
                }
            } else{
                String failureReason = executionLogVO.getFlowOutput().getInstanceId();
                if(failureReason != null){
                    speechText = "Unable to deploy instance in AWS! " +failureReason+ " Please check run id: "+flowStatusVO.getExecutionId()+" in OO machine for detailed error.";
                    card.setTitle("Error while deploy instance in AWS. " +failureReason+" Check run id: "+flowStatusVO.getExecutionId());
                    card.setContent(speechText);
                    // Create the plain text output.
                    speech.setText(speechText);
                    return SpeechletResponse.newTellResponse(speech, card);
                } else {
                    speechText = "Unable to deploy instance in AWS! Please check run id: "+flowStatusVO.getExecutionId()+" in OO machine for detailed error.";
                    card.setTitle("Error while deploy instance in AWS. Check run id: "+flowStatusVO.getExecutionId());
                    card.setContent(speechText);
                    // Create the plain text output.
                    speech.setText(speechText);
                    return SpeechletResponse.newTellResponse(speech, card);
                }
            }
        }
        // Create the Simple card content.
        card.setTitle("Unable to trigger OO flow for deploy instance");
        card.setContent(speechText);
        speech.setText(speechText);

        return SpeechletResponse.newTellResponse(speech, card);
    }

    /**
     * Creates a {@code SpeechletResponse} for the hello intent.
     *
     * @return SpeechletResponse spoken and visual response for the given intent
     */
    private SpeechletResponse getCreateBucketResponse(Intent intent) {

        String bucketName = intent.getSlot("bucketName").getValue();
        String bucketRegion = intent.getSlot("bucketRegion").getValue();
        bucketRegion = SpeechToValidValuesUtil.getAWSRegion(bucketRegion.toLowerCase());
        String speechText = "";
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        // Create the Simple card content.
        SimpleCard card = new SimpleCard();
        if(bucketRegion == null){
            log.debug("Bucket region not found");
            speechText = "Bucket region not valid. Please run flow executor again";
            speech.setText(speechText);
            return SpeechletResponse.newTellResponse(speech, card);
        }
        FlowInputVO flowInputVO = new FlowInputVO();
        flowInputVO.setFlowUuid(CREATE_BUCKET_FLOW_UUID);
        S3BucketVO s3BucketVO = new S3BucketVO();
        s3BucketVO.setBucketName(bucketName);
        s3BucketVO.setAwsRegion(bucketRegion);
        s3BucketVO.setProviderSAP(PROVIDER_SAP);
        s3BucketVO.setProviderUsername(PROVIDER_USERNAME);
        s3BucketVO.setProviderPassword(PROVIDER_PASSWORD);
        flowInputVO.setInputs(s3BucketVO);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String bucketData = gson.toJson(flowInputVO);
        log.info(bucketData);
        FlowStatusVO flowStatusVO = callOOFlowExecutorAPI(bucketData);
        if(flowStatusVO.getStatus() == 201) {
            /*speechText = "Create bucket flow executed successfully. Please wait while we get you created bucket details.";
            card.setTitle("CreateBucket flow executed");
            card.setContent(speechText);
            // Create the plain text output.
            PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
            speech.setText(speechText);
            // Create reprompt
            Reprompt reprompt = new Reprompt();
            reprompt.setOutputSpeech(speech);
            SpeechletResponse.newAskResponse(speech, card);*/
            //"A bucket named " + bucketName + ", has been created in " + bucketRegion;

            log.debug(flowStatusVO.getExecutionId());
            ExecutionLogVO executionLogVO = checkExecutionStatus(flowStatusVO.getExecutionId());
            String statusName = executionLogVO.getExecutionSummary().getResultStatusName();
            log.debug("status name: "+statusName);
            if(statusName.equals("success")){
                String createdBucketName = executionLogVO.getFlowOutput().getBucketName();
                String createdBucketRegion = executionLogVO.getFlowOutput().getAwsRegion();
                if((createdBucketName != null && createdBucketName.length() != 0)&&(createdBucketRegion != null && createdBucketRegion.length() != 0)) {
                    speechText = "A bucket named " + createdBucketName + ", has been created in " + createdBucketRegion;
                    card.setTitle("Bucket Created");
                    card.setContent(speechText);
                    // Create the plain text output.
                    speech.setText(speechText);
                    return SpeechletResponse.newTellResponse(speech, card);
                }
            } else{
                speechText = "Unable to create bucket in AWS! Please check run id: "+flowStatusVO.getExecutionId()+" in OO machine for detailed error.";
                card.setTitle("Error while creating bucket in AWS. Check run id: "+flowStatusVO.getExecutionId());
                card.setContent(speechText);
                // Create the plain text output.
                speech.setText(speechText);
                return SpeechletResponse.newTellResponse(speech, card);
            }
        }
        speechText = "Unable to execute create bucket flow";
        card.setTitle("Unable to trigger OO flow execution rest api");
        card.setContent(speechText);

        // Create the plain text output.
        speech.setText(speechText);

        return SpeechletResponse.newTellResponse(speech, card);
    }

    private SpeechletResponse getDeployInstanceResponse(Intent intent) {
        String instanceName = intent.getSlot("InstanceName").getValue();
        String instanceRegion = intent.getSlot("InstanceRegion").getValue();
        String amazonMachineImage = intent.getSlot("AMI").getValue();
        String keyPairName = intent.getSlot("KeyPairName").getValue();
        String subnet = intent.getSlot("SubnetId").getValue();
        log.debug("instanceName " +instanceName+ "region "+instanceRegion+"ami "+amazonMachineImage+"kp "+keyPairName+"subnet "+subnet);
        instanceRegion = SpeechToValidValuesUtil.getAWSRegion(instanceRegion.toLowerCase());
        String speechText = "";
        SimpleCard card = new SimpleCard();
        // Create the plain text output.
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        if(instanceRegion == null){
            log.debug("Instance region not valid");
            speechText = "Instance region not valid. Please run flow executor again";
            speech.setText(speechText);
            return SpeechletResponse.newTellResponse(speech, card);
        }
        amazonMachineImage = SpeechToValidValuesUtil.getAMIId(amazonMachineImage.toLowerCase());
        if(amazonMachineImage == null){
            log.debug("Invalid amazon machine image");
            speechText = "Invalid amazon machine image. Please run flow executor again";
            speech.setText(speechText);
            return SpeechletResponse.newTellResponse(speech, card);
        }
        if(keyPairName != null && !keyPairName.toLowerCase().equals("voice key pair")){
            log.debug("Invalid key pair value");
            speechText = "Invalid key pair value. Please run flow executor again";
            speech.setText(speechText);
            return SpeechletResponse.newTellResponse(speech, card);
        }
        subnet = SpeechToValidValuesUtil.getSubnetId(subnet.toLowerCase());
        if(subnet == null){
            log.debug("Invalid subnet name");
            speechText = "You have added invalid subnet. Please run flow executor again";
            speech.setText(speechText);
            return SpeechletResponse.newTellResponse(speech, card);
        }
        FlowInputVO flowInputVO = new FlowInputVO();
        flowInputVO.setFlowUuid(DEPLOY_INSTANCE_FLOW_UUID);
        EC2InstanceVO ec2InstanceVO = new EC2InstanceVO();
        ec2InstanceVO.setInstanceName(instanceName);
        ec2InstanceVO.setRegion(instanceRegion);
        ec2InstanceVO.setAmiId(amazonMachineImage);
        ec2InstanceVO.setKeyName(keyPairName);
        ec2InstanceVO.setSubnetId(subnet);
        ec2InstanceVO.setProviderSAP(PROVIDER_SAP);
        ec2InstanceVO.setProviderUsername(PROVIDER_USERNAME);
        ec2InstanceVO.setProviderPassword(PROVIDER_PASSWORD);
        flowInputVO.setInputs(ec2InstanceVO);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String instanceData = gson.toJson(flowInputVO);
        log.info(instanceData);
        FlowStatusVO flowStatusVO = callOOFlowExecutorAPI(instanceData);
        if(flowStatusVO.getStatus() == 201) {
            //speechText = instanceName + " instance has been deployed in " +instanceRegion+ " with " + keyPairName+ " key pair value.";
            log.debug(flowStatusVO.getExecutionId());
            ExecutionLogVO executionLogVO = checkExecutionStatus(flowStatusVO.getExecutionId());
            String statusName = executionLogVO.getExecutionSummary().getResultStatusName();
            log.debug("status name: "+statusName);
            if(statusName.equals("success")){
                String instanceId = executionLogVO.getFlowOutput().getInstanceId();
                String availabilityZone = executionLogVO.getFlowOutput().getAvailabilityZone();
                log.debug("inside success");
                if((instanceId != null && instanceId.length() != 0)&&(availabilityZone != null && availabilityZone.length() != 0)) {
                    log.debug("inside success if");
                    speechText = instanceName+ " instance has been deployed in " +availabilityZone+ " availability zone and it's instance id is: "+instanceId;
                    card.setTitle("Instance Deployed");
                    card.setContent(speechText);
                    // Create the plain text output.
                    speech.setText(speechText);
                    return SpeechletResponse.newTellResponse(speech, card);
                }
            } else{
                String failureReason = executionLogVO.getFlowOutput().getInstanceId();
                if(failureReason != null){
                speechText = "Unable to deploy instance in AWS! " +failureReason+ " Please check run id: "+flowStatusVO.getExecutionId()+" in OO machine for detailed error.";
                card.setTitle("Error while deploy instance in AWS. " +failureReason+" Check run id: "+flowStatusVO.getExecutionId());
                card.setContent(speechText);
                // Create the plain text output.
                speech.setText(speechText);
                return SpeechletResponse.newTellResponse(speech, card);
                } else {
                    speechText = "Unable to deploy instance in AWS! Please check run id: "+flowStatusVO.getExecutionId()+" in OO machine for detailed error.";
                    card.setTitle("Error while deploy instance in AWS. Check run id: "+flowStatusVO.getExecutionId());
                    card.setContent(speechText);
                    // Create the plain text output.
                    speech.setText(speechText);
                    return SpeechletResponse.newTellResponse(speech, card);
                }
            }
        }
        // Create the Simple card content.
        card.setTitle("Unable to trigger OO flow for deploy instance");
        card.setContent(speechText);
        speech.setText(speechText);

        return SpeechletResponse.newTellResponse(speech, card);
    }

    private FlowStatusVO callOOFlowExecutorAPI(String data) {
        int status = 0;
        String output = null;
        TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }
            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }
            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }
        };
        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            // Create all-trusting host name verifier
            HostnameVerifier allHostsValid = new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };

            // Install the all-trusting host verifier
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

            URL url = new URL(OO_URL+V2_EXECUTIONS);
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            con.setDoInput(true);
            con.setRequestProperty("Authorization", "Basic YWRtaW46YWRtaW4=");
            con.setRequestProperty("Content-Type", "application/json");
            OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
            wr.write(data);
            wr.flush();
            status = con.getResponseCode();

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (con.getInputStream())));
            output = br.readLine();
        } catch (Exception ex){
            log.error(String.valueOf(ex));
        }
        FlowStatusVO flowStatusVO = new FlowStatusVO();
        log.debug("execution id: "+ output);
        flowStatusVO.setExecutionId(output);
        flowStatusVO.setStatus(status);
        return flowStatusVO;
    }

    public ExecutionLogVO checkExecutionStatus(String executionId){
        int status = 0;
        String executionLog = null;
        ExecutionLogVO executionLogVO = null;
        TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }
            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }
            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }
        };
        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            // Create all-trusting host name verifier
            HostnameVerifier allHostsValid = new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };
            Thread.sleep(5000);
            log.debug("Main thread waiting for 5 seconds");
            // Install the all-trusting host verifier
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
            URL url = new URL(OO_URL+V2_EXECUTIONS + "/" + executionId + "/" +V2_EXECUTION_LOG);
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setDoOutput(true);
            con.setDoInput(true);
            con.setRequestProperty("Authorization", "Basic " + getAuthorizationString(OO_USERNAME,OO_PASSWORD));
            con.setRequestProperty("Content-Type", "application/json");
            status = con.getResponseCode();
            log.info(String.valueOf(status));
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (con.getInputStream())));
            executionLog = br.readLine();
            br.close();
            log.debug("execution log: " + executionLog);
            GsonBuilder gson = new GsonBuilder();
            executionLogVO = gson.create().fromJson(executionLog, ExecutionLogVO.class);
            String executionStatus = executionLogVO.getExecutionSummary().getStatus();
            if(!(executionStatus.equals("COMPLETED") || executionStatus.equals("FAILED"))){
                log.debug("execution status: "+executionStatus);
                return checkExecutionStatus(executionId);
            }
        } catch (Exception ex){
            log.error(String.valueOf(ex));
        }
        return executionLogVO;
    }

    /**
     * Creates and returns a {@code SpeechletResponse} with a welcome message.
     *
     * @return SpeechletResponse spoken and visual response for the given intent
     */
    private SpeechletResponse getWelcomeResponse() {
        String speechText = "Welcome to Voice Process Automation tool. Which flow do you want to execute?";

        // Create the Simple card content.
        SimpleCard card = new SimpleCard();
        card.setTitle("Welcome to Voice Process Automation tool");
        card.setContent(speechText);

        // Create the plain text output.
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(speechText);

        // Create reprompt
        Reprompt reprompt = new Reprompt();
        reprompt.setOutputSpeech(speech);

        return SpeechletResponse.newAskResponse(speech, reprompt, card);
    }

    private SpeechletResponse getStopResponse() {
        String speechText = "Flow executor stopped";

        // Create the Simple card content.
        SimpleCard card = new SimpleCard();
        card.setTitle("Flow executor stopped");
        card.setContent(speechText);

        // Create the plain text output.
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(speechText);

        return SpeechletResponse.newTellResponse(speech, card);
    }

    private SpeechletResponse getCancelResponse() {
        String speechText = "Current flow canceled. Do you want to run any other flow?";

        // Create the Simple card content.
        SimpleCard card = new SimpleCard();
        card.setTitle("Current flow canceled. Do you want to run any other flow?");
        card.setContent(speechText);

        // Create the plain text output.
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(speechText);

        // Create reprompt
        Reprompt reprompt = new Reprompt();
        reprompt.setOutputSpeech(speech);

        return SpeechletResponse.newAskResponse(speech, reprompt, card);
    }

    /**
     * Creates a {@code SpeechletResponse} for the help intent.
     *
     * @return SpeechletResponse spoken and visual response for the given intent
     */
    private SpeechletResponse getHelpResponse() {
        String speechText = "You can say flow name here like create a bucket or deploy an instance";

        // Create the Simple card content.
        SimpleCard card = new SimpleCard();
        card.setTitle("say create a bucket or deploy an instance");
        card.setContent(speechText);

        // Create the plain text output.
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(speechText);

        // Create reprompt
        Reprompt reprompt = new Reprompt();
        reprompt.setOutputSpeech(speech);

        return SpeechletResponse.newAskResponse(speech, reprompt, card);
    }

    private String getAuthorizationString(String user, String password)
    {
        String authorizationString = user + ":" + password;
        byte[] encodedAuthorization = Base64.getEncoder().encode(authorizationString.getBytes());
        log.debug("encoded auth: "+new String(encodedAuthorization));
        return new String(encodedAuthorization);
    }
}
