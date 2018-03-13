package com.alexa.speechlet;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.slu.Slot;
import com.amazon.speech.speechlet.*;
import com.amazon.speech.speechlet.dialog.directives.DelegateDirective;
import com.amazon.speech.speechlet.dialog.directives.DialogIntent;
import com.amazon.speech.speechlet.dialog.directives.DialogSlot;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SimpleCard;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import main.java.com.alexa.vo.S3BucketVO;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import java.util.*;

public class ExecuteFlowSpeechlet implements Speechlet {
    private static final Logger log = LoggerFactory.getLogger(ExecuteFlowSpeechlet.class);
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

        if ("CreateBucket".equals(intentName)) {

            //If the IntentRequest dialog state is STARTED and you accept Utterances that
            //allow a user to provide slots?  If not, you don't need to return the updatedIntent.
            if (dialogueState.equals(IntentRequest.DialogState.STARTED)) {

                //Create a new DialogIntent
                DialogIntent dialogIntent = new DialogIntent();

                //Set the name to match our intentName
                dialogIntent.setName(intentName);

                //Map over the Dialog Slots
                //We do this to ensure that we include any slots already provided by the user
                Map<String,DialogSlot> dialogSlots = new HashMap<String,DialogSlot>();

                //Set up an iterator
                Iterator iter = intent.getSlots().entrySet().iterator();

                log.debug("Building DialogIntent");
                //Iterate and copy over all slots/values
                while (iter.hasNext()) {

                    Map.Entry pair = (Map.Entry)iter.next();

                    //Create a new DialogSlot
                    DialogSlot dialogSlot = new DialogSlot();

                    //Create a new Slot
                    Slot slot = (Slot) pair.getValue();

                    //Set the name of the slot
                    dialogSlot.setName(slot.getName());

                    //Copy over the value if its already set
                    if (slot.getValue() != null)
                        dialogSlot.setValue(slot.getValue());

                    //Add this DialogSlot to the DialogSlots Hashmap.
                    dialogSlots.put((String) pair.getKey(), dialogSlot);

                    log.debug("DialogSlot " + (String) pair.getKey() + " with Name " + slot.getName() + " added.");
                }

                //Set the dialogSlots on the DialogIntent
                dialogIntent.setSlots(dialogSlots);

                //Create a DelegateDirective
                DelegateDirective dd = new DelegateDirective();

                //Add our new DialogIntent to the DelegateDirective
                dd.setUpdatedIntent(dialogIntent);

                //Directives must be provided as a List.  Add our DelegateDirective to the List.
                List<Directive> directiveList = new ArrayList<Directive>();
                directiveList.add(dd);

                //Create a new SpeechletResponse and set the Directives to our List.
                SpeechletResponse speechletResp = new SpeechletResponse();
                speechletResp.setDirectives(directiveList);

                //Only end the session if we have all the info. Assuming we still need to
                //get more, we keep the session open.
                speechletResp.setShouldEndSession(false);

                //Return the SpeechletResponse.
                return speechletResp;

            } else if (dialogueState.equals(IntentRequest.DialogState.COMPLETED)) {

                log.debug("onIntent, inside dialogueState IF statement");
                //Generate our response and return.
                return getHelloResponse(intent);

            } else { // dialogueState.equals(DialogState.IN_PROGRESS)

                log.debug("onIntent, inside dialogueState ELSE statement");

                //Create an empty DelegateDirective
                //This will tell the Alexa Engine to keep collecting information.
                DelegateDirective dd = new DelegateDirective();

                //Directives must be provided as a List.  Add our DelegateDirective to the List.
                List<Directive> directiveList = new ArrayList<Directive>();
                directiveList.add(dd);

                //Create a new SpeechletResponse and set the Directives to our List.
                SpeechletResponse speechletResp = new SpeechletResponse();
                speechletResp.setDirectives(directiveList);

                //Only end the session if we have all the info. Assuming we still need to
                //get more, we keep the session open.
                speechletResp.setShouldEndSession(false);

                //Return the SpeechletResponse.
                return speechletResp;
            }
        } else if ("AMAZON.HelpIntent".equals(intentName)) {
            return getHelpResponse();
        } else {
            throw new SpeechletException("Invalid Intent");
        }
    }

    @Override
    public void onSessionEnded(final SessionEndedRequest request, final Session session)
            throws SpeechletException {
        log.info("onSessionEnded requestId={}, sessionId={}", request.getRequestId(),
                session.getSessionId());
        // any cleanup logic goes here
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
        card.setTitle("HelloWorld");
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
     * Creates a {@code SpeechletResponse} for the hello intent.
     *
     * @return SpeechletResponse spoken and visual response for the given intent
     */
    private SpeechletResponse getHelloResponse(Intent intent) {

        String bucketName = intent.getSlot("bucketName").getValue();
        String bucketRegion = intent.getSlot("bucketRegion").getValue();
        S3BucketVO s3BucketVO = new S3BucketVO();
        s3BucketVO.setBucketName(bucketName);
        s3BucketVO.setBucketRegion(bucketRegion);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String bucketData = gson.toJson(s3BucketVO);
        int status = callOOFlowExecutorAPI(bucketData);
        String speechText = "";
        if(status == 200) {
            speechText = "A bucket named , " + bucketName + ", has been created in " + bucketRegion;
        }
        // Create the Simple card content.
        SimpleCard card = new SimpleCard();
        card.setTitle("HelloWorld");
        card.setContent(speechText);

        // Create the plain text output.
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(speechText);

        return SpeechletResponse.newTellResponse(speech, card);
    }

    /**
     * Creates a {@code SpeechletResponse} for the help intent.
     *
     * @return SpeechletResponse spoken and visual response for the given intent
     */
    private SpeechletResponse getHelpResponse() {
        String speechText = "You can say hello to me!";

        // Create the Simple card content.
        SimpleCard card = new SimpleCard();
        card.setTitle("HelloWorld");
        card.setContent(speechText);

        // Create the plain text output.
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(speechText);

        // Create reprompt
        Reprompt reprompt = new Reprompt();
        reprompt.setOutputSpeech(speech);

        return SpeechletResponse.newAskResponse(speech, reprompt, card);
    }
    private int callOOFlowExecutorAPI(String data) {
        Response response = null;
        int status = 0;
        try {
            ResteasyClientBuilder resteasyClientBuilder = new ResteasyClientBuilder();
            resteasyClientBuilder.hostnameVerification(ResteasyClientBuilder.HostnameVerificationPolicy.ANY)
                    .maxPooledPerRoute(10)
                    .connectionPoolSize(10);
            ResteasyClient resteasyClient = resteasyClientBuilder.build();
            ResteasyWebTarget resteasyWebTarget = resteasyClient.target("https://3oju4i7ynf.execute-api.us-east-1.amazonaws.com/staging");
            response = response = resteasyWebTarget.request()
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .post(Entity.entity(data, "application/json"));
            if (!(response.getStatus() == 200)) {
                throw new Exception("Rest POST request failed with status " + response.getStatus());
            }
            //String responseString = response.readEntity(String.class);
            status = response.getStatus();
            return status;
        } catch (Exception e){
            System.out.println(e);
        } finally {
            if(response != null){
                response.close();
            }
        }
        return status;
    }
}
