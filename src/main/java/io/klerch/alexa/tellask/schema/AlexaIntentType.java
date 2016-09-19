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
    INTENT_LOOP_OFF("AMAZON.LoopOffIntent"),
    INTENT_LOOP_ON("AMAZON.LoopOnIntent"),
    INTENT_PAUSE("AMAZON.PauseIntent"),
    INTENT_PREVIOUS("AMAZON.PreviousIntent"),
    INTENT_RESUME("AMAZON.ResumeIntent"),
    INTENT_SHUFFLE_OFF("AMAZON.ShuffleOffIntent"),
    INTENT_SHUFFLE_ON("AMAZON.ShuffleOnIntent"),
    INTENT_CUSTOM("");

    private String name;

    AlexaIntentType(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
