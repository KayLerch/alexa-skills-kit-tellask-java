/**
 * Created by Kay Lerch (https://twitter.com/KayLerch)
 *
 * Contribute to https://github.com/KayLerch/alexa-skills-kit-tellask-java
 *
 * Attached license applies.
 * This source is licensed under GNU GENERAL PUBLIC LICENSE Version 3 as of 29 June 2007
 */
package io.klerch.alexa.tellask.processor;

import com.google.testing.compile.JavaFileObjects;
import io.klerch.alexa.tellask.schema.annotation.AlexaIntentListener;
import org.junit.Assert;
import org.junit.Test;

import javax.tools.JavaFileObject;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

public class AlexaIntentListenerProcessorTest {
    @Test
    public void getSupportedAnnotationTypes() throws Exception {
        final AlexaIntentListenerProcessor processor = new AlexaIntentListenerProcessor();
        Assert.assertTrue(processor.getSupportedAnnotationTypes().contains(AlexaIntentListener.class.getTypeName()));
    }

    @Test
    public void processValid() throws Exception {
        final JavaFileObject fileObject = JavaFileObjects.forResource("handler/ValidIntentHandler.java");
        assertAbout(javaSource())
                .that(fileObject)
                .processedWith(new AlexaIntentListenerProcessor())
                .compilesWithoutError();
    }
}