/**
 * Created by Kay Lerch (https://twitter.com/KayLerch)
 *
 * Contribute to https://github.com/KayLerch/alexa-skills-kit-tellask-java
 *
 * Attached license applies.
 * This source is licensed under GNU GENERAL PUBLIC LICENSE Version 3 as of 29 June 2007
 */
package io.klerch.alexa.tellask.schema.annotation;

import io.klerch.alexa.tellask.model.wrapper.AlexaSpeechlet;

import java.lang.annotation.*;

/**
 * You can use this annotation to tag the request stream handler for providing
 * supported application-ids and your custom speechlet.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AlexaApplication {
    /**
     * An enumeration of supported application-ids. Incoming speechlet requests having
     * an application id not listed here (or in the corresponding getter of the request handler)
     * will be rejected.
     * @return set of supported application-ids
     */
    String[] applicationIds() default {};

    /**
     * A speechlet handler to delegate speechlet requests to. If you don't provide
     * your custom handler which needs to extend AlexaSpeechlet then the default
     * implementation AlexaSpeechlet is used.
     * @return speechlet handler to delegate speechlet requests to
     */
    Class<? extends AlexaSpeechlet> speechlet() default AlexaSpeechlet.class;
}