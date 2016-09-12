package io.klerch.alexa.tellask.model;

import io.klerch.alexa.tellask.schema.AlexaOutputFormat;

public class AlexaOutputSlot {
    private AlexaOutputFormat format = AlexaOutputFormat.TEXT;
    private final Object value;
    private final String name;

    public AlexaOutputSlot(final String name, final Object value) {
        this.name = name;
        this.value = value;
    }

    public AlexaOutputFormat getFormatAs() {
        return format;
    }

    public void setFormatAs(final AlexaOutputFormat outputAs) {
        this.format = outputAs;
    }

    public AlexaOutputSlot formatAs(final AlexaOutputFormat format) {
        setFormatAs(format);
        return this;
    }

    public String getSsml() {
        return format.getSsml(value);
    }

    public Object getValue() {
        return value;
    }

    public String getName() {
        return name;
    }
}
