/**
 * Created by Kay Lerch (https://twitter.com/KayLerch)
 *
 * Contribute to https://github.com/KayLerch/alexa-skills-kit-tellask-java
 *
 * Attached license applies.
 * This source is licensed under GNU GENERAL PUBLIC LICENSE Version 3 as of 29 June 2007
 */
package io.klerch.alexa.tellask.dummies.handler;

import io.klerch.alexa.tellask.dummies.model.AlexaStateModelSample;
import io.klerch.alexa.tellask.model.AlexaInput;
import io.klerch.alexa.tellask.model.AlexaOutput;
import io.klerch.alexa.tellask.model.AlexaOutputSlot;
import io.klerch.alexa.tellask.schema.AlexaIntentHandler;
import io.klerch.alexa.tellask.schema.annotation.AlexaIntentListener;
import io.klerch.alexa.tellask.schema.type.AlexaOutputFormat;
import io.klerch.alexa.tellask.util.AlexaRequestHandlerException;

@AlexaIntentListener(customIntents = "IntentWithOneUtteranceAndOneReprompt", priority = 1000)
public class SampleHandler1000 implements AlexaIntentHandler {
    @Override
    public boolean verify(AlexaInput alexaInput) {
        return true;
    }

    @Override
    public AlexaOutput handleRequest(final AlexaInput alexaInput) throws AlexaRequestHandlerException {
        final String name = alexaInput.getSlotValue("name");

        if (!alexaInput.hasSlotIsNumber("credits")) {
            throw new AlexaRequestHandlerException("Credits is no number.", alexaInput);
        }

        final String credits = alexaInput.getSlotValue("credits");

        final AlexaStateModelSample model = new AlexaStateModelSample();
        model.setName(name);

        return AlexaOutput.ask("IntentWithOneUtteranceAndOneReprompt")
                .putState(model)
                .putSlot(new AlexaOutputSlot("credits", credits).formatAs(AlexaOutputFormat.NUMBER))
                .withReprompt(true)
                .build();
    }

    @Override
    public AlexaOutput handleError(AlexaRequestHandlerException exception) {
        return AlexaOutput.tell("SaySorry").build();
    }
}
