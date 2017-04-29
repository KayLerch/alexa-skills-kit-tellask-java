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
import com.amazon.speech.slu.Slot;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.SsmlOutputSpeech;
import io.klerch.alexa.tellask.ModelFactory;
import io.klerch.alexa.tellask.util.resource.ResourceUtteranceReader;
import org.junit.*;
import org.junit.rules.ExpectedException;

import java.util.HashMap;
import java.util.Map;

public class AlexaSpeechletTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    private AlexaSpeechlet speechlet;
    private Session session;

    @Before
    public void init() {
        speechlet = new AlexaSpeechlet("en-US", new ResourceUtteranceReader());
        session = ModelFactory.givenSession();
    }

    @Test
    public void onSessionStarted() throws Exception {
        SpeechletRequestEnvelope envelope = ModelFactory.givenIntentSpeechletRequestEnvelope();
        speechlet.onSessionStarted(envelope);
    }

    @Test
    public void onLaunch() throws Exception {

        final SpeechletResponse response = speechlet.onLaunch(ModelFactory.givenLaunchSpeechletRequestEnvelope());
        Assert.assertNotNull(response);
        Assert.assertTrue(response instanceof AlexaSpeechletResponse);

        final AlexaSpeechletResponse alexaResponse = (AlexaSpeechletResponse)response;
        Assert.assertNotNull(alexaResponse.getOutput());
        Assert.assertEquals("IntentWithNoSlots", alexaResponse.getOutput().getIntentName());
        Assert.assertNotNull(alexaResponse.getOutputSpeech());
        Assert.assertTrue(alexaResponse.getOutputSpeech() instanceof SsmlOutputSpeech);

        final SsmlOutputSpeech outputSpeech = (SsmlOutputSpeech)alexaResponse.getOutputSpeech();
        Assert.assertEquals(outputSpeech.getSsml(), "<speak>Hello there</speak>");

        Assert.assertNotNull(alexaResponse.getReprompt());
        Assert.assertNotNull(alexaResponse.getReprompt().getOutputSpeech());
        Assert.assertTrue(alexaResponse.getReprompt().getOutputSpeech() instanceof SsmlOutputSpeech);

        final SsmlOutputSpeech repromptSpeech = (SsmlOutputSpeech)alexaResponse.getReprompt().getOutputSpeech();
        Assert.assertEquals(repromptSpeech.getSsml(), "<speak>Hello again</speak>");
    }

    // SpeechletV2 doesn't throw Exceptions
    @Ignore
    @Test
    public void onIntentWithNoHandler() throws Exception {
        SpeechletRequestEnvelope speechletRequestEnvelope = SpeechletRequestEnvelope.builder()
                .withRequest(ModelFactory.givenIntentRequest("IntentThatHasNoHandler"))
                .withSession(session)
                .build();
        final SpeechletResponse response = speechlet.onIntent(speechletRequestEnvelope);
        Assert.assertNotNull(response);
        Assert.assertTrue(response instanceof AlexaSpeechletResponse);
    }

    @Test
    public void onIntentWithError() throws Exception {
        // provoke an error with an non-numeric credit-value
        Map<String, Slot> slots = new HashMap<>();
        slots.put("name", Slot.builder().withName("name").withValue("Joe").build());
        slots.put("credits", Slot.builder().withName("credits").withValue("notANumber").build());

        SpeechletRequestEnvelope speechletRequestEnvelope = SpeechletRequestEnvelope.builder()
                .withRequest(ModelFactory.givenIntentRequest("IntentWithOneUtteranceAndOneReprompt", slots))
                .withSession(session)
                .build();

        final SpeechletResponse response = speechlet.onIntent(speechletRequestEnvelope);
        Assert.assertNotNull(response);
        Assert.assertTrue(response instanceof AlexaSpeechletResponse);

        final AlexaSpeechletResponse alexaResponse = (AlexaSpeechletResponse)response;
        Assert.assertNotNull(alexaResponse.getOutput());
        Assert.assertEquals("SaySorry", alexaResponse.getOutput().getIntentName());
        Assert.assertTrue(alexaResponse.getOutput().shouldEndSession());

        Assert.assertNotNull(alexaResponse.getOutputSpeech());
        Assert.assertTrue(alexaResponse.getOutputSpeech() instanceof SsmlOutputSpeech);

        final SsmlOutputSpeech outputSpeech = (SsmlOutputSpeech)alexaResponse.getOutputSpeech();
        Assert.assertEquals(outputSpeech.getSsml(), "<speak>Sorry, there was an error</speak>");
    }

    @Test
    public void onIntent() throws Exception {
        Map<String, Slot> slots = new HashMap<>();
        slots.put("name", Slot.builder().withName("name").withValue("Joe").build());
        slots.put("credits", Slot.builder().withName("credits").withValue("123").build());

        SpeechletRequestEnvelope speechletRequestEnvelope = SpeechletRequestEnvelope.builder()
                .withRequest(ModelFactory.givenIntentRequest("IntentWithOneUtteranceAndOneReprompt", slots))
                .withSession(session)
                .build();

        final SpeechletResponse response = speechlet.onIntent(speechletRequestEnvelope);
        Assert.assertNotNull(response);
        Assert.assertTrue(response instanceof AlexaSpeechletResponse);

        final AlexaSpeechletResponse alexaResponse = (AlexaSpeechletResponse)response;
        Assert.assertNotNull(alexaResponse.getOutput());
        Assert.assertEquals("IntentWithOneUtteranceAndOneReprompt", alexaResponse.getOutput().getIntentName());
        Assert.assertFalse(alexaResponse.getOutput().shouldEndSession());
        Assert.assertNotNull(alexaResponse.getOutputSpeech());
        Assert.assertTrue(alexaResponse.getOutputSpeech() instanceof SsmlOutputSpeech);

        final SsmlOutputSpeech outputSpeech = (SsmlOutputSpeech)alexaResponse.getOutputSpeech();
        Assert.assertEquals(outputSpeech.getSsml(), "<speak>Hello <say-as interpret-as=\"spell-out\">Joe</say-as>. Your current score is <say-as interpret-as=\"number\">123</say-as></speak>");

        Assert.assertNotNull(alexaResponse.getReprompt());
        Assert.assertNotNull(alexaResponse.getReprompt().getOutputSpeech());
        Assert.assertTrue(alexaResponse.getReprompt().getOutputSpeech() instanceof SsmlOutputSpeech);

        final SsmlOutputSpeech repromptSpeech = (SsmlOutputSpeech)alexaResponse.getReprompt().getOutputSpeech();
        Assert.assertEquals(repromptSpeech.getSsml(), "<speak>This is a reprompt <say-as interpret-as=\"spell-out\">Joe</say-as> with your score of <say-as interpret-as=\"number\">123</say-as></speak>");
    }

    @Test
    public void onSessionEnded() throws Exception {
        SpeechletRequestEnvelope speechletRequestEnvelope = SpeechletRequestEnvelope.builder()
                .withRequest(ModelFactory.givenSessionEndedRequest())
                .withSession(session)
                .build();

        speechlet.onSessionEnded(speechletRequestEnvelope);
    }
}