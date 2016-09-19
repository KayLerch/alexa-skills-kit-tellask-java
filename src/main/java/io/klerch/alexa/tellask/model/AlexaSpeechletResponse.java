package io.klerch.alexa.tellask.model;

import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.OutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SsmlOutputSpeech;
import io.klerch.alexa.tellask.util.YamlReader;
import org.apache.commons.lang3.Validate;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AlexaSpeechletResponse extends SpeechletResponse {
    private final Logger LOG = Logger.getLogger(AlexaSpeechletResponse.class);

    private final AlexaOutput output;
    private final YamlReader yamlReader;
    private final OutputSpeech outputSpeech;
    private final Reprompt reprompt;

    public AlexaSpeechletResponse(final AlexaOutput output) {
        this.output = output;
        this.yamlReader = new YamlReader(output.getUtteranceReader(), output.getLocale());
        this.setShouldEndSession(output.shouldEndSession());
        this.setCard(output.getCard());
        this.outputSpeech = getOutputSpeech();
        setOutputSpeech(outputSpeech);

        if (output.shouldReprompt()) {
            this.reprompt = getReprompt();
            // a reprompt is optional
            if (this.reprompt != null) {
                setReprompt(reprompt);
            } else {
                LOG.warn("Reprompt was desired but could not be generated from contents out of YAML file.");
            }
        } else {
            LOG.debug("No reprompt is desired. Skip looking for reprompt speech in YAML file.");
            this.reprompt = null;
        }
    }

    public AlexaOutput getOutput() {
        return output;
    }

    public OutputSpeech getOutputSpeech() {
        if (outputSpeech != null) return outputSpeech;

        final String utterance;

        try {
            utterance = yamlReader.getRandomUtterance(output).orElseThrow(IOException::new);
            LOG.debug("Random utterance read out from YAML file: " + utterance);
        } catch (IOException e) {
            LOG.error("Error while generating response utterance.", e);
            return null;
        }

        final String utteranceSsml = fillSlotsInUtterance(utterance);

        final SsmlOutputSpeech outputSpeech = new SsmlOutputSpeech();
        outputSpeech.setSsml(utteranceSsml);
        return outputSpeech;
    }

    public Reprompt getReprompt() {
        if (reprompt != null || !output.shouldReprompt()) return reprompt;

        final String repromptSpeech = yamlReader.getRandomReprompt(output).orElse(null);

        if (repromptSpeech != null) {
            final String utteranceSsml = fillSlotsInUtterance(repromptSpeech);
            final SsmlOutputSpeech outputSpeech = new SsmlOutputSpeech();
            outputSpeech.setSsml(utteranceSsml);
            final Reprompt reprompt = new Reprompt();
            reprompt.setOutputSpeech(outputSpeech);
            return reprompt;
        }
        return null;
    }

    private String fillSlotsInUtterance(final String utterance) {
        final StringBuffer buffer = new StringBuffer();
        // extract all the placeholders fosund in the utterance
        final Matcher slots = Pattern.compile("\\{(.*?)\\}").matcher(utterance);
        // for any of the placeholders ...
        while (slots.find()) {
            // ... placeholder-name is the slotName to look after in two places of the output
            final String slotName = slots.group(1);
            final AlexaOutputSlot outputSlot = output
                    // prefer directly set output slots
                    .getSlots().stream()
                    // which do have the same name as what is found in the utterance
                    .filter(slot -> slot.getName().equals(slotName))
                    .findFirst()
                    // if not directly applied look in provided models for AlexaSlotSave fields
                    .orElse(output.getModels()
                            .stream()
                            // for those having that AlexaSlotSave field
                            .filter(model -> model.hasOutputSlot(slotName))
                            // create a AlexaOutputSlot from attributes in annotation + the field value itself
                            .map(model -> model.getOutputSlot(slotName).orElse(null))
                            .findFirst().orElse(null));

            Validate.notNull(outputSlot, "Could not replace placeholder with name {" + slotName + "} because no corresponding slot was set in the output.");
            slots.appendReplacement(buffer, outputSlot.getSsml());
        }
        slots.appendTail(buffer);
        return "<speak>" + buffer.toString() + "</speak>";
    }
}
