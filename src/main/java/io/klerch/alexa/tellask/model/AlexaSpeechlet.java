package io.klerch.alexa.tellask.model;

import com.amazon.speech.speechlet.*;
import io.klerch.alexa.state.utils.AlexaStateException;
import io.klerch.alexa.tellask.schema.AlexaIntentHandler;
import io.klerch.alexa.tellask.schema.AlexaLaunchHandler;
import io.klerch.alexa.tellask.schema.AlexaRequestHandler;
import io.klerch.alexa.tellask.util.AlexaIntentHandlerFactory;
import io.klerch.alexa.tellask.util.AlexaLaunchHandlerFactory;
import org.apache.log4j.Logger;

/**
 * The AlexaSpeechlet is the actual handler of incoming speechlet requests. It is
 * an extended version of the general Speechlet of the Alexa Skills Kit SDK. You don't
 * need your own extension of this class as this one already gets back to all the handlers
 * annotated with either AlexaLaunchListener or AlexaIntentListener. Most likely you want
 * to override onSessionStarted and onSessionEnded to have your own routines implemented.
 */
public class AlexaSpeechlet implements Speechlet {
    private final Logger LOG = Logger.getLogger(AlexaSpeechlet.class);
    public final String LOCALE;

    public AlexaSpeechlet(final String locale) {
        this.LOCALE = locale;
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
        final AlexaInput input = new AlexaInput(request, session, LOCALE);
        final AlexaLaunchHandler handler = AlexaLaunchHandlerFactory.createHandler().orElse(null);
        return handleRequest(input, handler);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SpeechletResponse onIntent(final IntentRequest request, final Session session) throws SpeechletException {
        final AlexaInput input = new AlexaInput(request, session, LOCALE);
        final AlexaIntentHandler handler = AlexaIntentHandlerFactory.createHandler(input).orElse(null);
        return handleRequest(input, handler);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSessionEnded(final SessionEndedRequest request, final Session session) throws SpeechletException {
        LOG.debug("Session has ended.");
    }

    private AlexaSpeechletResponse handleRequest(final AlexaInput input, final AlexaRequestHandler handler) throws SpeechletException {
        AlexaSpeechletResponse response;

        if (handler == null) {
            throw new SpeechletException("Could not find a handler for speechlet request");
        }

        try {
            final AlexaOutput output = handler.handleRequest(input);
            // save state of all models
            output.getModels().stream().forEach(model -> {
                try {
                    // ensure model has a handler. by default choose the session state handler
                    if (model.getHandler() == null) {
                        input.getSessionHandler().writeModel(model.getModel());
                    } else {
                        model.saveState();
                    }
                } catch (final AlexaStateException e) {
                    LOG.error("Error while saving state of a model.", e);
                }
            });
            // generate speechlet response from settings returned by the intent handler and
            // contents of YAML utterance file
            response = new AlexaSpeechletResponse(output);
        } catch (final Exception e) {
            LOG.error("Error while handling an intent.", e);
            response = new AlexaSpeechletResponse(handler.handleError(input, e));
        }
        return response;
    }
}
