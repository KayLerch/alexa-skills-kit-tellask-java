/**
 * Created by Kay Lerch (https://twitter.com/KayLerch)
 *
 * Contribute to https://github.com/KayLerch/alexa-skills-kit-tellask-java
 *
 * Attached license applies.
 * This source is licensed under GNU GENERAL PUBLIC LICENSE Version 3 as of 29 June 2007
 */
package io.klerch.alexa.tellask.util;

import io.klerch.alexa.tellask.ModelFactory;
import io.klerch.alexa.tellask.model.AlexaInput;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class AlexaRequestHandlerExceptionTest {
    @Test
    public void testConstructors() throws Exception {
        final IOException nestedE = new IOException();

        final AlexaInput input = ModelFactory.givenAlexaInput("en-US", "intentName");
        final AlexaRequestHandlerException e3 = new AlexaRequestHandlerException("message", input);
        Assert.assertEquals("message", e3.getMessage());
        Assert.assertEquals(input, e3.getInput());

        final AlexaRequestHandlerException e4 = new AlexaRequestHandlerException("message", input, "errorIntent");
        Assert.assertEquals("message", e4.getMessage());
        Assert.assertEquals(input, e4.getInput());
        Assert.assertEquals("errorIntent", e4.getErrorIntent());

        final AlexaRequestHandlerException e5 = new AlexaRequestHandlerException("message", nestedE, input, "errorIntent");
        Assert.assertEquals("message", e5.getMessage());
        Assert.assertEquals(nestedE, e5.getCause());
        Assert.assertEquals(input, e5.getInput());
        Assert.assertEquals("errorIntent", e5.getErrorIntent());
    }
}