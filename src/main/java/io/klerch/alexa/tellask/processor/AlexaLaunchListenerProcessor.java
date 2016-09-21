/**
 * Created by Kay Lerch (https://twitter.com/KayLerch)
 *
 * Contribute to https://github.com/KayLerch/alexa-skills-kit-tellask-java
 *
 * Attached license applies.
 * This source is licensed under GNU GENERAL PUBLIC LICENSE Version 3 as of 29 June 2007
 */
package io.klerch.alexa.tellask.processor;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.*;
import io.klerch.alexa.tellask.schema.AlexaLaunchHandler;
import io.klerch.alexa.tellask.schema.annotation.AlexaLaunchListener;
import io.klerch.alexa.tellask.util.factory.AlexaLaunchHandlerFactory;
import org.apache.log4j.Logger;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * This annotation processors scans your skill project for classes
 * tagged with the AlexaLaunchListener annotation. It creates a factory
 * encapsulating the ONE launch listener as only a single one makes sence
 * in your skill project. The processor prints a mandatory warning in case
 * it found more than one AlexaLaunchListener-tagged AlexaLaunchHandlers. It
 * won't fail in this case and simply picks the first handler found in the project.
 */
@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class AlexaLaunchListenerProcessor extends AbstractProcessor {
    private static final Logger LOG = Logger.getLogger(AlexaLaunchListenerProcessor.class);
    private ProcessingEnvironment processingEnvironment;

    private Function<TypeElement, CodeBlock> generateCode = (final TypeElement element) -> {
        final ClassName handlerClass = ClassName.get(element);
        return CodeBlock.of("return new $T();", handlerClass);
    };

    private Predicate<TypeElement> isConcretePublicClass = (final TypeElement t) -> {
        final boolean condition = !(t.getModifiers().contains(Modifier.ABSTRACT) &&
                t.getModifiers().contains(Modifier.PUBLIC));

        if (!condition) {
            final ClassName handlerClass = ClassName.get(t);
            processingEnvironment.getMessager().printMessage(Diagnostic.Kind.MANDATORY_WARNING, "Class " + handlerClass.simpleName() + " is annotated with " + AlexaLaunchListener.class.getSimpleName() + " but is abstract or not public. It won't be considered for handling intents.");
        }
        return condition;
    };

    private Predicate<TypeElement> implementsAlexaLaunchHandler = (final TypeElement t) -> {
        final TypeMirror alexaHandler = processingEnvironment.getElementUtils().getTypeElement(AlexaLaunchHandler.class.getTypeName()).asType();
        final boolean condition = processingEnvironment.getTypeUtils().isAssignable(t.asType(), alexaHandler);

        if (!condition) {
            final ClassName handlerClass = ClassName.get(t);
            processingEnvironment.getMessager().printMessage(Diagnostic.Kind.MANDATORY_WARNING, "Class " + handlerClass.simpleName() + " is annotated with " + AlexaLaunchListener.class.getSimpleName() + " but does not implement " + AlexaLaunchHandler.class.getSimpleName() + ". It won't be considered for handling intents.");
        }
        return condition;
    };

    private Predicate<TypeElement> hasDefaultConstructor = (final TypeElement t) -> {
        final boolean condition = t.getEnclosedElements().stream()
                // for all constructors
                .filter(e -> ElementKind.CONSTRUCTOR.equals(e.getKind()))
                .map(e -> (ExecutableElement) e)
                // check for parameterless methods
                .filter(e -> e.getParameters().isEmpty())
                // check for public methods
                .filter(e -> e.getModifiers().contains(Modifier.PUBLIC))
                // needs at least one constructor matching the above conditions
                .findFirst().isPresent();

        if (!condition) {
            final ClassName handlerClass = ClassName.get(t);
            processingEnvironment.getMessager().printMessage(Diagnostic.Kind.MANDATORY_WARNING, "Class " + handlerClass.simpleName() + " is annotated with " + AlexaLaunchListener.class.getSimpleName() + " but does not contain a public default constructor. It won't be considered for handling intents.");
        }
        return condition;
    };

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singletonList(AlexaLaunchListener.class.getTypeName()).stream().collect(Collectors.toSet());
    }

    @Override
    public synchronized void init(final ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.processingEnvironment = processingEnv;
    }

    @Override
    public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            return true;
        }

        final MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(AlexaLaunchHandlerFactory.FACTORY_METHOD_NAME)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(AlexaLaunchHandler.class);

        final List<CodeBlock> codeBlocks = roundEnv.getElementsAnnotatedWith(AlexaLaunchListener.class).stream()
                // only interested in tagged classes
                .filter(e -> e.getKind() == ElementKind.CLASS)
                // cast as type
                .map(e -> (TypeElement) e)
                // must implement AlexaIntentHandler
                .filter(implementsAlexaLaunchHandler)
                // must match certain criteria like being public and not abstract
                .filter(isConcretePublicClass)
                // must also have a public default constructor
                .filter(hasDefaultConstructor)
                // process for all valid classes having the AlexaLaunchListener-annotation
                .map(generateCode)
                // to list
                .collect(Collectors.toList());

        if (codeBlocks.isEmpty()) {
            return true;
        } else if (codeBlocks.size() > 1) {
            processingEnvironment.getMessager().printMessage(Diagnostic.Kind.MANDATORY_WARNING, "There is more than one class annotated with " + AlexaLaunchListener.class.getSimpleName() + ". Only one of them will be considered.");
        }

        // add only the first
        methodBuilder.addCode(codeBlocks.get(0));

        final MethodSpec method = methodBuilder.build();

        final TypeSpec alexaIntentHandlerFactory = TypeSpec.classBuilder(AlexaLaunchHandlerFactory.FACTORY_CLASS_NAME)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(method)
                .build();

        try {
            JavaFile.builder(AlexaLaunchHandlerFactory.FACTORY_PACKAGE, alexaIntentHandlerFactory)
                    .build()
                    .writeTo(processingEnvironment.getFiler());
        } catch (final IOException e) {
            LOG.error(e);
            processingEnvironment.getMessager().printMessage(Diagnostic.Kind.ERROR, "Could not generate factory caused by " + e.getMessage());
            return false;
        }
        return true;
    }
}
