package io.klerch.alexa.tellask.model;

import io.klerch.alexa.state.handler.AlexaSessionStateHandler;
import io.klerch.alexa.state.model.AlexaStateModel;
import io.klerch.alexa.tellask.ModelFactory;
import io.klerch.alexa.tellask.dummies.AlexaStateModelSample;
import io.klerch.alexa.tellask.dummies.StateModel;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class AlexaIntentModelTest {
    AlexaSessionStateHandler handler;

    @Before
    public void init() throws Exception {
        handler = spy(new AlexaSessionStateHandler(ModelFactory.givenSession()));
    }

    @Test
    public void getModel() throws Exception {
        final AlexaStateModel model = new AlexaStateModelSample().withHandler(handler);
        final AlexaIntentModel intentModel = new AlexaIntentModel(model);
        Assert.assertEquals(model, intentModel.getModel());
    }

    @Test
    public void getHandler() throws Exception {
        final AlexaStateModel model = new AlexaStateModelSample().withHandler(handler);
        final AlexaIntentModel intentModel = new AlexaIntentModel(model);
        Assert.assertEquals(handler, intentModel.getHandler());
    }

    @Test
    public void saveState() throws Exception {
        final AlexaStateModel model = new AlexaStateModelSample().withHandler(handler);
        final AlexaIntentModel intentModel = new AlexaIntentModel(model);
        intentModel.saveState();

        verify(handler).writeModel(model);
    }

    @Test
    public void hasOutputSlot() throws Exception {
        final AlexaStateModel model = new AlexaStateModelSample();
        final AlexaIntentModel intentModel = new AlexaIntentModel(model);
        Assert.assertTrue(intentModel.hasOutputSlot());

        final AlexaStateModel model2 = new StateModel();
        final AlexaIntentModel intentModel2 = new AlexaIntentModel(model2);
        Assert.assertFalse(intentModel2.hasOutputSlot());
    }

    @Test
    public void hasOutputSlotByName() throws Exception {
        final AlexaStateModel model = new AlexaStateModelSample();
        final AlexaIntentModel intentModel = new AlexaIntentModel(model);
        Assert.assertTrue(intentModel.hasOutputSlot("name"));
        Assert.assertFalse(intentModel.hasOutputSlot("this-does-not-exist"));
    }

    @Test
    public void getOutputSlot() throws Exception {
        final AlexaStateModelSample model = new AlexaStateModelSample();
        model.setName("Olaf");
        final AlexaIntentModel intentModel = new AlexaIntentModel(model);
        Assert.assertTrue(intentModel.getOutputSlot("name").isPresent());
        Assert.assertEquals("Olaf", intentModel.getOutputSlot("name").get().getValue());
    }
}