package io.klerch.alexa.tellask.model;

import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.Session;
import io.klerch.alexa.tellask.ModelFactory;
import org.junit.Assert;
import org.junit.Test;

public class AlexaInputTest {
    @Test
    public void constructAndGetMembers() throws Exception {
        final IntentRequest request = ModelFactory.givenIntentRequest();
        final Session session = ModelFactory.givenSession();
        final AlexaInput input = new AlexaInput(request, session);

        Assert.assertEquals(request, input.getIntentRequest());
        Assert.assertNotNull(input.getSessionHandler());
        Assert.assertEquals(session, input.getSessionHandler().getSession());
    }
}