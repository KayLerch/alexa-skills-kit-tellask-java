/**
 * Created by Kay Lerch (https://twitter.com/KayLerch)
 *
 * Contribute to https://github.com/KayLerch/alexa-skills-kit-tellask-java
 *
 * Attached license applies.
 * This source is licensed under GNU GENERAL PUBLIC LICENSE Version 3 as of 29 June 2007
 */
package io.klerch.alexa.tellask;

import com.amazon.speech.json.SpeechletRequestEnvelope;
import com.amazon.speech.slu.Intent;
import com.amazon.speech.slu.Slot;
import com.amazon.speech.speechlet.*;
import com.amazonaws.services.lambda.runtime.ClientContext;
import com.amazonaws.services.lambda.runtime.CognitoIdentity;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import io.klerch.alexa.tellask.model.AlexaInput;
import io.klerch.alexa.tellask.model.AlexaOutput;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ModelFactory {
    public static Context givenLambdaContext() {
        return new Context() {
            @Override
            public String getAwsRequestId() {
                return null;
            }

            @Override
            public String getLogGroupName() {
                return null;
            }

            @Override
            public String getLogStreamName() {
                return null;
            }

            @Override
            public String getFunctionName() {
                return null;
            }

            @Override
            public CognitoIdentity getIdentity() {
                return null;
            }

            @Override
            public ClientContext getClientContext() {
                return null;
            }

            @Override
            public int getRemainingTimeInMillis() {
                return 0;
            }

            @Override
            public int getMemoryLimitInMB() {
                return 0;
            }

            @Override
            public LambdaLogger getLogger() {
                return null;
            }
        };
    }

    public static SpeechletRequestEnvelope givenLaunchSpeechletRequestEnvelope() {
        return givenLaunchSpeechletRequestEnvelope("applicationId");
    }

    public static SpeechletRequestEnvelope givenLaunchSpeechletRequestEnvelope(final String applicationId) {
        return SpeechletRequestEnvelope.builder()
                .withRequest(givenLaunchRequest())
                .withSession(givenSession(applicationId))
                .withVersion("1.0.0")
                .build();
    }

    public static SpeechletRequestEnvelope givenIntentSpeechletRequestEnvelope() {
        return givenIntentSpeechletRequestEnvelope("intentName", "applicationId", null);
    }

    public static SpeechletRequestEnvelope givenIntentSpeechletRequestEnvelope(final String intentName) {
        return givenIntentSpeechletRequestEnvelope(intentName, "applicationId", null);
    }

    public static SpeechletRequestEnvelope givenIntentSpeechletRequestEnvelope(final String intentName, final String applicationId, final Map<String, Slot> slots) {
        return SpeechletRequestEnvelope.builder()
                .withRequest(givenIntentRequest(intentName, slots))
                .withSession(givenSession(applicationId))
                .withVersion("1.0.0")
                .build();
    }

    public static Session givenSession() {
        return givenSession("applicationId");
    }

    public static Session givenSession(final String applicationId) {
        final Application application = new Application(applicationId);
        final User user = User.builder().withUserId("userId").withAccessToken("accessToken").build();
        return Session.builder().withSessionId("sessionId")
                .withApplication(application).withUser(user).build();
    }

    public static IntentRequest givenIntentRequest(final String intentName, final Map<String, Slot> slots) {
        Map<String, Slot> slotsForSure = slots != null ? slots : new HashMap<>();
        final Intent intent = Intent.builder()
                .withName(intentName)
                .withSlots(slotsForSure)
                .build();
        return IntentRequest.builder()
                .withRequestId("requestId")
                .withTimestamp(new Date())
                .withIntent(intent)
                .build();
    }

    public static IntentRequest givenIntentRequest(final String intentName) {
        return givenIntentRequest(intentName, null);
    }

    public static LaunchRequest givenLaunchRequest() {
        return LaunchRequest.builder()
                .withRequestId("requestId")
                .withTimestamp(new Date())
                .build();
    }

    public static SessionStartedRequest givenSessionStartedRequest() {
        return SessionStartedRequest.builder().withRequestId("requestId").withTimestamp(new Date()).build();
    }

    public static SessionEndedRequest givenSessionEndedRequest() {
        return SessionEndedRequest.builder().withRequestId("requestId").withReason(SessionEndedRequest.Reason.USER_INITIATED).withTimestamp(new Date()).build();
    }

    public static AlexaInput givenAlexaInput(final String locale, final String intentName) {
        return new AlexaInput(givenIntentRequest(intentName), givenSession(), locale);
    }

    public static AlexaOutput givenAlexaOutputWithIntent(final String intentName) {
        return AlexaOutput.ask(intentName).build();
    }
}
