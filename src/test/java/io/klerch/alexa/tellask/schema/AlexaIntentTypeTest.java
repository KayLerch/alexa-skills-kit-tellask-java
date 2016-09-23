/**
 * Created by Kay Lerch (https://twitter.com/KayLerch)
 *
 * Contribute to https://github.com/KayLerch/alexa-skills-kit-tellask-java
 *
 * Attached license applies.
 * This source is licensed under GNU GENERAL PUBLIC LICENSE Version 3 as of 29 June 2007
 */
package io.klerch.alexa.tellask.schema;

import io.klerch.alexa.tellask.schema.type.AlexaIntentType;
import org.junit.Assert;
import org.junit.Test;

public class AlexaIntentTypeTest {
    @Test
    public void getName() throws Exception {
        Assert.assertEquals("AMAZON.CancelIntent", AlexaIntentType.INTENT_CANCEL.getName());
        Assert.assertEquals("AMAZON.HelpIntent", AlexaIntentType.INTENT_HELP.getName());
        Assert.assertEquals("AMAZON.NextIntent", AlexaIntentType.INTENT_NEXT.getName());
        Assert.assertEquals("AMAZON.NoIntent", AlexaIntentType.INTENT_NO.getName());
        Assert.assertEquals("AMAZON.RepeatIntent", AlexaIntentType.INTENT_REPEAT.getName());
        Assert.assertEquals("AMAZON.StartOverIntent", AlexaIntentType.INTENT_STARTOVER.getName());
        Assert.assertEquals("AMAZON.StopIntent", AlexaIntentType.INTENT_STOP.getName());
        Assert.assertEquals("AMAZON.YesIntent", AlexaIntentType.INTENT_YES.getName());
    }
}