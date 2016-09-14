package io.klerch.alexa.tellask.compile;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import io.klerch.alexa.tellask.model.AlexaInput;
import io.klerch.alexa.tellask.schema.AlexaIntentHandler;
import io.klerch.alexa.tellask.schema.AlexaIntentListener;
import io.klerch.alexa.tellask.schema.AlexaIntentType;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

@AutoService(Processor.class)
@SupportedAnnotationTypes("io.klerch.alexa.tellask.schema.AlexaIntentListener")
public class AlexaIntentListenerProcessor extends AbstractProcessor {
    public static final String FACTORY_CLASS_NAME = "AlexaIntentHandlerFactory";
    public static final String FACTORY_METHOD_NAME = "createHandler";
    public static final String FACTORY_PACKAGE = "io.klerch.alexa.tellask.compile";
    private Types typeUtils;
    private Elements elementUtils;
    private Filer filer;
    private Messager messager;

    @Override
    public synchronized void init(final ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        typeUtils = processingEnv.getTypeUtils();
        elementUtils = processingEnv.getElementUtils();
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();
        this.messager.printMessage(Diagnostic.Kind.WARNING, "Init!!!!!!!!!!!!!!!");
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
        this.messager.printMessage(Diagnostic.Kind.WARNING, "Entered!!!!!!!!!!!!!!!");

        final MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(FACTORY_METHOD_NAME)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(AlexaIntentHandler.class)
                .addParameter(AlexaInput.class, "input");

        roundEnv.getElementsAnnotatedWith(AlexaIntentListener.class).stream()
                // only interested in tagged classes
                .filter(e -> e.getKind() == ElementKind.CLASS)
                // cast as type
                .map(e -> (TypeElement)e)
                // must match certain criteria like being public and not abstract
                .filter(isValidHandler)
                // must also have a public default constructor
                .filter(hasDefaultConstructor)
                // process for all valid classes having the Listener-annotation
                .map(generateCode)
                // add code-block to method
                .forEach(methodBuilder::addCode);

        // add one last lines which returns null in case of no intenthandler found
        final MethodSpec method = methodBuilder.addCode("return null;").build();

        final TypeSpec alexaIntentHandlerFactory = TypeSpec.classBuilder(FACTORY_CLASS_NAME)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(method)
                .build();

        final JavaFile file = JavaFile
                .builder(FACTORY_PACKAGE + "." + FACTORY_CLASS_NAME, alexaIntentHandlerFactory)
                .build();

        try {
            file.writeTo(System.out);
        } catch (IOException e) {
            messager.printMessage(Diagnostic.Kind.ERROR, "Could not generate factory. " + e.getMessage());
        }
        return true;
    }

    private Function<TypeElement, CodeBlock> generateCode = (final TypeElement element) -> {
        // the intent type comes from the annotation
        final AlexaIntentType intentType = element.getAnnotation(AlexaIntentListener.class).IntentType();

        // do only consider intent name if custom intent set, otherwise pick name coming from enum
        final String intentName = AlexaIntentType.INTENT_CUSTOM.equals(intentType) ?
                element.getAnnotation(AlexaIntentListener.class).IntentName() : intentType.getName();

        // an if-statement checks for the intent-name and returns an instance of the corresponding handler
        return CodeBlock.of("if($S.equals(input.getIntentRequest().getIntent().getName()))" +
                "{" +
                "return new $T();" +
                "}", intentName, element.getQualifiedName());
    };

    private Predicate<TypeElement> isValidHandler = (final TypeElement t) ->
        !t.getModifiers().contains(Modifier.ABSTRACT) &&
                t.getModifiers().contains(Modifier.PUBLIC);

    private Predicate<TypeElement> hasDefaultConstructor = (final TypeElement t) ->
            t.getEnclosedElements().stream()
                .filter(e -> ElementKind.CONSTRUCTOR.equals(e.getKind()))
                .map(e -> (ExecutableElement)e)
                .filter(e -> e.getParameters().isEmpty())
                .filter(e -> e.getModifiers().contains(Modifier.PUBLIC))
                .findFirst().isPresent();
}
