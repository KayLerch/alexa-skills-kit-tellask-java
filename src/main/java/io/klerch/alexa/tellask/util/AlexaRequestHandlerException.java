package io.klerch.alexa.tellask.util;

import io.klerch.alexa.tellask.model.AlexaInput;

/**
 * An exception dedicated to errors raised while handling speechlet requests.
 */
public class AlexaRequestHandlerException extends Exception {

    private static final long serialVersionUID = 1906572041950253337L;

    private final transient AlexaInput input;
    private final String errorIntent;

    /**
     * New exception with AlexaInput for giving this exception a bit more context
     * @param message the message
     * @param input the AlexaInput
     */
    public AlexaRequestHandlerException(final String message, final AlexaInput input) {
        super(message);
        this.input = input;
        this.errorIntent = null;
    }

    /**
     * New exception with AlexaInput for giving this exception a bit more context
     * @param message the message
     * @param input the AlexaInput
     * @param errorIntent the intent to use when looking for an utterance to return
     */
    public AlexaRequestHandlerException(final String message, final AlexaInput input, final String errorIntent) {
        super(message);
        this.input = input;
        this.errorIntent = errorIntent;
    }

    /**
     * New exception with AlexaInput for giving this exception a bit more context
     * @param message the message
     * @param cause the causing exception if any
     * @param input the AlexaInput
     * @param errorIntent the intent to use when looking for an utterance to return
     */
    public AlexaRequestHandlerException(final String message, final Throwable cause, final AlexaInput input, final String errorIntent) {
        super(message, cause);
        this.input = input;
        this.errorIntent = errorIntent;
    }

    /**
     * Gets the AlexaInput
     * @return the AlexaInput
     */
    public AlexaInput getInput() {
        return input;
    }

    /**
     * Gets the intent referencing utterances to express an error situation.
     * @return the intent referencing utterances to express an error situation
     */
    public String getErrorIntent() {
        return errorIntent;
    }
}
