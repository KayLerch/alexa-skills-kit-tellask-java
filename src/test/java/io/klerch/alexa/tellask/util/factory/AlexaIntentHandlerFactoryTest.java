package io.klerch.alexa.tellask.util.factory;

import io.klerch.alexa.tellask.ModelFactory;
import io.klerch.alexa.tellask.dummies.handler.SampleHandler1000;
import io.klerch.alexa.tellask.model.AlexaInput;
import io.klerch.alexa.tellask.schema.AlexaIntentHandler;
import io.klerch.alexa.tellask.util.factory.AlexaIntentHandlerFactory;
import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;

public class AlexaIntentHandlerFactoryTest {
    @Test
    public void createHandler() throws Exception {
        final AlexaInput input = ModelFactory.givenAlexaInput("en-US", "IntentWithOneUtteranceAndOneReprompt");
        final Optional<AlexaIntentHandler> handler = AlexaIntentHandlerFactory.createHandler(input);
        Assert.assertTrue(handler.isPresent());
        Assert.assertTrue(handler.get() instanceof SampleHandler1000);
    }

    @Test
    public void createHandlerNotExistant() throws Exception {
        final AlexaInput input = ModelFactory.givenAlexaInput("en-US", "IntentWithNoHandlerForIt");
        final Optional<AlexaIntentHandler> handler = AlexaIntentHandlerFactory.createHandler(input);
        Assert.assertFalse(handler.isPresent());
    }
}