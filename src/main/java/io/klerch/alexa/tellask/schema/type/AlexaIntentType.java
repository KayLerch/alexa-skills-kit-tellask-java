/**
 * Created by Kay Lerch (https://twitter.com/KayLerch)
 *
 * Contribute to https://github.com/KayLerch/alexa-skills-kit-tellask-java
 *
 * Attached license applies.
 * This source is licensed under GNU GENERAL PUBLIC LICENSE Version 3 as of 29 June 2007
 */
package io.klerch.alexa.tellask.schema.type;

/**
 * Enumeration of Alexa Intent types whose value is equal to the qualified
 * name of the intent in Alexa.
 */
public enum AlexaIntentType {
    /**
     * User is requesting help
     */
    INTENT_HELP("AMAZON.HelpIntent"),
    /**
     * User asks for next step in a certain context.
     */
    INTENT_NEXT("AMAZON.NextIntent"),
    /**
     * User negates a question in a certain context.
     */
    INTENT_NO("AMAZON.NoIntent"),
    /**
     * User is requesting to repeat something in a certain context.
     */
    INTENT_REPEAT("AMAZON.RepeatIntent"),
    /**
     * User is requesting to start over something in a certain context.
     */
    INTENT_STARTOVER("AMAZON.StartOverIntent"),
    /**
     * User is requesting to cancel a certain context.
     */
    INTENT_CANCEL("AMAZON.CancelIntent"),
    /**
     * User is requesting to stop the overall procedure in a certain context.
     */
    INTENT_STOP("AMAZON.StopIntent"),
    /**
     * User confirms to a question in a certain context.
     */
    INTENT_YES("AMAZON.YesIntent"),
    /*
    new intents won't be supported unless Skills Kit SDK cannot handle missing session

    INTENT_LOOP_OFF("AMAZON.LoopOffIntent"),
    INTENT_LOOP_ON("AMAZON.LoopOnIntent"),
    INTENT_PAUSE("AMAZON.PauseIntent"),
    INTENT_PREVIOUS("AMAZON.PreviousIntent"),
    INTENT_RESUME("AMAZON.ResumeIntent"),
    INTENT_SHUFFLE_OFF("AMAZON.ShuffleOffIntent"),
    INTENT_SHUFFLE_ON("AMAZON.ShuffleOnIntent"),*/
    /**
     * User kicks off a custom intent with an utterance. Don't forget to provide
     * the custom intent-name.
     */
    INTENT_CUSTOM("");

    private String name;

    AlexaIntentType(final String name) {
        this.name = name;
    }

    /**
     * Gets the qualified name of the intent in Alexa.
     * @return qualified name of the intent in Alexa
     */
    public String getName() {
        return name;
    }
}
