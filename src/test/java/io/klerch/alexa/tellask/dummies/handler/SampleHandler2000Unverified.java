package io.klerch.alexa.tellask.dummies.handler;

import io.klerch.alexa.tellask.model.AlexaInput;
import io.klerch.alexa.tellask.model.AlexaOutput;
import io.klerch.alexa.tellask.schema.AlexaIntentHandler;
import io.klerch.alexa.tellask.schema.AlexaIntentListener;
import io.klerch.alexa.tellask.util.AlexaRequestHandlerException;

@AlexaIntentListener(intentName = "IntentWithOneUtteranceAndOneReprompt", priority = 2000)
public class SampleHandler2000Unverified implements AlexaIntentHandler {
    @Override
    public boolean verify(AlexaInput alexaInput) {
        return false;
    }

    @Override
    public AlexaOutput handleRequest(final AlexaInput alexaInput) throws AlexaRequestHandlerException {
        return AlexaOutput.ask("Plus").build();
    }

    @Override
    public AlexaOutput handleError(AlexaRequestHandlerException exception) {
        return AlexaOutput.tell("SaySorry").build();
    }
}
