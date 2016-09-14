package io.klerch.alexa.tellask.model;

import io.klerch.alexa.tellask.schema.AlexaIntentHandler;

public class AlexaIntentHandlerFactory {
    static AlexaIntentHandler createHandler(final AlexaInput input) {
        // TODO: create new handler based on input
        if ("intent".equals(input.getIntentRequest().getIntent().getName())) {
            return null;
        }
        return null;
    }
}
