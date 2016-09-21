/**
 * Created by Kay Lerch (https://twitter.com/KayLerch)
 *
 * Contribute to https://github.com/KayLerch/alexa-skills-kit-tellask-java
 *
 * Attached license applies.
 * This source is licensed under GNU GENERAL PUBLIC LICENSE Version 3 as of 29 June 2007
 */
package io.klerch.alexa.tellask;

import com.amazon.speech.json.SpeechletResponseEnvelope;
import com.amazon.speech.ui.SsmlOutputSpeech;
import org.junit.Assert;

public class Assertions {
    public static void assertValidLaunchResponse(final SpeechletResponseEnvelope responseEnvelope) {
        Assert.assertNotNull(responseEnvelope);
        Assert.assertNotNull(responseEnvelope.getResponse());
        Assert.assertFalse(responseEnvelope.getResponse().getShouldEndSession());
        Assert.assertNotNull(responseEnvelope.getResponse().getOutputSpeech());
        Assert.assertTrue(responseEnvelope.getResponse().getOutputSpeech() instanceof SsmlOutputSpeech);

        final SsmlOutputSpeech outputSpeech = (SsmlOutputSpeech)responseEnvelope.getResponse().getOutputSpeech();
        Assert.assertEquals(outputSpeech.getSsml(), "<speak>Hello there</speak>");

        Assert.assertNotNull(responseEnvelope.getResponse().getReprompt());
        Assert.assertNotNull(responseEnvelope.getResponse().getReprompt().getOutputSpeech());
        Assert.assertTrue(responseEnvelope.getResponse().getReprompt().getOutputSpeech() instanceof SsmlOutputSpeech);

        final SsmlOutputSpeech repromptSpeech = (SsmlOutputSpeech)responseEnvelope.getResponse().getReprompt().getOutputSpeech();
        Assert.assertEquals(repromptSpeech.getSsml(), "<speak>Hello again</speak>");
    }

    public static void assertValidIntentResponse(final SpeechletResponseEnvelope responseEnvelope) {
        Assert.assertNotNull(responseEnvelope);
        Assert.assertNotNull(responseEnvelope.getResponse());
        Assert.assertFalse(responseEnvelope.getResponse().getShouldEndSession());
        Assert.assertNotNull(responseEnvelope.getResponse().getOutputSpeech());
        Assert.assertTrue(responseEnvelope.getResponse().getOutputSpeech() instanceof SsmlOutputSpeech);

        final SsmlOutputSpeech outputSpeech = (SsmlOutputSpeech)responseEnvelope.getResponse().getOutputSpeech();
        Assert.assertEquals(outputSpeech.getSsml(), "<speak>Hello <say-as interpret-as=\"spell-out\">Joe</say-as>. Your current score is <say-as interpret-as=\"number\">123</say-as></speak>");

        Assert.assertNotNull(responseEnvelope.getResponse().getReprompt());
        Assert.assertNotNull(responseEnvelope.getResponse().getReprompt().getOutputSpeech());
        Assert.assertTrue(responseEnvelope.getResponse().getReprompt().getOutputSpeech() instanceof SsmlOutputSpeech);

        final SsmlOutputSpeech repromptSpeech = (SsmlOutputSpeech)responseEnvelope.getResponse().getReprompt().getOutputSpeech();
        Assert.assertEquals(repromptSpeech.getSsml(), "<speak>This is a reprompt <say-as interpret-as=\"spell-out\">Joe</say-as> with your score of <say-as interpret-as=\"number\">123</say-as></speak>");
    }
}
