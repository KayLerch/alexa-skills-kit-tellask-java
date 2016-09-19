package io.klerch.alexa.tellask.model;

import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletRequestHandler;
import com.amazon.speech.speechlet.SpeechletRequestHandlerException;
import com.amazon.speech.speechlet.verifier.*;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.klerch.alexa.tellask.schema.AlexaApplication;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AlexaRequestStreamHandler implements RequestStreamHandler {

    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode parser = mapper.readTree(input);
        // ...
        final String locale = "en-US";
        final AlexaSpeechlet speechlet = new AlexaSpeechlet(locale);

        final SpeechletRequestHandler handler = getRequestStreamHandler();
        byte[] serializedSpeechletRequest = IOUtils.toByteArray(input);
        byte[] outputBytes;
        try {
            outputBytes =
                    handler.handleSpeechletCall(speechlet,
                            serializedSpeechletRequest);
        } catch (SpeechletRequestHandlerException | SpeechletException ex) {
            throw new RuntimeException(ex);
        }
        output.write(outputBytes);
    }

    private SpeechletRequestHandler getRequestStreamHandler() {
        final AlexaApplication app = this.getClass().getAnnotation(AlexaApplication.class);

        Validate.notNull(app, "Could not find application configuration. Please make sure you used " + AlexaApplication.class.getSimpleName() + "-annotation in your " + AlexaRequestStreamHandler.class.getSimpleName());

        final Set<String> supportedApplicationIds = Stream.of(app.ApplicationIds()).collect(Collectors.toSet());

        return new SpeechletRequestHandler(
                Arrays.<SpeechletRequestVerifier>asList(
                        new ApplicationIdSpeechletRequestVerifier(supportedApplicationIds)),
                Arrays.asList(
                        new ResponseSizeSpeechletResponseVerifier(),
                        new OutputSpeechSpeechletResponseVerifier(),
                        new CardSpeechletResponseVerifier()));
    }
}
