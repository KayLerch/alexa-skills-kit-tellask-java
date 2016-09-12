package io.klerch.alexa.tellask.util;

import io.klerch.alexa.tellask.model.AlexaOutput;
import io.klerch.alexa.tellask.schema.UtteranceReader;
import io.klerch.alexa.tellask.test.Test;
import org.apache.commons.lang3.Validate;
import org.yaml.snakeyaml.Yaml;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class YamlReader {
    final UtteranceReader utteranceReader;
    final Map<String, List<Object>> phrases = new HashMap<>();

    public YamlReader(final UtteranceReader utteranceReader) {
        this.utteranceReader = utteranceReader;
    }

    public List<String> getUtterances(final AlexaOutput output) {
        return getUtterances(output.getIntentName());
    }

    public Optional<String> getRandomUtterance(final AlexaOutput output) {
        return getRandomUtterance(output.getIntentName());
    }

    public List<String> getUtterances(final String intentName) {
        return getPhrasesForIntent(intentName, 0);
    }

    public Optional<String> getRandomUtterance(final String intentName) {
        return getRandomOf(getPhrasesForIntent(intentName, 0));
    }

    public List<String> getReprompts(final AlexaOutput output) {
        return getReprompts(output.getIntentName());
    }

    public Optional<String> getRandomReprompt(final AlexaOutput output) {
        return getRandomReprompt(output.getIntentName());
    }

    public List<String> getReprompts(final String intentName) {
        return getPhrasesForIntent(intentName, 1);
    }

    public Optional<String> getRandomReprompt(final String intentName) {
        return getRandomOf(getPhrasesForIntent(intentName, 1));
    }

    private List<Object> loadUtterances(final String intentName) {
        // leverage reader to get yaml with utterances
        final Map<?, ?> content = new Yaml().loadAs(utteranceReader.read(), Map.class);

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
            return ((Map<?, ?>) o).values().stream().flatMap(Test::flatten);
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
            Object assumedUtteranceCollection = contents.get(index);
            if (assumedUtteranceCollection instanceof ArrayList) {
                // parse each phrase as string and add to return collection
                ((ArrayList)assumedUtteranceCollection).forEach(utterance -> utterances.add(String.valueOf(utterance)));
            }
        }
        return utterances;
    }

    private Optional<String> getRandomOf(final List<String> list) {
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(new Random().nextInt(list.size())));
    }
}
