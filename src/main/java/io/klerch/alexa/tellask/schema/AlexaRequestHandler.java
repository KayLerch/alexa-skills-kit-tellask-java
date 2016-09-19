package io.klerch.alexa.tellask.schema;

import io.klerch.alexa.tellask.model.AlexaInput;
import io.klerch.alexa.tellask.model.AlexaOutput;

/**
 * An interface for a request handler. The handler will only be considered to handle a launch request
 * by tagging the implementation class with the AlexaLaunchListener-annotation.
 */
public interface AlexaRequestHandler {
    /**
     * This method handles an incoming speechlet request.
     * @param input Alexa input request
     * @return Alexa output response
     * @throws Exception Can throw an exception which triggers the handleError method
     */
    AlexaOutput handleRequest(final AlexaInput input) throws Exception;

    /**
     * This method handles a failed handling of a speechlet request event.
     * @param input The original Alexa input whose handling failed
     * @param exception The exception which caused the failure (likely be thrown by the handleIntent)
     * @return Alexa output response
     */
    AlexaOutput handleError(final AlexaInput input, final Throwable exception);
}
