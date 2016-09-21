package io.klerch.alexa.tellask.dummies.lambda;

import io.klerch.alexa.tellask.dummies.SampleAlexaSpeechlet;
import io.klerch.alexa.tellask.model.wrapper.AlexaRequestStreamHandler;
import io.klerch.alexa.tellask.schema.annotation.AlexaApplication;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@AlexaApplication(speechlet = SampleAlexaSpeechlet.class)
public class SampleRequestStreamHandler2 extends AlexaRequestStreamHandler {
    @Override
    public Set<String> getSupportedApplicationIds() {
        return Arrays.asList("applicationId").stream().collect(Collectors.toSet());
    }
}
