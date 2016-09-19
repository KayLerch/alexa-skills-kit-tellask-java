package io.klerch.alexa.tellask;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.*;
import io.klerch.alexa.tellask.model.AlexaOutput;

import java.util.Date;

public class ModelFactory {
    public static Session givenSession() {
        final Application application = new Application("applicationId2");
        final User user = User.builder().withUserId("userId").withAccessToken("accessToken").build();
        return Session.builder().withSessionId("sessionId")
                .withApplication(application).withUser(user).build();
    }

    public static IntentRequest givenIntentRequest() {
        final Intent intent = Intent.builder().withName("IntentName").build();
        return IntentRequest.builder()
                .withRequestId("requestId")
                .withTimestamp(new Date())
                .withIntent(intent)
                .build();
    }

    public static LaunchRequest givenLaunchRequest() {
        return LaunchRequest.builder()
                .withRequestId("requestId")
                .withTimestamp(new Date())
                .build();
    }

    public static AlexaOutput givenAlexaOutputWithIntent(final String intentName) {
        return AlexaOutput.ask(intentName).build();
    }
}
