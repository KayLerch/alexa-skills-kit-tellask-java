package io.klerch.alexa.tellask.dummies;

import io.klerch.alexa.tellask.model.AlexaRequestStreamHandler;
import io.klerch.alexa.tellask.schema.AlexaApplication;

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
