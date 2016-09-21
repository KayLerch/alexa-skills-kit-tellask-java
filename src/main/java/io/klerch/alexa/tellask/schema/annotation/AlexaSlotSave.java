/**
 * Created by Kay Lerch (https://twitter.com/KayLerch)
 *
 * Contribute to https://github.com/KayLerch/alexa-skills-kit-tellask-java
 *
 * Attached license applies.
 * This source is licensed under GNU GENERAL PUBLIC LICENSE Version 3 as of 29 June 2007
 */
package io.klerch.alexa.tellask.schema.annotation;

import io.klerch.alexa.tellask.schema.type.AlexaOutputFormat;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation for fields in either an AlexaIntentModel or AlexaStateModel.
 * Fields having this annotation will have their value used for resolving slots (placeholders)
 * in the utterances.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface AlexaSlotSave {
    /**
     * Name of the slot (placeholder) in the utterance where the value of this field should
     * go into.
     * @return the slot name
     */
    String slotName();

    /**
     * The format to apply when putting the field's value into the slot (placeholder) of an
     * utterance. Most likely this format has an SSML tag which is wrapped around the value
     * of this field
     * @return the format to apply when putting the field's value into the slot of an utterance.
     */
    AlexaOutputFormat formatAs() default AlexaOutputFormat.TEXT;
}