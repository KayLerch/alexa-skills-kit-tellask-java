package io.klerch.alexa.tellask.compile;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.*;
import io.klerch.alexa.tellask.model.AlexaInput;
import io.klerch.alexa.tellask.schema.AlexaIntentHandler;
import io.klerch.alexa.tellask.schema.AlexaIntentListener;
import io.klerch.alexa.tellask.schema.AlexaIntentType;
import io.klerch.alexa.tellask.util.AlexaIntentHandlerFactory;
import org.apache.commons.lang3.Validate;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@AutoService(Processor.class)
@SupportedAnnotationTypes("io.klerch.alexa.tellask.schema.AlexaIntentListener")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class AlexaIntentListenerProcessor extends AbstractProcessor {
    private ProcessingEnvironment processingEnv;
    private final List<String> intentsProcessed = new ArrayList<>();

    public AlexaIntentListenerProcessor() {
    }

    @Override
    public synchronized void init(final ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.processingEnv = processingEnv;
    }

    @Override
    public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) return true;

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
                // process for all valid classes having the Listener-annotation
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
                    .writeTo(processingEnv.getFiler());
        } catch (final IOException e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Could not generate factory caused by " + e.getMessage());
            return false;
        }
        return true;
    }

    private Function<TypeElement, CodeBlock> generateCode = (final TypeElement element) -> {
        // the intent type comes from the annotation
        final AlexaIntentType intentType = element.getAnnotation(AlexaIntentListener.class).IntentType();

        // do only consider intent name if custom intent set, otherwise pick name coming from enum
        final String intentName = AlexaIntentType.INTENT_CUSTOM.equals(intentType) ?
                element.getAnnotation(AlexaIntentListener.class).IntentName() : intentType.getName();

        final ClassName handlerClass = ClassName.get(element);

        Validate.isTrue(!intentsProcessed.contains(intentName), "There's another intent handler for intent '" + intentName + "'. " + handlerClass.simpleName() + " won't be considered for handling intents.");

        intentsProcessed.add(intentName);

        // an if-statement checks for the intent-name and returns an instance of the corresponding handler
        return CodeBlock.of("if($S.equals(input.getIntentRequest().getIntent().getName()))" +
                "{" +
                "return new $T();" +
                "}", intentName, handlerClass);
    };

    private Predicate<TypeElement> isConcretePublicClass = (final TypeElement t) -> {
        final boolean condition = !(t.getModifiers().contains(Modifier.ABSTRACT) &&
                t.getModifiers().contains(Modifier.PUBLIC));

        if (!condition) {
            final ClassName handlerClass = ClassName.get(t);
            processingEnv.getMessager().printMessage(Diagnostic.Kind.MANDATORY_WARNING, "Class " + handlerClass.simpleName() + " is annotated with " + AlexaIntentListener.class.getSimpleName() + " but is abstract or not public. It won't be considered for handling intents.");
        }
        return condition;
    };


    private Predicate<TypeElement> implementsAlexaIntentHandler = (final TypeElement t) -> {
        final TypeMirror alexaHandler = processingEnv.getElementUtils().getTypeElement(AlexaIntentHandler.class.getTypeName()).asType();
        final boolean condition = processingEnv.getTypeUtils().isAssignable(t.asType(), alexaHandler);

        if (!condition) {
            final ClassName handlerClass = ClassName.get(t);
            processingEnv.getMessager().printMessage(Diagnostic.Kind.MANDATORY_WARNING, "Class " + handlerClass.simpleName() + " is annotated with " + AlexaIntentListener.class.getSimpleName() + " but does not implement " + AlexaIntentHandler.class.getSimpleName() + ". It won't be considered for handling intents.");
        }
        return condition;
    };

    private Predicate<TypeElement> hasDefaultConstructor = (final TypeElement t) -> {
        final boolean condition = t.getEnclosedElements().stream()
                .filter(e -> ElementKind.CONSTRUCTOR.equals(e.getKind()))
                .map(e -> (ExecutableElement) e)
                .filter(e -> e.getParameters().isEmpty())
                .filter(e -> e.getModifiers().contains(Modifier.PUBLIC))
                .findFirst().isPresent();

        if (!condition) {
            final ClassName handlerClass = ClassName.get(t);
            processingEnv.getMessager().printMessage(Diagnostic.Kind.MANDATORY_WARNING, "Class " + handlerClass.simpleName() + " is annotated with " + AlexaIntentListener.class.getSimpleName() + " but does not contain a public default constructor. It won't be considered for handling intents.");
        }
        return condition;
    };
}
