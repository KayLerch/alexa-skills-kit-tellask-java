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
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class AlexaRequestStreamHandler implements RequestStreamHandler {
    /**
     * Provides a set of application-id(s) you can find in the Alexa developer console of your skill.
     * Only requests coming in with these application-id(s) pass the request verification.
     * @return supported application ids
     */
    public Set<String> getSupportedApplicationIds() {
        final AlexaApplication app = this.getClass().getAnnotation(AlexaApplication.class);
        return app != null ? Stream.of(app.ApplicationIds()).collect(Collectors.toSet()) : Collections.emptySet();
    }

    /**
     * Provides the speechlet used to handle the request.
     * @return speechlet used to handle the request.
     */
    public Class<? extends AlexaSpeechlet> getSpeechlet() {
        final AlexaApplication app = this.getClass().getAnnotation(AlexaApplication.class);
        return app != null ? app.Speechlet() : AlexaSpeechlet.class;
    }

    /**
     * The handler method is called on a Lambda execution.
     * @param input the input stream containing the Lambda request payload
     * @param output the output stream containing the Lambda response payload
     * @param context a context for a Lambda execution.
     * @throws IOException exception is thrown on invalid request payload or on a provided speechlet
     * handler having no public constructor taking a String containing the locale
     */
    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        byte[] serializedSpeechletRequest = IOUtils.toByteArray(input);
        final JsonNode parser = mapper.readTree(serializedSpeechletRequest);
        final String locale = Optional.of(parser.path("request"))
                .filter(node -> !node.isMissingNode())
                .map(node -> node.path("locale"))
                .filter(node -> !node.isMissingNode())
                .map(JsonNode::textValue)
                .orElse("en-US");

        final Class<? extends AlexaSpeechlet> speechletClass = getSpeechlet();
        try {
            final AlexaSpeechlet speechlet = speechletClass.getConstructor(String.class).newInstance(locale);
            final SpeechletRequestHandler handler = getRequestStreamHandler();
            byte[] outputBytes = handler.handleSpeechletCall(speechlet, serializedSpeechletRequest);
            output.write(outputBytes);
        } catch (SpeechletRequestHandlerException | SpeechletException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new IOException(e);
        }
    }

    /**
     * Constructs the stream handler giving it all the provided information.
     * @return stream handler
     */
    private SpeechletRequestHandler getRequestStreamHandler() {
        final Set<String> supportedApplicationIds = getSupportedApplicationIds();
        // at least one supported application-id need to be provided
        Validate.notEmpty(supportedApplicationIds, "Must provide supported application-id either with overriding the getter or using AlexaApplication-annotation in " + this.getClass().getSimpleName());

        return new SpeechletRequestHandler(
                Collections.singletonList(
                        new ApplicationIdSpeechletRequestVerifier(supportedApplicationIds)),
                Arrays.asList(
                        new ResponseSizeSpeechletResponseVerifier(),
                        new OutputSpeechSpeechletResponseVerifier(),
                        new CardSpeechletResponseVerifier()));
    }
}
