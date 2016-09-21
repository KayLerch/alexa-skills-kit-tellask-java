package io.klerch.alexa.tellask.schema.annotation;

import io.klerch.alexa.tellask.schema.type.AlexaIntentType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation necessary to introduce your AlexaIntentHandlers to
 * the annotation processor. AlexaIntentHandlers not having this annotation
 * won't be considered on incoming intent requests.
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface AlexaIntentListener {
    /**
     * Defines the type of intent to listen for. Default is custom intent.
     * @return type of intent to listen for
     */
    AlexaIntentType intentType() default AlexaIntentType.INTENT_CUSTOM;

    /**
     * Defines the name of an intent. Only applies if intentType is set to custom (by default)
     * @return Defines the name of an intent. By default this name is blank.
     */
    String intentName() default "";

    /**
     * Defines the priority among intent handlers listen for the same intent. The AlexaIntentHandler
     * having a higher priority in its AlexaIntentListener-annotation is preferred if another AlexaIntentHandler
     * with the same intent also returns true in its verify-method. Default is 0.
     * @return priority among intent handlers listen for the same intent. Default is 0.
     */
    int priority() default 0;
}