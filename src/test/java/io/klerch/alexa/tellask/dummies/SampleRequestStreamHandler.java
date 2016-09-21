package io.klerch.alexa.tellask.dummies;

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
