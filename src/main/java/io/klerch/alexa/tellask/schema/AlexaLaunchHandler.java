/**
 * Created by Kay Lerch (https://twitter.com/KayLerch)
 *
 * Contribute to https://github.com/KayLerch/alexa-skills-kit-tellask-java
 *
 * Attached license applies.
 * This source is licensed under GNU GENERAL PUBLIC LICENSE Version 3 as of 29 June 2007
 */
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
