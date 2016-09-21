/**
 * Created by Kay Lerch (https://twitter.com/KayLerch)
 *
 * Contribute to https://github.com/KayLerch/alexa-skills-kit-tellask-java
 *
 * Attached license applies.
 * This source is licensed under GNU GENERAL PUBLIC LICENSE Version 3 as of 29 June 2007
 */
package handler;

import io.klerch.alexa.tellask.model.AlexaInput;
import io.klerch.alexa.tellask.model.AlexaOutput;
import io.klerch.alexa.tellask.schema.AlexaLaunchHandler;
import io.klerch.alexa.tellask.schema.annotation.AlexaLaunchListener;
import io.klerch.alexa.tellask.util.AlexaRequestHandlerException;

@AlexaLaunchListener
public class ValidLaunchHandler implements AlexaLaunchHandler {

    @Override
    public AlexaOutput handleRequest(AlexaInput alexaInput) throws AlexaRequestHandlerException {
        return AlexaOutput.ask("IntentWithNoSlots").withReprompt(true).build();
    }

    @Override
    public AlexaOutput handleError(AlexaRequestHandlerException e) {
        return AlexaOutput.ask("SaySorry").build();
    }
}
