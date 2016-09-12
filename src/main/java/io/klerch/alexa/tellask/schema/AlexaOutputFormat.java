package io.klerch.alexa.tellask.schema;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public enum AlexaOutputFormat {
    TEXT("%1$s"),
    AUDIO("<audio src=\"%1$s\" /> "),
    PHONEME_IPA("<phoneme alphabet=\"ipa\" ph=\"%1$s\"></phoneme>"),
    PHONEME_X_SAMPA("<phoneme alphabet=\"x-sampa\" ph=\"%1$s\"></phoneme>"),
    SPELLOUT("<say-as interpret-as=\"spell-out\">%1$s</say-as>"),
    NUMBER("<say-as interpret-as=\"number\">%1$s</say-as>"),
    ORDINAL("<say-as interpret-as=\"ordinal\">%1$s</say-as>"),
    DIGITS("<say-as interpret-as=\"digits\">%1$s</say-as>"),
    FRACTION("<say-as interpret-as=\"fraction\">%1$s</say-as>"),
    UNIT("<say-as interpret-as=\"unit\">%1$s</say-as>"),
    DATE("<say-as interpret-as=\"date\">%1$s</say-as>"),
    TIME("<say-as interpret-as=\"time\">%1$s</say-as>"),
    TELEPHONE("<say-as interpret-as=\"telephone\">%1$s</say-as>"),
    ADDRESS("<say-as interpret-as=\"address\">%1$s</say-as>"),
    NOUN("<w role=\"ivona:NN\">%1$s</w>"),
    VERB_PRESENT("<w role=\"ivona:VB\">%1$s</w>"),
    VERB_PAST("<w role=\"ivona:VBD\">%1$s</w>");

    final String ssmlTemplate;

    AlexaOutputFormat(final String ssmlTemplate) {
        this.ssmlTemplate = ssmlTemplate;
    }

    public String getSsmlTemplate() {
        return this.ssmlTemplate;
    }

    public String getSsml(final Object text) {
        if (text instanceof Date)
            return getSsmlDate((Date)text);
        if (text instanceof LocalDateTime) {
            return getSsmlDate((LocalDateTime)text);
        }
        return String.format(ssmlTemplate, text);
    }

    private String getSsmlDate(final Date date) {
        return String.format(ssmlTemplate, new SimpleDateFormat("yyyyMMdd").format(date));
    }

    private String getSsmlDate(final LocalDateTime date) {
        return String.format(ssmlTemplate, DateTimeFormatter.ofPattern("yyyyMMdd").format(date));
    }
}
