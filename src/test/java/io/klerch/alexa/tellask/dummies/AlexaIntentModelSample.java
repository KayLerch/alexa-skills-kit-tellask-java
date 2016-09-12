package io.klerch.alexa.tellask.dummies;


import io.klerch.alexa.tellask.model.AlexaIntentModel;
import io.klerch.alexa.tellask.schema.AlexaOutputFormat;
import io.klerch.alexa.tellask.schema.AlexaSlotSave;

public class AlexaIntentModelSample extends AlexaIntentModel {
    @AlexaSlotSave(SlotName = "slot", FormatAs = AlexaOutputFormat.ORDINAL)
    private Integer numbericSlotSave;

    @AlexaSlotSave(SlotName = "slot2")
    private String stringSlotSave;

    private String notSlotSave;
}
