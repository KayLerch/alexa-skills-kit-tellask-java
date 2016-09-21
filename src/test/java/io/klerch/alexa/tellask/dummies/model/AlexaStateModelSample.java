/**
 * Created by Kay Lerch (https://twitter.com/KayLerch)
 *
 * Contribute to https://github.com/KayLerch/alexa-skills-kit-tellask-java
 *
 * Attached license applies.
 * This source is licensed under GNU GENERAL PUBLIC LICENSE Version 3 as of 29 June 2007
 */
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
