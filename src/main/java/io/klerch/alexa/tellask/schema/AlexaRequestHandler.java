/**
 * Created by Kay Lerch (https://twitter.com/KayLerch)
 *
 * Contribute to https://github.com/KayLerch/alexa-skills-kit-tellask-java
 *
 * Attached license applies.
 * This source is licensed under GNU GENERAL PUBLIC LICENSE Version 3 as of 29 June 2007
 */
package io.klerch.alexa.tellask.schema;

import io.klerch.alexa.state.utils.AlexaStateException;
import io.klerch.alexa.tellask.model.AlexaInput;
import io.klerch.alexa.tellask.model.AlexaOutput;
import io.klerch.alexa.tellask.util.AlexaRequestHandlerException;

/**
 * An interface for a request handler. The handler will only be considered to handle a launch request
 * by tagging the implementation class with the AlexaLaunchListener-annotation.
 */
public interface AlexaRequestHandler {
    /**
     * This method handles an incoming speechlet request.
     * @param input Alexa input request
     * @return Alexa output response
     * @throws AlexaRequestHandlerException Can throw any exception related to the request handling
     * @throws AlexaStateException Can throw any exception related to handling state with State SDK.
     * As you might use this SDK in your request handler you don't have to care about these exceptions.
     * They will also be catched by the calling speechlet request handler and will be given to the handleError
     * method wrapped in a AlexaRequestHandlerException.
     */
    AlexaOutput handleRequest(final AlexaInput input) throws AlexaRequestHandlerException, AlexaStateException;

    /**
     * This method handles a failed handling of a speechlet request event.
     * @param exception The exception which caused the failure (likely be thrown by the handleIntent)
     * @return Alexa output response
     */
    AlexaOutput handleError(final AlexaRequestHandlerException exception);
}
