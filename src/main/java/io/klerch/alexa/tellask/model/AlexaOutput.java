package io.klerch.alexa.tellask.model;

import com.amazon.speech.ui.Card;
import io.klerch.alexa.tellask.util.ResourceUtteranceReader;
import io.klerch.alexa.tellask.schema.UtteranceReader;
import org.apache.commons.lang3.Validate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class AlexaOutput {
    private String intentName;
    private Boolean shouldEndSession;
    private Boolean shouldReprompt;
    private final List<AlexaIntentModel> models;
    private final List<AlexaOutputSlot> slots;
    private final Card card;
    private final UtteranceReader utteranceReader;

    private AlexaOutput(final AlexaResponseBuilder builder) {
        this.intentName = builder.intentName;
        this.shouldEndSession = builder.shouldEndSession;
        this.shouldReprompt = builder.shouldReprompt;
        this.models = builder.models;
        this.card = builder.card;
        this.slots = builder.slots;
        this.utteranceReader = builder.utteranceReader;
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

        private AlexaResponseBuilder(final Boolean shouldEndSession, final String intentName) {
            this.shouldEndSession = shouldEndSession;
            this.intentName = intentName;
        }

        public AlexaResponseBuilder withSlot(final AlexaOutputSlot slot) {
            slots.add(slot);
            return this;
        }

        public AlexaResponseBuilder withStateOf(final AlexaIntentModel... models) {
            return withDeduplicatedStateOf(Arrays.asList(models));
        }

        public AlexaResponseBuilder withStateOf(final Collection<AlexaIntentModel> models) {
            return withDeduplicatedStateOf(models);
        }

        private AlexaResponseBuilder withDeduplicatedStateOf(final Collection<AlexaIntentModel> models) {
            models.stream().filter(model -> !this.models.contains(model)).forEach(this.models::add);
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
            return new AlexaOutput(this);
        }
    }
}
