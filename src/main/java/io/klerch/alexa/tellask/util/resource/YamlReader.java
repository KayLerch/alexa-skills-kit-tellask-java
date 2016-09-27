/**
 * Created by Kay Lerch (https://twitter.com/KayLerch)
 * <p>
 * Contribute to https://github.com/KayLerch/alexa-skills-kit-tellask-java
 * <p>
 * Attached license applies.
 * This source is licensed under GNU GENERAL PUBLIC LICENSE Version 3 as of 29 June 2007
 */
package io.klerch.alexa.tellask.util.resource;

import io.klerch.alexa.tellask.model.AlexaOutput;
import io.klerch.alexa.tellask.schema.UtteranceReader;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.yaml.snakeyaml.Yaml;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This reader processes input coming from the resource streams of
 * UtteranceReader to extract the utterances and reprompts and optionally
 * picks one of them randomly.
 */
public class YamlReader {
    private final UtteranceReader utteranceReader;
    private final Map<String, List<Object>> phrases = new HashMap<>();
    private final String locale;
    private final Map<?, ?> content;

    /**
     * A new YAMLReader needs an UtteranceReader so it can obtain YAML content with utterances.
     * @param utteranceReader the UtteranceReader reading ot YAML content with utterances
     * @param locale Localized skills will have multiple YAML files (one for each language). By
     *               giving this reader a locale it lets the UtteranceReader read out the appropriate
     *               YAML file.
     */
    public YamlReader(final UtteranceReader utteranceReader, final String locale) {
        this.utteranceReader = utteranceReader;
        this.locale = locale;
        // leverage reader to get yaml with utterances
        content = new Yaml().loadAs(utteranceReader.read(locale), Map.class);
    }

    /**
     * Returns a set of utterances for an intent which is defined in the AlexaOutput.
     * @param output The AlexaOutput containing the intent name whose utterances are to be returned.
     * @return set of utterances for an intent which is defined in the AlexaOutput
     */
    public List<String> getUtterances(final AlexaOutput output) {
        return getUtterances(output.getIntentName());
    }

    /**
     * Returns an utterance randomly picked from a set of utterances for an intent which is defined in the AlexaOutput.
     * @param output The AlexaOutput containing the intent name.
     * @return utterance randomly picked from a set of utterances for an intent which is defined in the AlexaOutput
     */
    public Optional<String> getRandomUtterance(final AlexaOutput output) {
        return getRandomUtterance(output.getIntentName());
    }

    /**
     * Returns a set of utterances for a given intent.
     * @param intentName the intent name whose utterances are to be returned
     * @return set of utterances for the given intent
     */
    public List<String> getUtterances(final String intentName) {
        return getPhrasesForIntent(intentName, 0);
    }

    /**
     * Returns an utterance randomly picked from a set of utterances for a given intent.
     * @param intentName The intent name.
     * @return utterance randomly picked from a set of utterances for the given intent
     */
    public Optional<String> getRandomUtterance(final String intentName) {
        return getRandomOf(getPhrasesForIntent(intentName, 0));
    }

    /**
     * Returns a set of reprompt utterances for an intent which is defined in the AlexaOutput.
     * @param output The AlexaOutput containing the intent name whose reprompt utterances are to be returned.
     * @return set of reprompt utterances for an intent which is defined in the AlexaOutput
     */
    public List<String> getReprompts(final AlexaOutput output) {
        return getReprompts(output.getIntentName());
    }

    /**
     * Returns a reprompt utterance randomly picked from a set of reprompt utterances for an intent which is defined in the AlexaOutput.
     * @param output The AlexaOutput containing the intent name.
     * @return reprompt utterance randomly picked from a set of reprompt utterances for an intent which is defined in the AlexaOutput
     */
    public Optional<String> getRandomReprompt(final AlexaOutput output) {
        return getRandomReprompt(output.getIntentName());
    }

    /**
     * Returns a set of reprompt utterances for a given intent.
     * @param intentName the intent name whose reprompt utterances are to be returned
     * @return set of reprompt utterances for the given intent
     */
    public List<String> getReprompts(final String intentName) {
        return getPhrasesForIntent(intentName, 1);
    }

    /**
     * Returns a reprompt utterance randomly picked from a set of reprompt utterances for a given intent.
     * @param intentName The intent name.
     * @return reprompt utterance randomly picked from a set of reprompt utterances for the given intent
     */
    public Optional<String> getRandomReprompt(final String intentName) {
        return getRandomOf(getPhrasesForIntent(intentName, 1));
    }

    private List<Object> loadUtterances(final String intentName) {
        // flatten yaml strings values beneath intent node of interest
        return content.entrySet().stream()
                .filter(k -> k.getKey().equals(intentName))
                .flatMap(k -> flatten(k.getValue())).collect(Collectors.toList());
    }

    /**
     * Recursively go along yaml nodes beneath the given one to flatten string values
     * @param o YAML node point of start
     * @return flattened values beneath given YAML node
     */
    private Stream<Object> flatten(final Object o) {
        if (o instanceof Map<?, ?>) {
            return ((Map<?, ?>) o).values().stream().flatMap(this::flatten);
        }
        return Stream.of(o);
    }

    @SuppressWarnings("unchecked")
    private List<String> getPhrasesForIntent(final String intentName, final Integer index) {
        Validate.notBlank(intentName, "Intent name is null or empty.");
        // return list of utterances if already read out and saved to local list
        final List<Object> contents = phrases.entrySet().stream()
                .filter(k -> k.getKey().equals(intentName))
                .findFirst()
                // otherwise load utterances from resource of utterance reader
                .orElse(new AbstractMap.SimpleEntry<>(intentName, loadUtterances(intentName)))
                .getValue();
        // cache the result
        phrases.putIfAbsent(intentName, contents);

        final List<String> utterances = new ArrayList<>();

        if (contents.size() > index) {
            // group node assumed to be an array list
            final Object assumedUtteranceCollection = contents.get(index);

            if (assumedUtteranceCollection instanceof ArrayList) {
                // parse each phrase as string and add to return collection
                ((ArrayList) assumedUtteranceCollection)
                        .stream()
                        .map(resolvePlaceholders)
                        .forEach(utterance -> utterances.add(String.valueOf(utterance)));
            } else if (assumedUtteranceCollection instanceof String) {
                utterances.add(String.valueOf(assumedUtteranceCollection));
            }
        }
        return utterances;
    }
    private Function<Object, String> resolvePlaceholders = (final Object utterance) -> {
        final StringBuffer buffer = new StringBuffer();
        // extract all the placeholders (e.g. ${placeholder}) found in the utterance
        final Matcher placeholders = Pattern.compile("\\$\\{(.*?)\\}").matcher(utterance.toString());
        // for any of the placeholders ...
        while (placeholders.find()) {
            final String placeholderName = placeholders.group(1);
            final List<String> placeholderValues = getPhrasesForIntent(placeholderName, 0);

            Validate.notEmpty(placeholderValues, "Utterance placeholder with name '" + placeholderName + "' could not be resolved.");
            placeholders.appendReplacement(buffer, placeholderValues.get(0));
        }
        placeholders.appendTail(buffer);
        return buffer.toString();
    };

    private Optional<String> getRandomOf(final List<String> list) {
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(new Random().nextInt(list.size())));
    }
}
