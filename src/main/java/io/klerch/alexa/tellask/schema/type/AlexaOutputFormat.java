/**
 * Created by Kay Lerch (https://twitter.com/KayLerch)
 *
 * Contribute to https://github.com/KayLerch/alexa-skills-kit-tellask-java
 *
 * Attached license applies.
 * This source is licensed under GNU GENERAL PUBLIC LICENSE Version 3 as of 29 June 2007
 */
package io.klerch.alexa.tellask.schema.type;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * The output formats provide a set of SSML pattern. Applied to some value (most likely
 * AlexaSlotSave-fields in state models) it returns a valid SSML which go into a response utterance.
 */
public enum AlexaOutputFormat {
    /**
     * Value is formatted as plain text.
     */
    TEXT("%1$s"),
    /**
     * Value is expected to be a URL to an MP3-file as it is wrapped in an SSML-audio-tag.
     */
    AUDIO("<audio src=\"%1$s\" /> "),
    /**
     * Value is expected to be an IPA phoneme as it is wrapped in a SSML-phoneme-tag.
     */
    PHONEME_IPA("<phoneme alphabet=\"ipa\" ph=\"%1$s\"></phoneme>"),
    /**
     * Value is expected to be an X-SAMPA phoneme as it is wrapped in a SSML-phoneme-tag.
     */
    PHONEME_X_SAMPA("<phoneme alphabet=\"x-sampa\" ph=\"%1$s\"></phoneme>"),
    /**
     * Value is wrapped in a SSML-tag letting Alexa spell out the phrase.
     */
    SPELLOUT("<say-as interpret-as=\"spell-out\">%1$s</say-as>"),
    /**
     * Value is wrapped in a SSML-tag letting Alexa treat this value as a number. Make sure
     * this value is numeric.
     */
    NUMBER("<say-as interpret-as=\"number\">%1$s</say-as>"),
    /**
     * Value is wrapped in a SSML-tag letting Alexa treat this value as an ordinal. Make sure
     * this value contains an ordinal.
     */
    ORDINAL("<say-as interpret-as=\"ordinal\">%1$s</say-as>"),
    /**
     * Value is wrapped in a SSML-tag letting Alexa treat this value as number with digits. Make sure
     * this value is a digital string.
     */
    DIGITS("<say-as interpret-as=\"digits\">%1$s</say-as>"),
    /**
     * Value is wrapped in a SSML-tag letting Alexa treat this value as a fraction. Make sure
     * this value represents a fraction.
     */
    FRACTION("<say-as interpret-as=\"fraction\">%1$s</say-as>"),
    /**
     * Value is wrapped in a SSML-tag letting Alexa treat this value as a unit. Make sure
     * this value represents a unit.
     */
    UNIT("<say-as interpret-as=\"unit\">%1$s</say-as>"),
    /**
     * Value is wrapped in a SSML-tag letting Alexa treat this value as a date. Make sure
     * this value represents a date.
     */
    DATE("<say-as interpret-as=\"date\">%1$s</say-as>"),
    /**
     * Value is wrapped in a SSML-tag letting Alexa treat this value as a time. Make sure
     * this value represents a time.
     */
    TIME("<say-as interpret-as=\"time\">%1$s</say-as>"),
    /**
     * Value is wrapped in a SSML-tag letting Alexa treat this value as a telephone number. Make sure
     * this value contains a telephone number.
     */
    TELEPHONE("<say-as interpret-as=\"telephone\">%1$s</say-as>"),
    /**
     * Value is wrapped in a SSML-tag letting Alexa treat this value as an address. Make sure
     * this value contains an address.
     */
    ADDRESS("<say-as interpret-as=\"address\">%1$s</say-as>"),
    /**
     * Value is wrapped in a SSML-tag letting Alexa treat this value as a noun.
     */
    NOUN("<w role=\"ivona:NN\">%1$s</w>"),
    /**
     * Value is wrapped in a SSML-tag letting Alexa treat this value as a verb in present.
     */
    VERB_PRESENT("<w role=\"ivona:VB\">%1$s</w>"),
    /**
     * Value is wrapped in a SSML-tag letting Alexa treat this value as a verb in past.
     */
    VERB_PAST("<w role=\"ivona:VBD\">%1$s</w>");

    final String ssmlTemplate;

    AlexaOutputFormat(final String ssmlTemplate) {
        this.ssmlTemplate = ssmlTemplate;
    }

    /**
     * Returns the SSML template which will be applied to a value
     * @return SSML template
     */
    public String getSsmlTemplate() {
        return this.ssmlTemplate;
    }

    /**
     * Applies a value to the SSML template of this format.
     * @param value the value to apply to the SSML template
     * @return valid SSML with given value in it
     */
    public String getSsml(final Object value) {
        if (value instanceof LocalDateTime) {
            return getSsmlLocalDate((LocalDateTime)value);
        }
        if (value instanceof Date)
            return getSsmlDate((Date)value);
        return String.format(ssmlTemplate, value);
    }

    private String getSsmlDate(final Date date) {
        return String.format(ssmlTemplate, new SimpleDateFormat("yyyyMMdd").format(date));
    }

    private String getSsmlLocalDate(final LocalDateTime date) {
        return String.format(ssmlTemplate, DateTimeFormatter.ofPattern("yyyyMMdd").format(date));
    }
}
