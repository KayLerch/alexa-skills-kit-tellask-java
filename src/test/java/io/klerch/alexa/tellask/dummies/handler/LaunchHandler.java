package io.klerch.alexa.tellask.dummies.handler;

import io.klerch.alexa.tellask.model.AlexaInput;
import io.klerch.alexa.tellask.model.AlexaOutput;
import io.klerch.alexa.tellask.schema.AlexaLaunchHandler;
import io.klerch.alexa.tellask.schema.AlexaLaunchListener;
import io.klerch.alexa.tellask.util.AlexaRequestHandlerException;

@AlexaLaunchListener
public class LaunchHandler implements AlexaLaunchHandler {

    @Override
    public AlexaOutput handleRequest(AlexaInput alexaInput) throws AlexaRequestHandlerException {
        return AlexaOutput.ask("IntentWithNoSlots").withReprompt(true).build();
    }

    @Override
    public AlexaOutput handleError(AlexaRequestHandlerException e) {
        return AlexaOutput.ask("SaySorry").build();
    }
}
