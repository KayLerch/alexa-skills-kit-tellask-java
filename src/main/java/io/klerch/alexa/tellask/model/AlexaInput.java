package io.klerch.alexa.tellask.model;

import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletRequest;
import io.klerch.alexa.state.handler.AlexaSessionStateHandler;

public class AlexaInput {
    private final AlexaSessionStateHandler sessionHandler;
    private final IntentRequest intentRequest;

    public AlexaInput(IntentRequest request, Session session) {
        this.sessionHandler = new AlexaSessionStateHandler(session);
        this.intentRequest = request;
    }

    public AlexaSessionStateHandler getSessionHandler() {
        return sessionHandler;
    }

    public IntentRequest getIntentRequest() {
        return intentRequest;
    }
}
