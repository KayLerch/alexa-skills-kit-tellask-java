package io.klerch.alexa.tellask.schema;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation necessary to introduce your AlexaLaunchHandler to
 * the annotation processor. AlexaLaunchHandlers not having this annotation
 * won't be considered on incoming launch requests.
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface AlexaLaunchListener {
}