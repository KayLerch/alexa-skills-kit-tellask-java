/**
 * Created by Kay Lerch (https://twitter.com/KayLerch)
 *
 * Contribute to https://github.com/KayLerch/alexa-skills-kit-tellask-java
 *
 * Attached license applies.
 * This source is licensed under GNU GENERAL PUBLIC LICENSE Version 3 as of 29 June 2007
 */
package io.klerch.alexa.tellask.model.wrapper;

import com.amazon.speech.ui.SsmlOutputSpeech;
import com.amazon.speech.ui.StandardCard;
import io.klerch.alexa.tellask.dummies.model.AlexaStateModelSample;
import io.klerch.alexa.tellask.model.AlexaOutput;
import io.klerch.alexa.tellask.schema.type.AlexaOutputFormat;
import io.klerch.alexa.tellask.util.resource.ResourceUtteranceReader;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class AlexaSpeechletResponseTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void contruct() throws Exception {
        exception.expect(NullPointerException.class);
        new AlexaSpeechletResponse(null, new ResourceUtteranceReader());

        exception.expect(NullPointerException.class);
        new AlexaSpeechletResponse(AlexaOutput.ask("someIntent").build(), null);
    }

    @Test
    public void getResponseWithSlotsAndReprompt() throws Exception {
        final AlexaStateModelSample model = new AlexaStateModelSample();
        model.setName("Paul");

        final AlexaOutput output = AlexaOutput
                .ask("IntentWithReprompts")
                .withReprompt(true)
                .putSlot("credits", 123, AlexaOutputFormat.NUMBER)
                .putState(model).build();

        final AlexaSpeechletResponse response = new AlexaSpeechletResponse(output, new ResourceUtteranceReader());
        Assert.assertEquals(output, response.getOutput());
        Assert.assertNotNull(response.getReprompt());
    }

    @Test
    public void getResponseWithSlots() throws Exception {
        final AlexaStateModelSample model = new AlexaStateModelSample();
        model.setName("Paul");

        final AlexaOutput output = AlexaOutput
                .ask("IntentWithOneUtteranceAndOneReprompt")
                .putSlot("credits", 123, AlexaOutputFormat.NUMBER)
                .putState(model).build();

        final AlexaSpeechletResponse response = new AlexaSpeechletResponse(output, new ResourceUtteranceReader());
        Assert.assertEquals(output, response.getOutput());
        Assert.assertEquals("<speak>Hello <say-as interpret-as=\"spell-out\">Paul</say-as>. Your current score is <say-as interpret-as=\"number\">123</say-as></speak>",
                ((SsmlOutputSpeech)response.getOutputSpeech()).getSsml());
        Assert.assertNull(response.getReprompt());
    }

    @Test
    public void getResponseWithReprompt() throws Exception {
        final AlexaOutput output = AlexaOutput
                .ask("IntentWithNoSlots")
                .withReprompt(true)
                .build();

        final AlexaSpeechletResponse response = new AlexaSpeechletResponse(output, new ResourceUtteranceReader());
        Assert.assertNotNull(response.getReprompt());
        Assert.assertEquals("<speak>Hello again</speak>",
                ((SsmlOutputSpeech)response.getReprompt().getOutputSpeech()).getSsml());
    }

    @Test
    public void getResponseWithCard() throws Exception {
        final StandardCard card = new StandardCard();
        final AlexaOutput output = AlexaOutput
                .ask("IntentWithNoSlots")
                .withCard(card)
                .build();

        final AlexaSpeechletResponse response = new AlexaSpeechletResponse(output, new ResourceUtteranceReader());
        Assert.assertEquals(card, response.getCard());
    }

    @Test
    public void getResponseWithoutSlots() throws Exception {
        final AlexaOutput output = AlexaOutput
                .ask("IntentWithNoSlots").build();

        final AlexaSpeechletResponse response = new AlexaSpeechletResponse(output, new ResourceUtteranceReader());
        Assert.assertEquals(output, response.getOutput());
        Assert.assertEquals("<speak>Hello there</speak>",
                ((SsmlOutputSpeech)response.getOutputSpeech()).getSsml());
    }

    @Test
    public void getResponseWithSlotMissingInOutput() throws Exception {
        final AlexaOutput output = AlexaOutput
                .ask("IntentWithOneUtteranceAndOneReprompt").build();

        exception.expect(NullPointerException.class);
        new AlexaSpeechletResponse(output, new ResourceUtteranceReader());
    }
}