package io.klerch.alexa.tellask.schema;


public enum AlexaIntentType {
    INTENT_HELP("AMAZON.HelpIntent"),
    INTENT_NEXT("AMAZON.NextIntent"),
    INTENT_NO("AMAZON.NoIntent"),
    INTENT_REPEAT("AMAZON.RepeatIntent"),
    INTENT_STARTOVER("AMAZON.StartOverIntent"),
    INTENT_CANCEL("AMAZON.CancelIntent"),
    INTENT_STOP("AMAZON.StopIntent"),
    INTENT_YES("AMAZON.YesIntent"),
    INTENT_CUSTOM("");

    private String name;

    AlexaIntentType(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
