package io.klerch.alexa.tellask.util;

import io.klerch.alexa.tellask.model.AlexaOutput;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class YamlReaderTest {
    private YamlReader reader;

    private final String intentWithReprompts = "IntentWithReprompts";
    private final String intentNotExisting = "AnIntentThatDoesNotExistInTheYaml";
    private final String intentWithoutReprompts = "IntentWithoutReprompts";
    private final String intentWithEmptyReprompts = "IntentWithEmptyReprompts";
    private final String intentWithoutAny = "IntentWithoutAny";
    private final String intentWithEmptyUtterance = "IntentWithEmptyUtterance";

    private AlexaOutput givenAlexaOutputWithIntent(final String intentName) {
        return AlexaOutput.ask(intentName).build();
    }

    @Before
    public void init() throws Exception {
        reader = new YamlReader(new ResourceUtteranceReader());
    }

    @Test
    public void testWithIntentWithReprompts() throws Exception {
        testIntent(intentWithReprompts, 3, 2);
    }

    @Test
    public void testWithIntentNotExisting() throws Exception {
        testIntent(intentNotExisting, 0, 0);
    }

    @Test
    public void testWithIntentWithoutReprompts() throws Exception {
        testIntent(intentWithoutReprompts, 3, 0);
    }

    @Test
    public void testWithIntentWithEmptyReprompts() throws Exception {
        testIntent(intentWithEmptyReprompts, 3, 0);
    }

    @Test
    public void testWithIntentWithoutAny() throws Exception {
        testIntent(intentWithoutAny, 0, 0);
    }

    @Test
    public void testWithIntentWithEmptyUtterance() throws Exception {
        testIntent(intentWithEmptyUtterance, 0, 0);
    }

    private void testIntent(final String intent, final int expectedIntentPhrases, final int expectedRepromptPhrases) {
        // getUtterancesByIntentName
        List<String> utterances = reader.getUtterances(intent);
        Assert.assertNotNull(utterances);
        Assert.assertEquals(expectedIntentPhrases, utterances.size());

        // getUtterancesByAlexaOutput
        utterances = reader.getUtterances(givenAlexaOutputWithIntent(intent));
        Assert.assertNotNull(utterances);
        Assert.assertEquals(expectedIntentPhrases, utterances.size());

        // getRandomUtteranceByIntentName
        if (expectedIntentPhrases > 0) {
            Assert.assertTrue(reader.getRandomUtterance(intent).isPresent());
        }
        else {
            Assert.assertFalse(reader.getRandomUtterance(intent).isPresent());
        }

        // getRandomUtteranceByAlexaOutput
        if (expectedIntentPhrases > 0)
            Assert.assertTrue(reader.getRandomUtterance(givenAlexaOutputWithIntent(intent)).isPresent());
        else
            Assert.assertFalse(reader.getRandomUtterance(givenAlexaOutputWithIntent(intent)).isPresent());

        // getRepromptsByIntentName
        List<String> reprompts = reader.getReprompts(intent);
        Assert.assertNotNull(reprompts);
        Assert.assertEquals(expectedRepromptPhrases, reprompts.size());

        // getRepromptsByAlexaOutput
        reprompts = reader.getReprompts(givenAlexaOutputWithIntent(intent));
        Assert.assertNotNull(reprompts);
        Assert.assertEquals(expectedRepromptPhrases, reprompts.size());

        // getRandomRepromptByIntentName
        if (expectedRepromptPhrases > 0) {
            Assert.assertTrue(reader.getRandomReprompt(intent).isPresent());
        }
        else {
            Assert.assertFalse(reader.getRandomReprompt(intent).isPresent());
        }

        // getRandomRepromptByAlexaOutput
        if (expectedRepromptPhrases > 0) {
            Assert.assertTrue(reader.getRandomReprompt(givenAlexaOutputWithIntent(intent)).isPresent());
        }
        else {
            Assert.assertFalse(reader.getRandomReprompt(givenAlexaOutputWithIntent(intent)).isPresent());
        }
    }
}