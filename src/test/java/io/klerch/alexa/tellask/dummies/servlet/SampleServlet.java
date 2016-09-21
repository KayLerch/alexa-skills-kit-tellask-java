package io.klerch.alexa.tellask.dummies.servlet;

import io.klerch.alexa.tellask.dummies.SampleAlexaSpeechlet;
import io.klerch.alexa.tellask.model.wrapper.AlexaSpeechlet;
import io.klerch.alexa.tellask.model.wrapper.AlexaSpeechletServlet;
import io.klerch.alexa.tellask.schema.annotation.AlexaApplication;

@AlexaApplication(applicationIds = "applicationId")
public class SampleServlet extends AlexaSpeechletServlet {
    @Override
    public Class<? extends AlexaSpeechlet> getAlexaSpeechlet() {
        return SampleAlexaSpeechlet.class;
    }
}
