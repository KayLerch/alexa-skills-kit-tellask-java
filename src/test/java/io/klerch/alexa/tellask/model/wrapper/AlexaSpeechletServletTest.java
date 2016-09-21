/**
 * Created by Kay Lerch (https://twitter.com/KayLerch)
 *
 * Contribute to https://github.com/KayLerch/alexa-skills-kit-tellask-java
 *
 * Attached license applies.
 * This source is licensed under GNU GENERAL PUBLIC LICENSE Version 3 as of 29 June 2007
 */
package io.klerch.alexa.tellask.model.wrapper;

import com.amazon.speech.Sdk;
import com.amazon.speech.json.SpeechletRequestEnvelope;
import com.amazon.speech.json.SpeechletResponseEnvelope;
import com.amazon.speech.slu.Slot;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.klerch.alexa.tellask.Assertions;
import io.klerch.alexa.tellask.ModelFactory;
import io.klerch.alexa.tellask.dummies.SampleAlexaSpeechlet;
import io.klerch.alexa.tellask.dummies.servlet.SampleServlet;
import io.klerch.alexa.tellask.dummies.servlet.SampleServlet2;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.*;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;

public class AlexaSpeechletServletTest {
    @Before
    public void init() {
        System.setProperty(Sdk.DISABLE_REQUEST_SIGNATURE_CHECK_SYSTEM_PROPERTY, "true");
    }

    private HttpServletResponse givenServletResponse(final OutputStream stream) throws Exception {
        final HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        final ServletOutputStream servletOutputStream = new ServletOutputStream() {
            @Override
            public void write(int b) throws IOException {
                stream.write(b);
            }
        };
        when(response.getOutputStream()).thenReturn(servletOutputStream);
        return response;
    }

    @Test
    public void getSetSpeechletAndApplicationId() throws Exception {
        final AlexaSpeechletServlet servlet = new SampleServlet();
        Assert.assertEquals(SampleAlexaSpeechlet.class, servlet.getAlexaSpeechlet());
        Assert.assertTrue(servlet.getSupportedApplicationIds().contains("applicationId"));

        final AlexaSpeechletServlet servlet2 = new SampleServlet2();
        Assert.assertEquals(SampleAlexaSpeechlet.class, servlet2.getAlexaSpeechlet());
        Assert.assertTrue(servlet2.getSupportedApplicationIds().contains("applicationId"));
    }

    @Test
    public void doLaunchPost() throws Exception {
        final AlexaSpeechletServlet servlet = new SampleServlet();
        final SpeechletRequestEnvelope envelope = ModelFactory.givenLaunchSpeechletRequestEnvelope("applicationId");
        final SpeechletResponseEnvelope responseEnvelope = doPost(servlet, envelope);
        Assertions.assertValidLaunchResponse(responseEnvelope);
    }

    @Test
    public void doIntentPost() throws Exception {
        final AlexaSpeechletServlet servlet = new SampleServlet();
        Map<String, Slot> slots = new HashMap<>();
        slots.put("name", Slot.builder().withName("name").withValue("Joe").build());
        slots.put("credits", Slot.builder().withName("credits").withValue("123").build());

        final SpeechletRequestEnvelope envelope = ModelFactory.givenIntentSpeechletRequestEnvelope("IntentWithOneUtteranceAndOneReprompt", "applicationId", slots);
        final SpeechletResponseEnvelope responseEnvelope = doPost(servlet, envelope);
        Assertions.assertValidIntentResponse(responseEnvelope);
    }

    private SpeechletResponseEnvelope doPost(final AlexaSpeechletServlet servlet, final SpeechletRequestEnvelope envelope) throws Exception {
        final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

        final InputStream stream = convertToStream(envelope);

        final ServletInputStream servletInputStream = new ServletInputStream() {
            @Override
            public int read() throws IOException {
                return stream.read();
            }
        };
        when(request.getInputStream()).thenReturn(servletInputStream);
        when(request.getReader()).thenReturn(new BufferedReader(new InputStreamReader(stream)));

        final ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
        final HttpServletResponse response = givenServletResponse(responseStream);
        servlet.doPost(request, response);

        return convertToResponseEnvelope(responseStream);
    }

    private SpeechletResponseEnvelope convertToResponseEnvelope(final ByteArrayOutputStream stream) throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(stream.toByteArray(), SpeechletResponseEnvelope.class);
    }

    private InputStream convertToStream(final SpeechletRequestEnvelope envelope) throws JsonProcessingException {
        final ObjectMapper mapper = new ObjectMapper();
        final String jsonEnvelope = mapper.writeValueAsString(envelope);
        return new ByteArrayInputStream(jsonEnvelope.getBytes(StandardCharsets.UTF_8));
    }
}