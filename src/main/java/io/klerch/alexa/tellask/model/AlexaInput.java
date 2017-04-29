/**
 * Created by Kay Lerch (https://twitter.com/KayLerch)
 *
 * Contribute to https://github.com/KayLerch/alexa-skills-kit-tellask-java
 *
 * Attached license applies.
 * This source is licensed under GNU GENERAL PUBLIC LICENSE Version 3 as of 29 June 2007
 */
package io.klerch.alexa.tellask.model;

import com.amazon.speech.speechlet.Context;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletRequest;
import io.klerch.alexa.state.handler.AlexaSessionStateHandler;
import io.klerch.alexa.state.handler.AlexaStateHandler;
import org.apache.commons.codec.language.ColognePhonetic;
import org.apache.commons.codec.language.DoubleMetaphone;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * This is the input for an intent request handing in all necessary information
 * for handling the intent properly. Its counterpart is the AlexaOutput object.
 */
public class AlexaInput {
    private final AlexaStateHandler sessionStateHandler;
    private IntentRequest intentRequest;
    private LaunchRequest launchRequest;
    private Context context;
    private final String locale;

    /**
     * Creates a new Alexa input giving it all information from an actual speechlet request
     * @param request the intent request
     * @param session the session object
     * @param locale the locale of the request
     * @param context the Alexa service context
     */
    public AlexaInput(final IntentRequest request, final Session session, final String locale, final Context context) {
        this(session, locale, context);
        this.intentRequest = request;
        this.launchRequest = null;
    }

    /**
     * Creates a new Alexa input giving it all information from an actual speechlet request
     * @param request the intent request
     * @param session the session object
     * @param locale the locale of the request
     * @param context the Alexa service context
     */
    public AlexaInput(final LaunchRequest request, final Session session, final String locale, final Context context) {
        this(session, locale, context);
        this.intentRequest = null;
        this.launchRequest = request;
    }

    private AlexaInput(final Session session, final String locale, final Context context) {
        this.sessionStateHandler = new AlexaSessionStateHandler(session);
        this.locale = locale;
        this.context = context;
    }

    /**
     * Gets the intent name
     * @return the intent name
     */
    public String getIntentName() {
        return intentRequest != null ? intentRequest.getIntent().getName() : null;
    }

    /**
     * Returns the SessionHandler for reading state from and writing state to the session object
     * @return SessionHandler for current session.
     */
    public AlexaStateHandler getSessionStateHandler() {
        return sessionStateHandler;
    }

    /**
     * The speechlet request to handle. This should be either an intent request or launch request
     * @return The speechlet request.
     */
    public SpeechletRequest getRequest() {
        return intentRequest != null ? intentRequest : launchRequest;
    }

    /**
     * Context object with information about the current state of Alexa service and device at the time of the request.
     * @return The context object.
     */
    public Context getContext() {
        return context;
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
     * Checks if a slot is contained in the intent request and also got the value provided.
     * @param slotName name of the slot to look after
     * @param value the value
     * @return True, if slot with slotName has a value equal to the given value
     */
    public boolean hasSlotIsEqual(final String slotName, final String value) {
        final String slotValue = getSlotValue(slotName);
        return (slotValue != null && slotValue.equals(value)) ||
                slotValue == value;
    }

    /**
     * Checks if a slot is contained in the intent request and has a value which is a
     * phonetic sibling of the string given to this method. Double metaphone algorithm
     * is optimized for English language and in this case is used to match slot value with
     * value given to this method.
     * @param slotName name of the slot to look after
     * @param value the value
     * @return True, if slot value and given value are phonetically equal with Double metaphone algorithm
     */
    public boolean hasSlotIsDoubleMetaphoneEqual(final String slotName, final String value) {
        final String slotValue = getSlotValue(slotName);
        return hasSlotNotBlank(slotName) && value != null &&
                new DoubleMetaphone().isDoubleMetaphoneEqual(slotValue, value);
    }

    /**
     * Checks if a slot is contained in the intent request and has a value which is a
     * phonetic sibling of the string given to this method. Cologne phonetic algorithm
     * is optimized for German language and in this case is used to match slot value with
     * value given to this method.
     * @param slotName name of the slot to look after
     * @param value the value
     * @return True, if slot value and given value are phonetically equal with Cologne phonetic algorithm
     */
    public boolean hasSlotIsCologneEqual(final String slotName, final String value) {
        final String slotValue = getSlotValue(slotName);
        return hasSlotNotBlank(slotName) && value != null &&
                new ColognePhonetic().isEncodeEqual(slotValue, value);
    }

    /**
     * Checks if a slot is contained in the intent request and has a value which is a
     * phonetic sibling of the string given to this method. This method picks the correct
     * algorithm depending on the locale coming in with the speechlet request. For example the
     * German locale compares the slot value and the given value with the Cologne phonetic
     * algorithm whereas english locales result in this method using the Double Metaphone algorithm.
     * @param slotName name of the slot to look after
     * @param value the value
     * @return True, if slot value and given value are phonetically equal
     */
    public boolean hasSlotIsPhoneticallyEqual(final String slotName, final String value) {
        return getLocale().equals("de-DE") ? hasSlotIsCologneEqual(slotName, value) :
                hasSlotIsDoubleMetaphoneEqual(slotName, value);
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
        return hasSlot(slotName) && NumberUtils.isNumber(intentRequest.getIntent().getSlot(slotName).getValue());
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
     * Gets the slot value as a string in case slot exists otherwise null
     * @param slotName name of the slot to look after
     * @return slot value as a string in case slot exists otherwise null
     */
    public String getSlotValue(final String slotName) {
        return hasSlot(slotName) ? intentRequest.getIntent().getSlot(slotName).getValue() : null;
    }
}
