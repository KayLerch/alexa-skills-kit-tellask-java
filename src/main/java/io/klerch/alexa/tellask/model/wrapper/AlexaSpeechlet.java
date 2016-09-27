/**
 * Created by Kay Lerch (https://twitter.com/KayLerch)
 *
 * Contribute to https://github.com/KayLerch/alexa-skills-kit-tellask-java
 *
 * Attached license applies.
 * This source is licensed under GNU GENERAL PUBLIC LICENSE Version 3 as of 29 June 2007
 */
package io.klerch.alexa.tellask.model.wrapper;

import com.amazon.speech.speechlet.*;
import io.klerch.alexa.state.utils.AlexaStateException;
import io.klerch.alexa.tellask.model.AlexaInput;
import io.klerch.alexa.tellask.model.AlexaIntentModel;
import io.klerch.alexa.tellask.model.AlexaOutput;
import io.klerch.alexa.tellask.schema.AlexaIntentHandler;
import io.klerch.alexa.tellask.schema.AlexaLaunchHandler;
import io.klerch.alexa.tellask.schema.AlexaRequestHandler;
import io.klerch.alexa.tellask.schema.UtteranceReader;
import io.klerch.alexa.tellask.util.factory.AlexaIntentHandlerFactory;
import io.klerch.alexa.tellask.util.factory.AlexaLaunchHandlerFactory;
import io.klerch.alexa.tellask.util.AlexaRequestHandlerException;
import org.apache.commons.lang3.Validate;
import org.apache.log4j.Logger;

import java.util.function.Consumer;

/**
 * The AlexaSpeechlet is the actual handler of incoming speechlet requests. It is
 * an extended version of the general speechlet of the Alexa Skills Kit SDK. You don't
 * need your own extension of this class as this one already gets back to all the handlers
 * annotated with either AlexaLaunchListener or AlexaIntentListener. Most likely you want
 * to override onSessionStarted and onSessionEnded to have your own routines implemented.
 */
public class AlexaSpeechlet implements Speechlet {
    private static final Logger LOG = Logger.getLogger(AlexaSpeechlet.class);
    private final String locale;
    private final UtteranceReader utteranceReader;
    private AlexaInput input;

    private final Consumer<AlexaIntentModel> saveModelState = model -> {
        try {
            // ensure model has a handler. by default choose the session state handler
            if (model.getHandler() == null) {
                input.getSessionStateHandler().writeModel(model.getModel());
            } else {
                model.saveState();
            }
        } catch (final AlexaStateException e) {
            LOG.error("Error while saving state of a model.", e);
        }
    };

    /**
     * A new extended speechlet handler working in the context of a locale
     * @param locale the locale provided by the speechlet request
     * @param utteranceReader the reader to use when reading out utterances
     */
    public AlexaSpeechlet(final String locale, final UtteranceReader utteranceReader) {
        Validate.notNull(utteranceReader, "Utterance reader must not be null.");
        this.utteranceReader = utteranceReader;
        this.locale = locale;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSessionStarted(final SessionStartedRequest request, final Session session) throws SpeechletException {
        LOG.debug("Session has started.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SpeechletResponse onLaunch(final LaunchRequest request, final Session session) throws SpeechletException {
        input = new AlexaInput(request, session, locale);
        final AlexaLaunchHandler handler = AlexaLaunchHandlerFactory.createHandler().orElse(null);
        return handleRequest(handler);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SpeechletResponse onIntent(final IntentRequest request, final Session session) throws SpeechletException {
        input = new AlexaInput(request, session, locale);
        final AlexaIntentHandler handler = AlexaIntentHandlerFactory.createHandler(input).orElse(null);
        return handleRequest(handler);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSessionEnded(final SessionEndedRequest request, final Session session) throws SpeechletException {
        LOG.debug("Session has ended.");
    }

    private AlexaSpeechletResponse handleRequest(final AlexaRequestHandler handler) throws SpeechletException {
        AlexaSpeechletResponse response;

        if (handler == null) {
            throw new SpeechletException("Could not find a handler for speechlet request");
        }

        try {
            final AlexaOutput output = handler.handleRequest(input);
            // save state of all models
            output.getModels().stream().forEach(saveModelState);
            // generate speechlet response from settings returned by the intent handler and
            // contents of YAML utterance file
            response = new AlexaSpeechletResponse(output, utteranceReader, locale);
        } catch (final AlexaRequestHandlerException e) {
            final AlexaRequestHandlerException exception = e.getInput() == null ?
                    new AlexaRequestHandlerException(e.getMessage(), e.getCause(), input, e.getErrorIntent()) : e;
            LOG.error("Error while handling an intent.", exception);
            response = new AlexaSpeechletResponse(handler.handleError(exception), utteranceReader, locale);
        } catch (final AlexaStateException e) {
            final AlexaRequestHandlerException exception = new AlexaRequestHandlerException("Error while handling state.", e, input, null);
            LOG.error(exception);
            response = new AlexaSpeechletResponse(handler.handleError(exception), utteranceReader, locale);
        } catch (final Exception e) {
            final AlexaRequestHandlerException exception = new AlexaRequestHandlerException("General error occured.", e, input, null);
            LOG.error(exception);
            response = new AlexaSpeechletResponse(handler.handleError(exception), utteranceReader, locale);
        }
        return response;
    }
}
