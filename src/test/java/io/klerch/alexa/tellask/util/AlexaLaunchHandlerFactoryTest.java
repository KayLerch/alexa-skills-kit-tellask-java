package io.klerch.alexa.tellask.util;

import io.klerch.alexa.tellask.dummies.handler.LaunchHandler;
import io.klerch.alexa.tellask.schema.AlexaLaunchHandler;
import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;

public class AlexaLaunchHandlerFactoryTest {
    @Test
    public void createHandler() throws Exception {
        final Optional<AlexaLaunchHandler> handler = AlexaLaunchHandlerFactory.createHandler();
        Assert.assertTrue(handler.isPresent());
        Assert.assertTrue(handler.get() instanceof LaunchHandler);
    }
}