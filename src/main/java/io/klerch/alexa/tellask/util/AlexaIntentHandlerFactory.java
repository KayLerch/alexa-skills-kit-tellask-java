package io.klerch.alexa.tellask.util;

import io.klerch.alexa.tellask.model.AlexaInput;
import io.klerch.alexa.tellask.schema.AlexaIntentHandler;
import org.apache.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;

/**
 * Constructs the AlexaIntentHandler which is tagged with the AlexaIntentListener-annotation.
 */
public class AlexaIntentHandlerFactory {
    private final static Logger LOG = Logger.getLogger(AlexaIntentHandlerFactory.class);

    public static final String FACTORY_PACKAGE = AlexaIntentHandlerFactory.class.getPackage().getName();
    public static final String FACTORY_CLASS_NAME = AlexaIntentHandlerFactory.class.getSimpleName() + "Impl";
    public static final String FACTORY_METHOD_NAME = "createHandler";

    /**
     * Constructs the AlexaIntentHandler which is tagged with the AlexaIntentListener-annotation.
     * If more than one handler for an intent is found it returns the one whose verify-method returns
     * true. If even then there's more than one handler the one with the highest priority wins (you
     * set the priority of an AlexaIntentHanlder in the AlexaIntentListener-annotation.
     * @param input the input which should be handled. Most important is the intent name in the
     *              input as it is the matching criteria for finding the right intent handler.
     * @return AlexaLaunchHandler to handle launch events of your skill
     */
    @SuppressWarnings("unchecked")
    public static Optional<AlexaIntentHandler> createHandler(final AlexaInput input) {
        try {
            final Class factoryImpl = Class.forName(FACTORY_PACKAGE + "." + FACTORY_CLASS_NAME);
            final Method method = factoryImpl.getMethod(FACTORY_METHOD_NAME, AlexaInput.class);
            final Object handler = method.invoke(null, input);
            return handler != null ? Optional.of((AlexaIntentHandler)handler) : Optional.empty();
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            LOG.error("Could not access generated factory to obtain intent handlers likely because there is no valid intent handler in your project at all.", e);
            return Optional.empty();
        }
    }
}
