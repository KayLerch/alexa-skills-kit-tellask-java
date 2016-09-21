/**
 * Created by Kay Lerch (https://twitter.com/KayLerch)
 *
 * Contribute to https://github.com/KayLerch/alexa-skills-kit-tellask-java
 *
 * Attached license applies.
 * This source is licensed under GNU GENERAL PUBLIC LICENSE Version 3 as of 29 June 2007
 */
package io.klerch.alexa.tellask.util.factory;

import io.klerch.alexa.tellask.dummies.handler.LaunchHandler;
import io.klerch.alexa.tellask.schema.AlexaLaunchHandler;
import io.klerch.alexa.tellask.util.factory.AlexaLaunchHandlerFactory;
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