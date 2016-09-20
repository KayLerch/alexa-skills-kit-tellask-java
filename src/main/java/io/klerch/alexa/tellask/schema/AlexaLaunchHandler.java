package io.klerch.alexa.tellask.schema;

/**
 * An interface for a AlexaLaunchHandler handling a launch event
 * in your skill. It doesn't make sense to have more than one of these
 * handlers in your skill project. It's necessary to have one at all.
 * The handler will only be considered to handle a launch request
 * by tagging the implementation class with the AlexaLaunchListener-annotation.
 */
public interface AlexaLaunchHandler extends AlexaRequestHandler {
}
