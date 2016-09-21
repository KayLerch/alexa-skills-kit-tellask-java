package io.klerch.alexa.tellask.model;

import io.klerch.alexa.tellask.schema.type.AlexaOutputFormat;
import org.apache.commons.lang3.Validate;

/**
 * An output slot fills the placeholder in an output utterance coming from one of the YAML files.
 * The slot is identified by a name equal to the placeholder-name, a string-parsable value and
 * an output format.
 */
public class AlexaOutputSlot {
    private AlexaOutputFormat format = AlexaOutputFormat.TEXT;
    private final Object value;
    private final String name;

    /**
     * New slot with a name equal to the placeholder-name in an utterance from one of the YAML files and
     * a string-parsable value.
     * @param name slot name. Must not be blank.
     * @param value slot value. Must be non-null.
     */
    public AlexaOutputSlot(final String name, final Object value) {
        Validate.notBlank(name, "Slotname must not be empty.");
        Validate.notNull(value, "Slot value must not be null.");
        this.name = name;
        this.value = value;
    }

    /**
     * Gets the format of the slot output in an utterance. Usually this is an SSML tag wrapped around the
     * slot value.
     * @return the format of the slot output in an utterance
     */
    public AlexaOutputFormat getFormatAs() {
        return format;
    }

    /**
     * Sets the format of the slot output in an utterance. Usually this is an SSML tag wrapped around the
     * slot value.
     * @param format the format of the slot output in an utterance. Must not be null.
     */
    public void setFormatAs(final AlexaOutputFormat format) {
        Validate.notNull(format, "Slot output format must not be null.");
        this.format = format;
    }

    /**
     * Sets the format of the slot output in an utterance. Usually this is an SSML tag wrapped around the
     * slot value.
     * @param format the format of the slot output in an utterance. Must not be null.
     * @return this
     */
    public AlexaOutputSlot formatAs(final AlexaOutputFormat format) {
        setFormatAs(format);
        return this;
    }

    /**
     * The value combined with the output format results in a SSML-string returned with this method.
     * @return The SSML-representation of the slot value.
     */
    public String getSsml() {
        return format.getSsml(value);
    }

    /**
     * Returns the slot value
     * @return slot value
     */
    public Object getValue() {
        return value;
    }

    /**
     * Returns the slot name
     * @return slot value
     */
    public String getName() {
        return name;
    }
}
