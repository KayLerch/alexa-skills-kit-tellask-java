package io.klerch.alexa.tellask.model;

import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.Session;
import io.klerch.alexa.tellask.ModelFactory;
import org.junit.Assert;
import org.junit.Test;

public class AlexaInputTest {
    private static final String LOCALE = "en-US";

    @Test
    public void constructIntentInputAndGetMembers() throws Exception {
        final IntentRequest request = ModelFactory.givenIntentRequest();
        final Session session = ModelFactory.givenSession();
        final AlexaInput input = new AlexaInput(request, session, LOCALE);

        Assert.assertEquals(request, input.getRequest());
        Assert.assertNotNull(input.getSessionHandler());
        Assert.assertEquals(session, input.getSessionHandler().getSession());
    }

    @Test
    public void constructLaunchInputAndGetMembers() throws Exception {
        final LaunchRequest request = ModelFactory.givenLaunchRequest();
        final Session session = ModelFactory.givenSession();
        final AlexaInput input = new AlexaInput(request, session, LOCALE);

        Assert.assertEquals(request, input.getRequest());
        Assert.assertNotNull(input.getSessionHandler());
        Assert.assertEquals(session, input.getSessionHandler().getSession());
    }
}