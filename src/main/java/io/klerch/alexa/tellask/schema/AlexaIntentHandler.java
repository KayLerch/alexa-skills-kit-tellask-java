package io.klerch.alexa.tellask.schema;

import io.klerch.alexa.tellask.model.AlexaInput;

/**
 * An interface for an intent handler. The handler will only be considered to handle an intent
 * by tagging the implementation class with the AlexaIntentListener-annotation.
 */
public interface AlexaIntentHandler extends AlexaRequestHandler {
    /**
     * This method verifies an input should be handled by this handler. In case you have
     * more handlers for the same intent you can distinguish between them by checking the
     * input for certain conditions (e.g. a slot exists). If you want this handler to be
     * selected whenever an intent appears simply return true. Returning true will invoke
     * the handleIntent method of this handler unless there is no handler for same intent which
     * also returns true in shouldHandle and has a higher priority (set in the AlexaIntentListener-annotation)
     * @param input input request
     * @return true if this handler should handle the request with its handleIntent method.
     */
    boolean shouldHandle(final AlexaInput input);
}
