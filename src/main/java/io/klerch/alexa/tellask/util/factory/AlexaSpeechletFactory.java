package io.klerch.alexa.tellask.util.factory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.klerch.alexa.tellask.model.wrapper.AlexaSpeechlet;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

/**
 * This factory is for creating an AlexaSpeechlet
 */
public class AlexaSpeechletFactory {
    public static final String DEFAULT_LOCALE = "en-US";

    /**
     * Creates an AlexaSpeechlet from bytes of a speechlet request. It will extract the
     * locale from the request and uses it for creating a new instance of AlexaSpeechlet
     * @param serializedSpeechletRequest bytes of a speechlet request
     * @param speechletClass the class of your AlexaSpeechlet to instantiate
     * @param <T> must extend AlexaSpeechlet
     * @return new instance of AlexaSpeechlet
     * @throws IOException thrown when something went wrong
     */
    public static <T extends AlexaSpeechlet> T createSpeechletFromRequest(final byte[] serializedSpeechletRequest, final Class<T> speechletClass) throws IOException {
        final ObjectMapper mapper = new ObjectMapper();

        final JsonNode parser = mapper.readTree(serializedSpeechletRequest);
        final String locale = Optional.of(parser.path("request"))
                .filter(node -> !node.isMissingNode())
                .map(node -> node.path("locale"))
                .filter(node -> !node.isMissingNode())
                .map(JsonNode::textValue)
                .orElse(DEFAULT_LOCALE);

        try {
            return speechletClass.getConstructor(String.class).newInstance(locale);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new IOException("Could not create Speechlet from speechlet request", e);
        }
    }
}
