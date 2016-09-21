/**
 * Created by Kay Lerch (https://twitter.com/KayLerch)
 *
 * Contribute to https://github.com/KayLerch/alexa-skills-kit-tellask-java
 *
 * Attached license applies.
 * This source is licensed under GNU GENERAL PUBLIC LICENSE Version 3 as of 29 June 2007
 */
package io.klerch.alexa.tellask.model.wrapper;

import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletRequestHandler;
import com.amazon.speech.speechlet.SpeechletRequestHandlerException;
import com.amazon.speech.speechlet.verifier.*;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.klerch.alexa.tellask.schema.annotation.AlexaApplication;
import io.klerch.alexa.tellask.util.AlexaRequestHandlerException;
import io.klerch.alexa.tellask.util.factory.AlexaSpeechletFactory;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * An extended version of Lambda's RequestStreamHandler. You must provide some configuration
 * to this class by either using the AlexaApplication-annotation or by overriding the getter methods.
 * At least you must give it a supported application id otherwise all of the incoming requests will
 * be rejected.
 */
public abstract class AlexaRequestStreamHandler implements RequestStreamHandler {
    /**
     * Provides a set of application-id(s) you can find in the Alexa developer console of your skill.
     * Only requests coming in with these application-id(s) pass the request verification.
     * @return supported application ids
     */
    public Set<String> getSupportedApplicationIds() {
        final AlexaApplication app = this.getClass().getAnnotation(AlexaApplication.class);
        return app != null ? Stream.of(app.applicationIds()).collect(Collectors.toSet()) : Collections.emptySet();
    }

    /**
     * Provides the speechlet used to handle the request.
     * @return speechlet used to handle the request.
     */
    public Class<? extends AlexaSpeechlet> getSpeechlet() {
        final AlexaApplication app = this.getClass().getAnnotation(AlexaApplication.class);
        return app != null ? app.speechlet() : AlexaSpeechlet.class;
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
    public void handleRequest(final InputStream input, final OutputStream output, final Context context) throws IOException {
        byte[] serializedSpeechletRequest = IOUtils.toByteArray(input);
        final AlexaSpeechlet speechlet = AlexaSpeechletFactory.createSpeechletFromRequest(serializedSpeechletRequest, getSpeechlet());
        final SpeechletRequestHandler handler = getRequestStreamHandler();
        try {
            byte[] outputBytes = handler.handleSpeechletCall(speechlet, serializedSpeechletRequest);
            output.write(outputBytes);
        } catch (SpeechletRequestHandlerException | SpeechletException e) {
            // wrap actual exception in expected IOException
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
