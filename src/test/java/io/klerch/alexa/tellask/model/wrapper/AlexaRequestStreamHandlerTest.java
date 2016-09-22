/**
 * Created by Kay Lerch (https://twitter.com/KayLerch)
 *
 * Contribute to https://github.com/KayLerch/alexa-skills-kit-tellask-java
 *
 * Attached license applies.
 * This source is licensed under GNU GENERAL PUBLIC LICENSE Version 3 as of 29 June 2007
 */
package io.klerch.alexa.tellask.model.wrapper;

import com.amazon.speech.json.SpeechletRequestEnvelope;
import com.amazon.speech.json.SpeechletResponseEnvelope;
import com.amazon.speech.slu.Slot;
import com.amazon.speech.ui.SsmlOutputSpeech;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.klerch.alexa.tellask.Assertions;
import io.klerch.alexa.tellask.ModelFactory;
import io.klerch.alexa.tellask.dummies.SampleAlexaSpeechlet;
import io.klerch.alexa.tellask.dummies.lambda.SampleRequestStreamHandler;
import io.klerch.alexa.tellask.dummies.lambda.SampleRequestStreamHandler2;
import io.klerch.alexa.tellask.util.resource.ResourceUtteranceReader;
import io.klerch.alexa.tellask.util.resource.S3UtteranceReader;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class AlexaRequestStreamHandlerTest {
    @Test
    public void getUtteranceReader() throws Exception {
        final SampleRequestStreamHandler handler = new SampleRequestStreamHandler();
        Assert.assertTrue(handler.getUtteranceReader() instanceof ResourceUtteranceReader);

        final SampleRequestStreamHandler2 handler2 = new SampleRequestStreamHandler2();
        Assert.assertTrue(handler2.getUtteranceReader() instanceof S3UtteranceReader);
    }

    @Test
    public void getSupportedApplicationIds() throws Exception {
        final SampleRequestStreamHandler handler = new SampleRequestStreamHandler();
        Assert.assertTrue(handler.getSupportedApplicationIds().contains("applicationId"));

        final SampleRequestStreamHandler2 handler2 = new SampleRequestStreamHandler2();
        Assert.assertTrue(handler2.getSupportedApplicationIds().contains("applicationId"));
    }

    @Test
    public void getSpeechlet() throws Exception {
        final SampleRequestStreamHandler handler = new SampleRequestStreamHandler();
        Assert.assertTrue(handler.getSpeechlet().equals(SampleAlexaSpeechlet.class));

        final SampleRequestStreamHandler2 handler2 = new SampleRequestStreamHandler2();
        Assert.assertTrue(handler2.getSpeechlet().equals(SampleAlexaSpeechlet.class));
    }

    @Test
    public void handleLaunchRequest() throws Exception {
        final SampleRequestStreamHandler handler = new SampleRequestStreamHandler();
        final SpeechletRequestEnvelope envelope = ModelFactory.givenLaunchSpeechletRequestEnvelope("applicationId");

        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        handler.handleRequest(convertToStream(envelope), outputStream, ModelFactory.givenLambdaContext());

        final SpeechletResponseEnvelope responseEnvelope = convertToResponseEnvelope(outputStream);

        Assertions.assertValidLaunchResponse(responseEnvelope);
    }

    @Test
    public void handleIntentRequest() throws Exception {
        Map<String, Slot> slots = new HashMap<>();
        slots.put("name", Slot.builder().withName("name").withValue("Joe").build());
        slots.put("credits", Slot.builder().withName("credits").withValue("123").build());

        final SampleRequestStreamHandler handler = new SampleRequestStreamHandler();
        final SpeechletRequestEnvelope envelope = ModelFactory.givenIntentSpeechletRequestEnvelope("IntentWithOneUtteranceAndOneReprompt", "applicationId", slots);

        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        handler.handleRequest(convertToStream(envelope), outputStream, ModelFactory.givenLambdaContext());

        final SpeechletResponseEnvelope responseEnvelope = convertToResponseEnvelope(outputStream);
        Assertions.assertValidIntentResponse(responseEnvelope);
    }

    private SpeechletResponseEnvelope convertToResponseEnvelope(final OutputStream outputStream) throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(((ByteArrayOutputStream)outputStream).toByteArray(), SpeechletResponseEnvelope.class);
    }

    private InputStream convertToStream(final SpeechletRequestEnvelope envelope) throws JsonProcessingException {
        final ObjectMapper mapper = new ObjectMapper();
        final String jsonEnvelope = mapper.writeValueAsString(envelope);
        return new ByteArrayInputStream(jsonEnvelope.getBytes(StandardCharsets.UTF_8));
    }
}