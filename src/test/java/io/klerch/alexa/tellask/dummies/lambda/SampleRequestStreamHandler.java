/**
 * Created by Kay Lerch (https://twitter.com/KayLerch)
 *
 * Contribute to https://github.com/KayLerch/alexa-skills-kit-tellask-java
 *
 * Attached license applies.
 * This source is licensed under GNU GENERAL PUBLIC LICENSE Version 3 as of 29 June 2007
 */
package io.klerch.alexa.tellask.dummies.lambda;

import io.klerch.alexa.tellask.dummies.SampleAlexaSpeechlet;
import io.klerch.alexa.tellask.model.wrapper.AlexaRequestStreamHandler;
import io.klerch.alexa.tellask.model.wrapper.AlexaSpeechlet;
import io.klerch.alexa.tellask.schema.annotation.AlexaApplication;

@AlexaApplication(applicationIds = "applicationId")
public class SampleRequestStreamHandler extends AlexaRequestStreamHandler {
    @Override
    public Class<? extends AlexaSpeechlet> getSpeechlet() {
        return SampleAlexaSpeechlet.class;
    }
}
