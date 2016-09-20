package io.klerch.alexa.tellask.dummies;

import io.klerch.alexa.tellask.model.AlexaRequestStreamHandler;
import io.klerch.alexa.tellask.model.AlexaSpeechlet;
import io.klerch.alexa.tellask.schema.AlexaApplication;

@AlexaApplication(applicationIds = "applicationId")
public class SampleRequestStreamHandler extends AlexaRequestStreamHandler {
    @Override
    public Class<? extends AlexaSpeechlet> getSpeechlet() {
        return SampleAlexaSpeechlet.class;
    }
}
