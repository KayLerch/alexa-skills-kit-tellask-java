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
import io.klerch.alexa.tellask.model.AlexaInput;
import io.klerch.alexa.tellask.schema.AlexaIntentHandler;
import io.klerch.alexa.tellask.schema.annotation.AlexaIntentListener;
import io.klerch.alexa.tellask.schema.type.AlexaIntentType;
import io.klerch.alexa.tellask.util.factory.AlexaIntentHandlerFactory;
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
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * This annotation processors scans your skill project for classes
 * tagged with the AlexaIntentListener annotation. It creates a factory
 * encapsulating the logic of returning an instance of an AlexaIntentHandler
 * according to an AlexaInput having the intent name. The factory also calls
 * the verify-method of the AlexaIntentHandler which should return true in order
 * to be considered by the factory.
 */
@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class AlexaIntentListenerProcessor extends AbstractProcessor {
    private static final Logger LOG = Logger.getLogger(AlexaIntentListenerProcessor.class);

    private ProcessingEnvironment processingEnvironment;

    private Function<TypeElement, CodeBlock> generateCode = (final TypeElement element) -> {
        // the intent type comes from the annotation
        final AlexaIntentType intentType = element.getAnnotation(AlexaIntentListener.class).intentType();

        // do only consider intent name if custom intent set, otherwise pick name coming from enum
        final String intentName = AlexaIntentType.INTENT_CUSTOM.equals(intentType) ?
                element.getAnnotation(AlexaIntentListener.class).intentName() : intentType.getName();

        final ClassName handlerClass = ClassName.get(element);

        // an if-statement checks for the intent-name and returns an instance of the corresponding handler
        return CodeBlock.of("if($S.equals(input.getIntentName()))" +
                "{" +
                "final " + AlexaIntentHandler.class.getSimpleName() + " handler = new $T();" +
                "if (handler.verify(input)) return handler;" +
                "}", intentName, handlerClass);
    };

    private Predicate<TypeElement> isConcretePublicClass = (final TypeElement t) -> {
        final boolean condition = !(t.getModifiers().contains(Modifier.ABSTRACT) &&
                t.getModifiers().contains(Modifier.PUBLIC));

        if (!condition) {
            final ClassName handlerClass = ClassName.get(t);
            processingEnvironment.getMessager().printMessage(Diagnostic.Kind.MANDATORY_WARNING, "Class " + handlerClass.simpleName() + " is annotated with " + AlexaIntentListener.class.getSimpleName() + " but is abstract or not public. It won't be considered for handling intents.");
        }
        return condition;
    };


    private Predicate<TypeElement> implementsAlexaIntentHandler = (final TypeElement t) -> {
        final TypeMirror alexaHandler = processingEnvironment.getElementUtils().getTypeElement(AlexaIntentHandler.class.getTypeName()).asType();
        final boolean condition = processingEnvironment.getTypeUtils().isAssignable(t.asType(), alexaHandler);

        if (!condition) {
            final ClassName handlerClass = ClassName.get(t);
            processingEnvironment.getMessager().printMessage(Diagnostic.Kind.MANDATORY_WARNING, "Class " + handlerClass.simpleName() + " is annotated with " + AlexaIntentListener.class.getSimpleName() + " but does not implement " + AlexaIntentHandler.class.getSimpleName() + ". It won't be considered for handling intents.");
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
            processingEnvironment.getMessager().printMessage(Diagnostic.Kind.MANDATORY_WARNING, "Class " + handlerClass.simpleName() + " is annotated with " + AlexaIntentListener.class.getSimpleName() + " but does not contain a public default constructor. It won't be considered for handling intents.");
        }
        return condition;
    };

    private Comparator<TypeElement> byPriority = (o1, o2) -> o2.getAnnotation(AlexaIntentListener.class).priority() -
            o1.getAnnotation(AlexaIntentListener.class).priority();

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singletonList(AlexaIntentListener.class.getTypeName()).stream().collect(Collectors.toSet());
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

        final MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(AlexaIntentHandlerFactory.FACTORY_METHOD_NAME)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(AlexaIntentHandler.class)
                .addParameter(AlexaInput.class, "input");

        final List<CodeBlock> codeBlocks = roundEnv.getElementsAnnotatedWith(AlexaIntentListener.class).stream()
                // only interested in tagged classes
                .filter(e -> e.getKind() == ElementKind.CLASS)
                // cast as type
                .map(e -> (TypeElement) e)
                // must implement AlexaIntentHandler
                .filter(implementsAlexaIntentHandler)
                // must match certain criteria like being public and not abstract
                .filter(isConcretePublicClass)
                // must also have a public default constructor
                .filter(hasDefaultConstructor)
                // sort descending by priority (important for multiple intent-handlers for same intent)
                .sorted(byPriority)
                // process for all valid classes having the AlexaIntentListener-annotation
                .map(generateCode)
                // to list
                .collect(Collectors.toList());

        if (codeBlocks.isEmpty()) {
            return true;
        } else {
            codeBlocks.forEach(methodBuilder::addCode);
        }

        // add one last lines which returns null in case of no intenthandler found
        final MethodSpec method = methodBuilder.addCode("return null;").build();

        final TypeSpec alexaIntentHandlerFactory = TypeSpec.classBuilder(AlexaIntentHandlerFactory.FACTORY_CLASS_NAME)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(method)
                .build();

        try {
            JavaFile.builder(AlexaIntentHandlerFactory.FACTORY_PACKAGE, alexaIntentHandlerFactory)
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
