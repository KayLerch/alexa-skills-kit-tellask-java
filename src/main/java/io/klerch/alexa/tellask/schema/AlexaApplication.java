package io.klerch.alexa.tellask.schema;

import io.klerch.alexa.tellask.model.AlexaSpeechlet;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AlexaApplication {
    String[] ApplicationIds();
    Class<? extends AlexaSpeechlet> Speechlet() default AlexaSpeechlet.class;
}