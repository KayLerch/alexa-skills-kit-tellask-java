package io.klerch.alexa.tellask.dummies;

import io.klerch.alexa.state.model.AlexaStateModel;
import io.klerch.alexa.tellask.schema.AlexaOutputFormat;
import io.klerch.alexa.tellask.schema.AlexaSlotSave;

public class AlexaStateModelSample extends AlexaStateModel {
    @AlexaSlotSave(SlotName = "slot1")
    private Integer numbericSlotSave;

    @AlexaSlotSave(SlotName = "name", FormatAs = AlexaOutputFormat.SPELLOUT)
    private String name;

    private String notSlotSave;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
