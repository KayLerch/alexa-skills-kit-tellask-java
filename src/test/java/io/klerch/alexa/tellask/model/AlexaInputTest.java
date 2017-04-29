/**
 * Created by Kay Lerch (https://twitter.com/KayLerch)
 *
 * Contribute to https://github.com/KayLerch/alexa-skills-kit-tellask-java
 *
 * Attached license applies.
 * This source is licensed under GNU GENERAL PUBLIC LICENSE Version 3 as of 29 June 2007
 */
package io.klerch.alexa.tellask.model;

import com.amazon.speech.slu.Slot;
import com.amazon.speech.speechlet.Context;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.Session;
import io.klerch.alexa.tellask.ModelFactory;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.HashMap;
import java.util.Map;

public class AlexaInputTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    private static final String LOCALE = "en-US";

    private Map<String, Slot> givenSlots() {
        Map<String, Slot> slots = new HashMap<>();
        slots.put("slotString", Slot.builder().withName("slotString").withValue("value").build());
        slots.put("slotNumber", Slot.builder().withName("slotNumber").withValue("312").build());
        slots.put("slotBoolean", Slot.builder().withName("slotBoolean").withValue("true").build());
        slots.put("slotBlank", Slot.builder().withName("slotBlank").withValue("").build());
        slots.put("slotNull", Slot.builder().withName("slotNull").withValue(null).build());
        return slots;
    }

    @Test
    public void constructIntentInputAndGetMembers() throws Exception {
        final IntentRequest request = ModelFactory.givenIntentRequest("intentName");
        final Session session = ModelFactory.givenSession();
        final Context context = ModelFactory.givenAlexaContext();
        final AlexaInput input = new AlexaInput(request, session, LOCALE, context);

        Assert.assertEquals(request, input.getRequest());
        Assert.assertNotNull(input.getSessionStateHandler());
        Assert.assertEquals(session, input.getSessionStateHandler().getSession());
    }

    @Test
    public void constructLaunchInputAndGetMembers() throws Exception {
        final LaunchRequest request = ModelFactory.givenLaunchRequest();
        final Session session = ModelFactory.givenSession();
        final Context context = ModelFactory.givenAlexaContext();
        final AlexaInput input = new AlexaInput(request, session, LOCALE, context);

        Assert.assertEquals(request, input.getRequest());
        Assert.assertNotNull(input.getSessionStateHandler());
        Assert.assertEquals(session, input.getSessionStateHandler().getSession());
    }

    @Test
    public void testGetIntentName() throws Exception {
        final IntentRequest request = ModelFactory.givenIntentRequest("intentName");
        final Session session = ModelFactory.givenSession();
        final Context context = ModelFactory.givenAlexaContext();

        final AlexaInput input = new AlexaInput(request, session, LOCALE, context);
        Assert.assertEquals("intentName", input.getIntentName());

        final LaunchRequest launchRequest = ModelFactory.givenLaunchRequest();
        final AlexaInput input2 = new AlexaInput(launchRequest, session, LOCALE, context);
        Assert.assertNull(input2.getIntentName());
    }

    @Test
    public void testGetLocale() throws Exception {
        final IntentRequest request = ModelFactory.givenIntentRequest("intentName");
        final Session session = ModelFactory.givenSession();
        final Context context = ModelFactory.givenAlexaContext();
        final AlexaInput input = new AlexaInput(request, session, LOCALE, context);
        Assert.assertEquals(LOCALE, input.getLocale());
    }

    @Test
    public void testHasSlotIsEqual() throws Exception {
        final IntentRequest request = ModelFactory.givenIntentRequest("intentName", givenSlots());
        final Session session = ModelFactory.givenSession();
        final Context context = ModelFactory.givenAlexaContext();
        final AlexaInput input = new AlexaInput(request, session, LOCALE, context);

        Assert.assertTrue(input.hasSlotIsEqual("slotString", "value"));
        Assert.assertFalse(input.hasSlotIsEqual("slotString", "not-value"));
        Assert.assertFalse(input.hasSlotIsEqual("slot-that-does-not-exist", "value-that-doesnt-matter"));

        Assert.assertTrue(input.hasSlotIsEqual("slotBlank", ""));
        Assert.assertFalse(input.hasSlotIsEqual("slotBlank", "value"));

        Assert.assertTrue(input.hasSlotIsEqual("slotNull", null));
    }

    @Test
    public void testHasSlotIsDoubleMetaphoneEqual() throws Exception {
        final IntentRequest request = ModelFactory.givenIntentRequest("intentName", givenSlots());
        final Session session = ModelFactory.givenSession();
        final Context context = ModelFactory.givenAlexaContext();
        final AlexaInput input = new AlexaInput(request, session, LOCALE, context);

        Assert.assertTrue(input.hasSlotIsDoubleMetaphoneEqual("slotString", "valui"));
        Assert.assertFalse(input.hasSlotIsDoubleMetaphoneEqual("slotString", "not-value"));
        Assert.assertTrue(input.hasSlotIsDoubleMetaphoneEqual("slotBoolean", "drew"));
        Assert.assertFalse(input.hasSlotIsDoubleMetaphoneEqual("slotBoolean", "false"));

        Assert.assertFalse(input.hasSlotIsDoubleMetaphoneEqual("slotBlank", ""));
        Assert.assertFalse(input.hasSlotIsDoubleMetaphoneEqual("slotNull", "value"));
    }

    @Test
    public void testHasSlotIsCologneEqual() throws Exception {
        final IntentRequest request = ModelFactory.givenIntentRequest("intentName", givenSlots());
        final Session session = ModelFactory.givenSession();
        final Context context = ModelFactory.givenAlexaContext();
        final AlexaInput input = new AlexaInput(request, session, LOCALE, context);

        Assert.assertTrue(input.hasSlotIsCologneEqual("slotString", "walu"));
        Assert.assertFalse(input.hasSlotIsCologneEqual("slotString", "not-value"));
        Assert.assertTrue(input.hasSlotIsCologneEqual("slotBoolean", "truhe"));
        Assert.assertFalse(input.hasSlotIsCologneEqual("slotBoolean", "false"));

        Assert.assertFalse(input.hasSlotIsCologneEqual("slotBlank", ""));
        Assert.assertFalse(input.hasSlotIsCologneEqual("slotNull", "value"));
    }

    @Test
    public void testHasSlotIsPhoneticallyEqual() throws Exception {
        final IntentRequest request = ModelFactory.givenIntentRequest("intentName", givenSlots());
        final Session session = ModelFactory.givenSession();
        final Context context = ModelFactory.givenAlexaContext();
        final AlexaInput germanInput = new AlexaInput(request, session, "de-DE", context);

        Assert.assertTrue(germanInput.hasSlotIsPhoneticallyEqual("slotBoolean", "truhe"));
        Assert.assertFalse(germanInput.hasSlotIsPhoneticallyEqual("slotBoolean", "drew"));

        final AlexaInput englishInput = new AlexaInput(request, session, LOCALE, context);

        Assert.assertFalse(englishInput.hasSlotIsPhoneticallyEqual("slotBoolean", "truhe"));
        Assert.assertTrue(englishInput.hasSlotIsPhoneticallyEqual("slotBoolean", "drew"));
    }

    @Test
    public void testHasSlot() throws Exception {
        final IntentRequest request = ModelFactory.givenIntentRequest("intentName", givenSlots());
        final Session session = ModelFactory.givenSession();
        final Context context = ModelFactory.givenAlexaContext();
        final AlexaInput input = new AlexaInput(request, session, LOCALE, context);

        Assert.assertTrue(input.hasSlot("slotString"));
        Assert.assertFalse(input.hasSlot("slotThatDoesNotExist"));

        Assert.assertTrue(input.hasSlotIsNumber("slotNumber"));
        Assert.assertFalse(input.hasSlotIsNumber("slotString"));
        Assert.assertFalse(input.hasSlotIsNumber("slotThatDoesNotExist"));

        Assert.assertTrue(input.hasSlotIsTrue("slotBoolean"));
        Assert.assertFalse(input.hasSlotIsTrue("slotString"));
        Assert.assertFalse(input.hasSlotIsTrue("slotThatDoesNotExist"));

        Assert.assertTrue(input.hasSlotNotBlank("slotBoolean"));
        Assert.assertTrue(input.hasSlotNotBlank("slotString"));
        Assert.assertFalse(input.hasSlotNotBlank("slotBlank"));
        Assert.assertFalse(input.hasSlotNotBlank("slotNull"));
        Assert.assertFalse(input.hasSlotNotBlank("slotThatDoesNotExist"));
    }

    @Test
    public void testGetSlotValue() throws Exception {
        final IntentRequest request = ModelFactory.givenIntentRequest("intentName", givenSlots());
        final Session session = ModelFactory.givenSession();
        final Context context = ModelFactory.givenAlexaContext();
        final AlexaInput input = new AlexaInput(request, session, LOCALE, context);

        final String sString = input.getSlotValue("slotString");
        Assert.assertEquals("value", sString);

        final String bString = input.getSlotValue("slotBoolean");
        Assert.assertEquals("true", bString);

        final String iString = input.getSlotValue("slotNumber");
        Assert.assertEquals("312", iString);

        Assert.assertNull(input.getSlotValue("slotThatDoesNotExist"));
    }
}