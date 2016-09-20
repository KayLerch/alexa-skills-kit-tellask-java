package io.klerch.alexa.tellask.schema;

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
    AlexaIntentType IntentType() default AlexaIntentType.INTENT_CUSTOM;
    String IntentName() default "";
    int Priority() default 0;
}