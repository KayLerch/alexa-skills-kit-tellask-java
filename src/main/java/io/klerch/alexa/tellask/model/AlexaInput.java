package io.klerch.alexa.tellask.model;

import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletRequest;
import io.klerch.alexa.state.handler.AlexaSessionStateHandler;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.util.Optional;

/**
 * This is the input for an intent request handing in all necessary information
 * for handling the intent properly. Its counterpart is the AlexaOutput object.
 */
public class AlexaInput {
    private final AlexaSessionStateHandler sessionHandler;
    private final IntentRequest intentRequest;
    private final LaunchRequest launchRequest;
    private final String locale;

    /**
     * Creates a new Alexa input giving it all information from an actual Speechlet request
     * @param request the intent request
     * @param session the session object
     * @param locale the locale of the request
     */
    public AlexaInput(final IntentRequest request, final Session session, final String locale) {
        this.sessionHandler = new AlexaSessionStateHandler(session);
        this.intentRequest = request;
        this.launchRequest = null;
        this.locale = locale;
    }

    /**
     * Creates a new Alexa input giving it all information from an actual Speechlet request
     * @param request the intent request
     * @param session the session object
     * @param locale the locale of the request
     */
    public AlexaInput(final LaunchRequest request, final Session session, final String locale) {
        this.sessionHandler = new AlexaSessionStateHandler(session);
        this.intentRequest = null;
        this.launchRequest = request;
        this.locale = locale;
    }

    public String getIntentName() {
        return intentRequest != null ? intentRequest.getIntent().getName() : null;
    }

    /**
     * Returns the SessionHandler for reading state from and writing state to the session object
     * @return SessionHandler for current session.
     */
    public AlexaSessionStateHandler getSessionHandler() {
        return sessionHandler;
    }

    /**
     * The speechlet request to handle. This should be either an intent request or launch request
     * @return The speechlet request.
     */
    public SpeechletRequest getRequest() {
        return intentRequest != null ? intentRequest : launchRequest;
    }


    /**
     * The locale given by the request (e.g. en-US)
     * @return The locale given by the request (e.g. en-US)
     */
    public String getLocale() {
        return locale;
    }

    /**
     * Checks if a slot is contained in the intent request. This is no guarantee that the slot
     * value is not null.
     * @param slotName name of the slot to look after
     * @return True, if the slot exists in the intent request
     */
    public boolean hasSlot(final String slotName) {
        return intentRequest != null && intentRequest.getIntent().getSlots().containsKey(slotName);
    }

    /**
     * Checks if a slot is contained in the intent request and its value is not blank.
     * @param slotName name of the slot to look after
     * @return True, if the slot exists in the intent request and is not blank.
     */
    public boolean hasSlotNotBlank(final String slotName) {
        return hasSlot(slotName) && StringUtils.isNotBlank(intentRequest.getIntent().getSlot(slotName).getValue());
    }

    /**
     * Checks if a slot is contained in the intent request and its value is a number.
     * @param slotName name of the slot to look after
     * @return True, if the slot exists in the intent request and is a number.
     */
    public boolean hasSlotIsNumber(final String slotName) {
        return hasSlot(slotName) && StringUtils.isNumeric(intentRequest.getIntent().getSlot(slotName).getValue());
    }

    /**
     * Checks if a slot is contained in the intent request and its value represents a boolean true
     * (which could be "on" or "yes" as well)
     * @param slotName name of the slot to look after
     * @return True, if the slot exists in the intent request and represents a boolean true.
     */
    public boolean hasSlotIsTrue(final String slotName) {
        return hasSlot(slotName) && BooleanUtils.toBoolean(intentRequest.getIntent().getSlot(slotName).getValue());
    }

    /**
     * Gets a slot's value casted to the given type or returns nothing if slot is not existent.
     * @param slotValueType Class of a type to cast slot value to
     * @param slotName name of the slot to look after
     * @param <T> Type to cast slot value to
     * @return Typed value of the slot
     */
    @SuppressWarnings("unchecked")
    public <T> Optional<T> getSlotValueAs(Class<T> slotValueType, final String slotName) {
        return hasSlot(slotName) ? Optional.of((T)intentRequest.getIntent().getSlot(slotName).getValue()) : Optional.empty();
    }
}
