package io.klerch.alexa.tellask.util.factory;

import io.klerch.alexa.tellask.schema.AlexaLaunchHandler;
import org.apache.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;

/**
 * Constructs the AlexaLaunchHandler which is tagged with the AlexaLaunchListener-annotation.
 */
public class AlexaLaunchHandlerFactory {
    public static final String FACTORY_PACKAGE = AlexaLaunchHandlerFactory.class.getPackage().getName();
    public static final String FACTORY_CLASS_NAME = AlexaLaunchHandlerFactory.class.getSimpleName() + "Impl";
    public static final String FACTORY_METHOD_NAME = "createHandler";

    private static final Logger LOG = Logger.getLogger(AlexaLaunchHandlerFactory.class);

    private AlexaLaunchHandlerFactory() {
        // hides the implicit public constructor
    }

    /**
     * Constructs the AlexaLaunchHandler which is tagged with the AlexaLaunchListener-annotation.
     * If more than one of those handlers were found it will only return the first one found as
     * your skill only needs one of them.
     * @return AlexaLaunchHandler to handle launch events of your skill
     */
    @SuppressWarnings("unchecked")
    public static Optional<AlexaLaunchHandler> createHandler() {
        try {
            final Class factoryImpl = Class.forName(FACTORY_PACKAGE + "." + FACTORY_CLASS_NAME);
            final Method method = factoryImpl.getMethod(FACTORY_METHOD_NAME);
            final Object handler = method.invoke(null);
            return handler != null ? Optional.of((AlexaLaunchHandler) handler) : Optional.empty();
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            LOG.error("Could not access generated factory to obtain launch handlers likely because there is no valid launch handler in your project at all.", e);
            return Optional.empty();
        }
    }
}
