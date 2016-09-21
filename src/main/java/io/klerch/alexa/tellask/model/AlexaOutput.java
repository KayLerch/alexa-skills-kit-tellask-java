/**
 * Created by Kay Lerch (https://twitter.com/KayLerch)
 *
 * Contribute to https://github.com/KayLerch/alexa-skills-kit-tellask-java
 *
 * Attached license applies.
 * This source is licensed under GNU GENERAL PUBLIC LICENSE Version 3 as of 29 June 2007
 */
package io.klerch.alexa.tellask.model;

import com.amazon.speech.ui.Card;
import io.klerch.alexa.state.model.AlexaStateModel;
import io.klerch.alexa.tellask.schema.type.AlexaOutputFormat;
import io.klerch.alexa.tellask.util.resource.ResourceUtteranceReader;
import io.klerch.alexa.tellask.schema.UtteranceReader;
import org.apache.commons.lang3.Validate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

/**
 * The AlexaOutput provides all the information necessary to generate the speechlet response.
 */
public class AlexaOutput {
    /**
     * The default locale used when no locale is provided in the speechlet request
     */
    public static final String DEFAULT_LOCALE = "en-US";
    private String intentName;
    private Boolean shouldEndSession;
    private Boolean shouldReprompt;
    private final List<AlexaIntentModel> models;
    private final List<AlexaOutputSlot> slots;
    private final Card card;
    private final UtteranceReader utteranceReader;
    private final String locale;

    private AlexaOutput(final AlexaOutputBuilder builder) {
        this.intentName = builder.intentName;
        this.shouldEndSession = builder.shouldEndSession;
        this.shouldReprompt = builder.shouldReprompt;
        this.models = builder.models;
        this.card = builder.card;
        this.slots = builder.slots;
        this.utteranceReader = builder.utteranceReader;
        this.locale = builder.locale;
    }

    /**
     * name of the output intent.
     * @return name of the output intent
     */
    public String getIntentName() {
        return intentName;
    }

    /**
     * True if the session ends on giving the next speechlet response
     * @return True if the session ends on giving the next speechlet response
     */
    public Boolean shouldEndSession() {
        return shouldEndSession;
    }

    /**
     * True if the speechlet response should contain a reprompt whose texts
     * are also managed in the YAML utterances. If there is no reprompt text
     * specified for this intent in the YAML utterances no reprompt will be
     * given back to the user.
     * @return True if the speechlet response should contain a reprompt
     */
    public Boolean shouldReprompt() {
        return shouldReprompt;
    }

    /**
     * All the state models whose state will be saved automatically when
     * giving AlexaOutput to the speechlet.
     * @return state models whose state will be saved automatically
     */
    public List<AlexaIntentModel> getModels() {
        return models;
    }

    /**
     * All the slots whose values will be replaced with the corresponding placeholders
     * in the YAML utterances.
     * @return All the slots whose values will be replaced with the corresponding placeholders
     * in the YAML utterances.
     */
    public List<AlexaOutputSlot> getSlots() {
        return slots;
    }

    /**
     * Optionally a card will be attached to the speechlet response.
     * @return The card which will be attached to the speechlet response.
     */
    public Card getCard() {
        return card;
    }

    /**
     * The utterance reader is an implementation of logic reading utterances
     * from a store - likely a file sitting somewhere
     * @return The utterance reader implementation
     */
    public UtteranceReader getUtteranceReader() {
        return utteranceReader;
    }

    /**
     * The locale comes in with the speechlet request and indicates the spoken
     * language in the context the user session runs in
     * @return the locale
     */
    public String getLocale() {
        return locale;
    }

    /**
     * Entry point for creating a new AlexaOutput with its builder. A "tell" output
     * will close a session after it has been returned to the user
     * @param intentName the name of the output intent which is associated with a set
     *                   of reply utterances to choose from in the YAML utterances
     * @return a builder for creating a new AlexaOutput
     */
    public static AlexaOutputBuilder tell(final String intentName) {
        return new AlexaOutputBuilder(true, intentName);
    }

    /**
     * Entry point for creating a new AlexaOutput with its builder. An "ask" output
     * will NOT close a session after it has been returned to the user
     * @param intentName the name of the output intent which is associated with a set
     *                   of reply utterances to choose from in the YAML utterances
     * @return a builder for creating a new AlexaOutput
     */
    public static AlexaOutputBuilder ask(final String intentName) {
        return new AlexaOutputBuilder(false, intentName);
    }

    /**
     * A builder to create AlexaOutput objects
     */
    public static class AlexaOutputBuilder {
        private final String intentName;
        private final Boolean shouldEndSession;
        private Boolean shouldReprompt = false;
        private List<AlexaIntentModel> models = new ArrayList<>();
        private List<AlexaOutputSlot> slots = new ArrayList<>();
        private Card card;
        private UtteranceReader utteranceReader = new ResourceUtteranceReader();
        private String locale = AlexaOutput.DEFAULT_LOCALE;

        private Predicate<AlexaStateModel> notExists = (final AlexaStateModel model) ->
                !(this.models.stream().anyMatch(m -> m.getModel().equals(model)));

        private AlexaOutputBuilder(final Boolean shouldEndSession, final String intentName) {
            this.shouldEndSession = shouldEndSession;
            this.intentName = intentName;
        }

        /**
         * A slot provides a value to fill in a placeholder in the reply utterance.
         * When not giving this slot a specific format it will be returned as text.
         * @param slotName name of the slot equal to the placeholder-name in the reply utterance
         * @param slotValue value of the slot filling in the utterance placeholder
         * @return the AlexaOutput builder
         */
        public AlexaOutputBuilder putSlot(final String slotName, final Object slotValue) {
            slots.add(new AlexaOutputSlot(slotName, slotValue));
            return this;
        }

        /**
         * A slot provides a value to fill in a placeholder in the reply utterance.
         * @param slotName name of the slot equal to the placeholder-name in the reply utterance
         * @param slotValue value of the slot filling in the utterance placeholder
         * @param slotFormat the format a value is applied with when filling it in the utterance placeholder
         * @return the AlexaOutput builder
         */
        public AlexaOutputBuilder putSlot(final String slotName, final Object slotValue, final AlexaOutputFormat slotFormat) {
            slots.add(new AlexaOutputSlot(slotName, slotValue).formatAs(slotFormat));
            return this;
        }

        /**
         * A slot provides a value to fill in a placeholder in the reply utterance.
         * @param slot the slot object containg all the details like the slot name, value and format
         * @return the AlexaOutput builder
         */
        public AlexaOutputBuilder putSlot(final AlexaOutputSlot slot) {
            slots.add(slot);
            return this;
        }

        /**
         * One to many state models whose AlexaStateSave-annotated fields will be saved
         * to the associated state handler. If this model does not bring a handler with it
         * the speechlet handler will automatically use the AlexaSessionStateHandler to save
         * state to the local session object.
         * @param models One to many state models whose AlexaStateSave-annotated fields will be saved
         *               to the associated state handler.
         * @return the AlexaOutput builder
         */
        public AlexaOutputBuilder putState(final AlexaStateModel... models) {
            if (models == null) {
                return this;
            }
            return withDeduplicatedStateOf(Arrays.asList(models));
        }

        /**
         * One to many state models whose AlexaStateSave-annotated fields will be saved
         * to the associated state handler. If this model does not bring a handler with it
         * the speechlet handler will automatically use the AlexaSessionStateHandler to save
         * state to the local session object.
         * @param models One to many state models whose AlexaStateSave-annotated fields will be saved
         *               to the associated state handler.
         * @return the AlexaOutput builder
         */
        public AlexaOutputBuilder putState(final Collection<AlexaStateModel> models) {
            return withDeduplicatedStateOf(models);
        }

        /**
         * Overrides the locale which comes in with the speechlet request.
         * @param locale The new locale to use when replying to the user
         * @return the AlexaOutput builder
         */
        public AlexaOutputBuilder withLocale(final String locale) {
            this.locale = locale;
            return this;
        }

        private AlexaOutputBuilder withDeduplicatedStateOf(final Collection<AlexaStateModel> stateModels) {
            stateModels.stream().filter(notExists)
                    .map(AlexaIntentModel::new)
                    .forEach(models::add);
            return this;
        }

        /**
         * The card attached to the speechlet response.
         * @param card The card attached to the speechlet response.
         * @return the AlexaOutput builder
         */
        public AlexaOutputBuilder withCard(final Card card) {
            this.card = card;
            return this;
        }

        /**
         * Set true if the speechlet response should contain a reprompt whose texts
         * are also managed in the YAML utterances. If there is no reprompt text
         * specified for this intent in the YAML utterances no reprompt will be
         * given back to the user. By default this value is set to false
         * @param shouldReprompt Set true if the speechlet response should contain a reprompt
         * @return the AlexaOutput builder
         */
        public AlexaOutputBuilder withReprompt(final boolean shouldReprompt) {
            this.shouldReprompt = shouldReprompt;
            return this;
        }

        /**
         * The utterance reader is an implementation of logic reading utterances
         * from a store - likely a file sitting somewhere. By default AlexaOutput will
         * be associated with the ResourceUtteranceReader loading utterances from YAML
         * files in your /resources/{locale}/utterances.yml
         * @param reader The utterance reader implementation of your desire
         * @return the AlexaOutput builder
         */
        public AlexaOutputBuilder withReader(final UtteranceReader reader) {
            this.utteranceReader = reader;
            return this;
        }

        /**
         * Builds the AlexaOutput. Be sure you provided at least a non-blank intent-name.
         * @return the AlexaOutput builder
         */
        public AlexaOutput build() {
            Validate.notBlank(intentName, "Intent name must not be blank.");
            Validate.notNull(utteranceReader, "Utterance-reader must not be null.");
            Validate.notBlank(locale, "Locale must not be blank.");
            return new AlexaOutput(this);
        }
    }
}
