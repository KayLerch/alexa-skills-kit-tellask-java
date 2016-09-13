package io.klerch.alexa.tellask.model;

import com.amazon.speech.ui.StandardCard;
import io.klerch.alexa.state.model.AlexaStateModel;
import io.klerch.alexa.tellask.dummies.AlexaStateModelSample;
import io.klerch.alexa.tellask.schema.AlexaOutputFormat;
import io.klerch.alexa.tellask.util.ResourceUtteranceReader;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Arrays;
import java.util.List;

public class AlexaOutputTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

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
        final AlexaStateModelSample model = new AlexaStateModelSample();
        final AlexaOutput output = AlexaOutput.ask("intentName").withState(model).build();
        Assert.assertNotNull(output.getModels());
        Assert.assertTrue(output.getModels().stream().anyMatch(m -> m.getModel().equals(model)));
    }

    @Test
    public void getModelsWithDuplicate() throws Exception {
        final AlexaStateModelSample model = new AlexaStateModelSample();
        final AlexaOutput output = AlexaOutput.ask("intentName").withState(model, model).build();
        Assert.assertNotNull(output.getModels());
        Assert.assertThat(output.getModels().size(), Is.is(1));
        Assert.assertTrue(output.getModels().stream().anyMatch(m -> m.getModel().equals(model)));
    }

    @Test
    public void getModelsWithList() throws Exception {
        final AlexaStateModelSample model = new AlexaStateModelSample();
        final AlexaStateModelSample model2 = new AlexaStateModelSample();
        final List<AlexaStateModel> models = Arrays.asList(model, model2, model);

        final AlexaOutput output = AlexaOutput.ask("intentName").withState(models).build();
        Assert.assertNotNull(output.getModels());
        Assert.assertThat(output.getModels().size(), Is.is(2));
        Assert.assertTrue(output.getModels().stream().anyMatch(m -> m.getModel().equals(model)));
        Assert.assertTrue(output.getModels().stream().anyMatch(m -> m.getModel().equals(model2)));
    }

    @Test
    public void getSlots() throws Exception {
        final AlexaOutputSlot slot = new AlexaOutputSlot("name", "value");
        final AlexaOutput output = AlexaOutput.ask("intentName").withSlot(slot).build();
        Assert.assertNotNull(output.getSlots());
        Assert.assertTrue(output.getSlots().contains(slot));

        final AlexaOutput output2 = AlexaOutput.ask("intentName").withSlot("slotName", "slotValue").build();
        Assert.assertNotNull(output2.getSlots());
        Assert.assertTrue(output2.getSlots().stream().anyMatch(s -> s.getName().equals("slotName") && s.getValue().equals("slotValue")));

        final AlexaOutput output3 = AlexaOutput.ask("intentName").withSlot("slotName", "slotValue", AlexaOutputFormat.DIGITS).build();
        Assert.assertNotNull(output3.getSlots());
        Assert.assertTrue(output3.getSlots().stream()
                .anyMatch(s -> s.getName().equals("slotName") && s.getValue().equals("slotValue") && s.getFormatAs().equals(AlexaOutputFormat.DIGITS)));

    }

    @Test
    public void getCard() throws Exception {
        final StandardCard card = new StandardCard();
        final AlexaOutput output = AlexaOutput.ask("intentName").withCard(card).build();
        Assert.assertNotNull(output.getCard());
        Assert.assertEquals(card, output.getCard());
    }

    @Test
    public void getUtteranceReader() throws Exception {
        final ResourceUtteranceReader reader = new ResourceUtteranceReader();
        final AlexaOutput output = AlexaOutput.ask("intentName").withReader(reader).build();
        Assert.assertNotNull(output.getUtteranceReader());
        Assert.assertEquals(reader, output.getUtteranceReader());
    }

    @Test
    public void tell() throws Exception {
        final AlexaOutput output = AlexaOutput.tell("intentName").build();
        Assert.assertNotNull(output.shouldEndSession());
        Assert.assertTrue(output.shouldEndSession());
    }

    @Test
    public void ask() throws Exception {
        final AlexaOutput output = AlexaOutput.ask("intentName").build();
        Assert.assertNotNull(output.shouldEndSession());
        Assert.assertFalse(output.shouldEndSession());
    }

    @Test
    public void constructMalformed() throws Exception {
        exception.expect(IllegalArgumentException.class);
        AlexaOutput.ask("").build();

        exception.expect(NullPointerException.class);
        AlexaOutput.ask("intentName").withReader(null).build();
    }
}