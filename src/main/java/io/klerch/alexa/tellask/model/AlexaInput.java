package io.klerch.alexa.tellask.model;

import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.Session;
import io.klerch.alexa.state.handler.AlexaSessionStateHandler;

import java.util.Optional;

public class AlexaInput {
    private final AlexaSessionStateHandler sessionHandler;
    private final IntentRequest intentRequest;
    private final String locale;

    public AlexaInput(final IntentRequest request, final Session session, final String locale) {
        this.sessionHandler = new AlexaSessionStateHandler(session);
        this.intentRequest = request;
        this.locale = locale;
    }

    public AlexaSessionStateHandler getSessionHandler() {
        return sessionHandler;
    }

    public IntentRequest getIntentRequest() {
        return intentRequest;
    }

    public boolean hasSlot(final String slotName) {
        return intentRequest.getIntent().getSlots().containsKey(slotName);
    }

    @SuppressWarnings("unchecked")
    public <T> Optional<T> getSlotValueAs(Class<T> slotValueType, final String slotName) {
        return hasSlot(slotName) ? Optional.of((T)intentRequest.getIntent().getSlot(slotName).getValue()) : Optional.empty();
    }

    public String getLocale() {
        return locale;
    }
}
