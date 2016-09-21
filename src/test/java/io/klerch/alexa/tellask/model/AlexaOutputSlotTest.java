/**
 * Created by Kay Lerch (https://twitter.com/KayLerch)
 *
 * Contribute to https://github.com/KayLerch/alexa-skills-kit-tellask-java
 *
 * Attached license applies.
 * This source is licensed under GNU GENERAL PUBLIC LICENSE Version 3 as of 29 June 2007
 */
package io.klerch.alexa.tellask.model;

import io.klerch.alexa.tellask.schema.type.AlexaOutputFormat;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.time.LocalDateTime;
import java.util.Date;

public class AlexaOutputSlotTest {
    private final Logger LOG = Logger.getLogger(AlexaOutputSlotTest.class);

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void getSetFormatAs() throws Exception {
        final AlexaOutputSlot slot = new AlexaOutputSlot("name", "value");
        slot.setFormatAs(AlexaOutputFormat.ADDRESS);
        Assert.assertEquals(AlexaOutputFormat.ADDRESS, slot.getFormatAs());
    }

    @Test
    public void formatAs() throws Exception {
        final AlexaOutputSlot slot = new AlexaOutputSlot("name", "value").formatAs(AlexaOutputFormat.AUDIO);
        Assert.assertEquals(AlexaOutputFormat.AUDIO, slot.getFormatAs());
    }

    @Test
    public void getSsml() throws Exception {
        testSsml(AlexaOutputFormat.ADDRESS, "102 Elmstreet");
        testSsml(AlexaOutputFormat.AUDIO, "https://klerch.io/test.mp3");
        testSsml(AlexaOutputFormat.DATE, new Date());
        testSsml(AlexaOutputFormat.DATE, LocalDateTime.now());
        testSsml(AlexaOutputFormat.DIGITS, 123);
        testSsml(AlexaOutputFormat.FRACTION, "1/3");
        testSsml(AlexaOutputFormat.NOUN, "reader");
        testSsml(AlexaOutputFormat.NUMBER, 321);
        testSsml(AlexaOutputFormat.ORDINAL, 3.21);
        testSsml(AlexaOutputFormat.PHONEME_IPA, "pɪˈkɑːn");
        testSsml(AlexaOutputFormat.PHONEME_X_SAMPA, "l\\akM_0teN\\");
        testSsml(AlexaOutputFormat.SPELLOUT, "spellme");
        testSsml(AlexaOutputFormat.TELEPHONE, "023-3312321");
        testSsml(AlexaOutputFormat.TEXT, "Some text.");
        testSsml(AlexaOutputFormat.TIME, "2:30pm");
        testSsml(AlexaOutputFormat.UNIT, "3.2KG");
        testSsml(AlexaOutputFormat.VERB_PAST, "read");
        testSsml(AlexaOutputFormat.VERB_PRESENT, "read");
    }

    private void testSsml(final AlexaOutputFormat format, final Object value) {
        final AlexaOutputSlot slot = new AlexaOutputSlot("name", value).formatAs(format);
        final String ssml = slot.getSsml();
        LOG.info(ssml);
        Assert.assertEquals(format.getSsml(slot.getValue()), ssml);
    }

    @Test
    public void getNameValue() throws Exception {
        final AlexaOutputSlot slot = new AlexaOutputSlot("name", "value");
        Assert.assertEquals("name", slot.getName());
        Assert.assertEquals("value", slot.getValue());
    }

    @Test
    public void constructWithIllegalArguments() throws Exception {
        exception.expect(IllegalArgumentException.class);
        new AlexaOutputSlot("", "value");

        exception.expect(NullPointerException.class);
        new AlexaOutputSlot("name", null);

        exception.expect(NullPointerException.class);
        new AlexaOutputSlot("name", "value").formatAs(null);
    }
}