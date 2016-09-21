/**
 * Created by Kay Lerch (https://twitter.com/KayLerch)
 *
 * Contribute to https://github.com/KayLerch/alexa-skills-kit-tellask-java
 *
 * Attached license applies.
 * This source is licensed under GNU GENERAL PUBLIC LICENSE Version 3 as of 29 June 2007
 */
package io.klerch.alexa.tellask.model.wrapper;

import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.OutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SsmlOutputSpeech;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.klerch.alexa.tellask.model.AlexaOutput;
import io.klerch.alexa.tellask.model.AlexaOutputSlot;
import io.klerch.alexa.tellask.util.resource.YamlReader;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An extended version of the orginial speechlet response which makes it
 * compatible with TellAsk SDK and the speechlet handler it provides. This
 * object is capable of turning your AlexaOutput into a valid speechlet response.
 */
public class AlexaSpeechletResponse extends SpeechletResponse {
    private static final Logger LOG = Logger.getLogger(AlexaSpeechletResponse.class);

    @JsonIgnore
    private final AlexaOutput output;
    @JsonIgnore
    private final YamlReader yamlReader;
    private final OutputSpeech outputSpeech;
    private final Reprompt reprompt;

    /**
     * A speechlet response is generated from an AlexaOutput object which should
     * contain all the information necessary to get access to a set of utterances
     * (over the utterance reader) from which it picks randomly according to the
     * intent name also given by the AlexaOutput.
     * @param output the AlexaOutput
     */
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

    @Override
    @JsonInclude // works around a bug in Skills Kit SDK
    public boolean getShouldEndSession() {
        return output.shouldEndSession();
    }

    /**
     * The AlexaOutput used to generate the speechlet response
     * @return The AlexaOutput used to generate the speechlet response
     */
    public AlexaOutput getOutput() {
        return output;
    }

    /**
     * Gets the generated output speech.
     * @return the generated output speech.
     */
    @Override
    public OutputSpeech getOutputSpeech() {
        if (outputSpeech != null) {
            return outputSpeech;
        }

        final String utterance;

        try {
            utterance = yamlReader.getRandomUtterance(output).orElseThrow(IOException::new);
            LOG.debug("Random utterance read out from YAML file: " + utterance);
        } catch (IOException e) {
            LOG.error("Error while generating response utterance.", e);
            return null;
        }

        final String utteranceSsml = resolveSlotsInUtterance(utterance);

        final SsmlOutputSpeech ssmlOutputSpeech = new SsmlOutputSpeech();
        ssmlOutputSpeech.setSsml(utteranceSsml);
        return ssmlOutputSpeech;
    }

    /**
     * Gets the generated reprompt.
     * @return the generated reprompt
     */
    @Override
    public Reprompt getReprompt() {
        if (reprompt != null || !output.shouldReprompt()) {
            return reprompt;
        }

        final String repromptSpeech = yamlReader.getRandomReprompt(output).orElse(null);

        if (repromptSpeech != null) {
            final String utteranceSsml = resolveSlotsInUtterance(repromptSpeech);
            final SsmlOutputSpeech ssmlOutputSpeech = new SsmlOutputSpeech();
            ssmlOutputSpeech.setSsml(utteranceSsml);
            final Reprompt reprompt2 = new Reprompt();
            reprompt2.setOutputSpeech(ssmlOutputSpeech);
            return reprompt2;
        }
        return null;
    }

    private String resolveMultiPhraseCollections(final String utterance) {
        final StringBuffer buffer = new StringBuffer();
        // extract all the phrase collection (e.g. [Hello|Hi|Welcome] found in the utterance
        final Matcher multiPhrases = Pattern.compile("\\[(.*?)\\]").matcher(utterance);
        // for any of the multi-phrases ...
        while (multiPhrases.find()) {
            final String multiPhrase = multiPhrases.group(1);
            // single phrases are delimited by pipes
            final List<String> multiPhraseCollection = Arrays.asList(multiPhrase.split("\\|"));
            // pick random phrase out of the collection
            final String randomPhrase = getRandomOf(multiPhraseCollection).orElse("");
            if (StringUtils.isBlank(randomPhrase)) {
                LOG.warn("Empty multi-phrase collection found in one of your utterances. Got replaced by an empty string in speechlet response.");
            }
            multiPhrases.appendReplacement(buffer, randomPhrase);
        }
        multiPhrases.appendTail(buffer);
        return buffer.toString();
    }

    private Optional<String> getRandomOf(final List<String> list) {
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(new Random().nextInt(list.size())));
    }

    private String resolveSlotsInUtterance(final String utterance) {
        // first of all remove mutliphrases with randomly picked phrase out of these
        final String cleanedUtterance = resolveMultiPhraseCollections(utterance);
        final StringBuffer buffer = new StringBuffer();
        // extract all the placeholders found in the utterance
        final Matcher slots = Pattern.compile("\\{(.*?)\\}").matcher(cleanedUtterance);
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
