package io.klerch.alexa.tellask.schema;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface AlexaIntentListener {
    AlexaIntentType IntentType() default AlexaIntentType.INTENT_CUSTOM;
    String IntentName() default "";
}