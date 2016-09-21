package io.klerch.alexa.tellask.dummies.model;

import io.klerch.alexa.state.model.AlexaStateModel;
import io.klerch.alexa.tellask.schema.type.AlexaOutputFormat;
import io.klerch.alexa.tellask.schema.annotation.AlexaSlotSave;

public class AlexaStateModelSample extends AlexaStateModel {
    @AlexaSlotSave(slotName = "slot1")
    private Integer numbericSlotSave;

    @AlexaSlotSave(slotName = "name", formatAs = AlexaOutputFormat.SPELLOUT)
    private String name;

    private String notSlotSave;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
