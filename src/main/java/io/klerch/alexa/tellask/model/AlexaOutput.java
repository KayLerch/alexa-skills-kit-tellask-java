package io.klerch.alexa.tellask.model;

import com.amazon.speech.ui.Card;
import io.klerch.alexa.state.model.AlexaStateModel;
import io.klerch.alexa.tellask.schema.AlexaOutputFormat;
import io.klerch.alexa.tellask.util.ResourceUtteranceReader;
import io.klerch.alexa.tellask.schema.UtteranceReader;
import org.apache.commons.lang3.Validate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

public class AlexaOutput {
    private String intentName;
    private Boolean shouldEndSession;
    private Boolean shouldReprompt;
    private final List<AlexaIntentModel> models;
    private final List<AlexaOutputSlot> slots;
    private final Card card;
    private final UtteranceReader utteranceReader;
    private final String locale;

    private AlexaOutput(final AlexaResponseBuilder builder) {
        this.intentName = builder.intentName;
        this.shouldEndSession = builder.shouldEndSession;
        this.shouldReprompt = builder.shouldReprompt;
        this.models = builder.models;
        this.card = builder.card;
        this.slots = builder.slots;
        this.utteranceReader = builder.utteranceReader;
        this.locale = builder.locale;
    }

    public String getIntentName() {
        return intentName;
    }

    public Boolean shouldEndSession() {
        return shouldEndSession;
    }

    public Boolean shouldReprompt() {
        return shouldReprompt;
    }

    public List<AlexaIntentModel> getModels() {
        return models;
    }

    public List<AlexaOutputSlot> getSlots() {
        return slots;
    }

    public Card getCard() {
        return card;
    }

    public UtteranceReader getUtteranceReader() {
        return utteranceReader;
    }

    public String getLocale() {
        return locale;
    }

    public static AlexaResponseBuilder tell(final String intentName) {
        return new AlexaResponseBuilder(true, intentName);
    }

    public static AlexaResponseBuilder ask(final String intentName) {
        return new AlexaResponseBuilder(false, intentName);
    }

    public static class AlexaResponseBuilder {
        private final String intentName;
        private final Boolean shouldEndSession;
        private Boolean shouldReprompt = false;
        private List<AlexaIntentModel> models = new ArrayList<>();
        private List<AlexaOutputSlot> slots = new ArrayList<>();
        private Card card;
        private UtteranceReader utteranceReader = new ResourceUtteranceReader();
        private String locale = "en-US";

        private AlexaResponseBuilder(final Boolean shouldEndSession, final String intentName) {
            this.shouldEndSession = shouldEndSession;
            this.intentName = intentName;
        }

        public AlexaResponseBuilder putSlot(final String slotName, final Object slotValue) {
            slots.add(new AlexaOutputSlot(slotName, slotValue));
            return this;
        }

        public AlexaResponseBuilder putSlot(final String slotName, final Object slotValue, final AlexaOutputFormat slotFormat) {
            slots.add(new AlexaOutputSlot(slotName, slotValue).formatAs(slotFormat));
            return this;
        }

        public AlexaResponseBuilder putSlot(final AlexaOutputSlot slot) {
            slots.add(slot);
            return this;
        }

        public AlexaResponseBuilder putState(final AlexaStateModel... models) {
            return withDeduplicatedStateOf(Arrays.asList(models));
        }

        public AlexaResponseBuilder putState(final Collection<AlexaStateModel> models) {
            return withDeduplicatedStateOf(models);
        }

        public AlexaResponseBuilder withLocale(final String locale) {
            this.locale = locale;
            return this;
        }

        private Predicate<AlexaStateModel> notExists = ((final AlexaStateModel model) ->
                !(this.models.stream().anyMatch(m -> m.getModel().equals(model))));


        private AlexaResponseBuilder withDeduplicatedStateOf(final Collection<AlexaStateModel> stateModels) {
            stateModels.stream().filter(notExists)
                    .map(AlexaIntentModel::new)
                    .forEach(models::add);
            return this;
        }

        public AlexaResponseBuilder withCard(final Card card) {
            this.card = card;
            return this;
        }

        public AlexaResponseBuilder withReprompt(final boolean shouldReprompt) {
            this.shouldReprompt = shouldReprompt;
            return this;
        }

        public AlexaResponseBuilder withReader(final UtteranceReader reader) {
            this.utteranceReader = reader;
            return this;
        }

        public AlexaOutput build() {
            Validate.notBlank(intentName, "Intent name must not be blank.");
            Validate.notNull(utteranceReader, "Utterance-reader must not be null.");
            Validate.notBlank(locale, "Locale must not be blank.");
            return new AlexaOutput(this);
        }
    }
}
