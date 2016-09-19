package io.klerch.alexa.tellask.schema;

import io.klerch.alexa.tellask.model.AlexaOutput;
import io.klerch.alexa.tellask.model.AlexaInput;

/**
 * An interface for an intent handler
 */
public interface AlexaIntentHandler {
    boolean shouldHandle(final AlexaInput input);

    /**
     * This method handles an incoming intent defined in the AlexaIntentListener annotation
     * of the class derived from this interface.
     * @param input Alexa input request
     * @return Alexa output response
     * @throws Exception Can throw an exception which triggers the handleError method
     */
    AlexaOutput handleIntent(final AlexaInput input) throws Exception;

    /**
     * This method handles a failed handling of an intent.
     * @param input The original Alexa input whose handling failed
     * @param exception The exception which caused the failure (likely be thrown by the handleIntent)
     * @return Alexa output response
     */
    AlexaOutput handleError(final AlexaInput input, final Throwable exception);
}
