package io.klerch.alexa.tellask.schema;

import io.klerch.alexa.tellask.schema.type.AlexaIntentType;
import org.junit.Assert;
import org.junit.Test;

public class AlexaIntentTypeTest {
    @Test
    public void getName() throws Exception {
        Assert.assertEquals("AMAZON.CancelIntent", AlexaIntentType.INTENT_CANCEL.getName());
        Assert.assertEquals("", AlexaIntentType.INTENT_CUSTOM.getName());
        Assert.assertEquals("AMAZON.HelpIntent", AlexaIntentType.INTENT_HELP.getName());
        Assert.assertEquals("AMAZON.NextIntent", AlexaIntentType.INTENT_NEXT.getName());
        Assert.assertEquals("AMAZON.NoIntent", AlexaIntentType.INTENT_NO.getName());
        Assert.assertEquals("AMAZON.RepeatIntent", AlexaIntentType.INTENT_REPEAT.getName());
        Assert.assertEquals("AMAZON.StartOverIntent", AlexaIntentType.INTENT_STARTOVER.getName());
        Assert.assertEquals("AMAZON.StopIntent", AlexaIntentType.INTENT_STOP.getName());
        Assert.assertEquals("AMAZON.YesIntent", AlexaIntentType.INTENT_YES.getName());
    }

}