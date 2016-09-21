package io.klerch.alexa.tellask.dummies.servlet;

import io.klerch.alexa.tellask.dummies.SampleAlexaSpeechlet;
import io.klerch.alexa.tellask.model.wrapper.AlexaSpeechletServlet;
import io.klerch.alexa.tellask.schema.annotation.AlexaApplication;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@AlexaApplication(speechlet = SampleAlexaSpeechlet.class)
public class SampleServlet2 extends AlexaSpeechletServlet {
    @Override
    public Set<String> getSupportedApplicationIds() {
        return Arrays.asList("applicationId").stream().collect(Collectors.toSet());
    }
}
