package io.klerch.alexa.tellask.model;

import io.klerch.alexa.tellask.dummies.AlexaIntentModelSample;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;

public class AlexaOutputTest {
    @Test
    public void getIntentName() throws Exception {
        final AlexaOutput output = AlexaOutput.ask("intentName").build();
        Assert.assertThat(output.getIntentName(), Is.is("intentName"));
    }

    @Test
    public void shouldEndSession() throws Exception {
        final AlexaOutput output = AlexaOutput.ask("intentName").build();
        Assert.assertThat(output.shouldEndSession(), Is.is(false));

        final AlexaOutput output2 = AlexaOutput.tell("intentName").build();
        Assert.assertThat(output2.shouldEndSession(), Is.is(true));
    }

    @Test
    public void shouldReprompt() throws Exception {
        final AlexaOutput output = AlexaOutput.ask("intentName").build();
        Assert.assertThat(output.shouldReprompt(), Is.is(false));

        final AlexaOutput output2 = AlexaOutput.ask("intentName").withReprompt(true).build();
        Assert.assertThat(output2.shouldReprompt(), Is.is(true));

        final AlexaOutput output3 = AlexaOutput.ask("intentName").withReprompt(false).build();
        Assert.assertThat(output3.shouldReprompt(), Is.is(false));
    }

    @Test
    public void getModels() throws Exception {
        final AlexaIntentModelSample model = new AlexaIntentModelSample();
        final AlexaOutput output = AlexaOutput.ask("intentName").withStateOf(model).build();
        Assert.assertNotNull(output.getModels());
        Assert.assertTrue(output.getModels().contains(model));
    }

    @Test
    public void getModelsWithDuplicate() throws Exception {
        final AlexaIntentModelSample model = new AlexaIntentModelSample();
        final AlexaOutput output = AlexaOutput.ask("intentName").withStateOf(model, model).build();
        Assert.assertNotNull(output.getModels());
        Assert.assertTrue(output.getModels().contains(model));
        Assert.assertThat(output.getModels().size(), Is.is(1));
    }

    @Test
    public void getSlots() throws Exception {

    }

    @Test
    public void getCard() throws Exception {

    }

    @Test
    public void getUtteranceReader() throws Exception {

    }

    @Test
    public void tell() throws Exception {

    }

    @Test
    public void ask() throws Exception {

    }

}