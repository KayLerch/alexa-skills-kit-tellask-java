/**
 * Created by Kay Lerch (https://twitter.com/KayLerch)
 *
 * Contribute to https://github.com/KayLerch/alexa-skills-kit-tellask-java
 *
 * Attached license applies.
 * This source is licensed under GNU GENERAL PUBLIC LICENSE Version 3 as of 29 June 2007
 */
package io.klerch.alexa.tellask.model.wrapper;

import com.amazon.speech.Sdk;
import com.amazon.speech.speechlet.Speechlet;
import com.amazon.speech.speechlet.servlet.SpeechletServlet;
import io.klerch.alexa.state.handler.AlexaStateHandler;
import io.klerch.alexa.tellask.schema.UtteranceReader;
import io.klerch.alexa.tellask.schema.annotation.AlexaApplication;
import io.klerch.alexa.tellask.util.factory.AlexaSpeechletFactory;
import io.klerch.alexa.tellask.util.resource.ResourceUtteranceReader;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A servlet extending the SpeechletServlet to comply with Tellask SDK.
 * You can provide supported application-ids and your own AlexaSpeechlet
 * by using AlexaApplication annotation or by overriding the corresponding
 * getters
 */
public abstract class AlexaSpeechletServlet extends SpeechletServlet {
    /**
     * When this servlet is created it obtains supported application ids from overridden getter
     * or AlexaApplication-annotation and joins this set of ids with supported application ids
     * configured in system property 'com.amazon.speech.speechlet.servlet.supportedApplicationIds'
     */
    public AlexaSpeechletServlet() {
        super();

        final Set<String> customSupportedApplicationIds = getSupportedApplicationIds();

        if (!customSupportedApplicationIds.isEmpty()) {
            // add supported application ids to system variable
            final String systemSupportedApplicationIds = System.getProperty(Sdk.SUPPORTED_APPLICATION_IDS_SYSTEM_PROPERTY);

            if (systemSupportedApplicationIds != null) {
                customSupportedApplicationIds.addAll(Arrays.asList(systemSupportedApplicationIds.split(",")));
            }
            // update system property with appended application ids provided by this servlet
            System.setProperty(Sdk.SUPPORTED_APPLICATION_IDS_SYSTEM_PROPERTY, String.join(",", customSupportedApplicationIds));
        }
    }

    /**
     * Provides a set of application-id(s) you can find in the Alexa developer console of your skill.
     * Only requests coming in with these application-id(s) pass the request verification.
     * @return supported application ids
     */
    public Set<String> getSupportedApplicationIds() {
        final AlexaApplication app = this.getClass().getAnnotation(AlexaApplication.class);
        return app != null ? Stream.of(app.applicationIds()).collect(Collectors.toSet()) : Collections.emptySet();
    }

    /**
     * Provides the speechlet used to handle the request.
     * @return speechlet used to handle the request.
     */
    public Class<? extends AlexaSpeechlet> getAlexaSpeechlet() {
        final AlexaApplication app = this.getClass().getAnnotation(AlexaApplication.class);
        return app != null ? app.speechlet() : AlexaSpeechlet.class;
    }

    /**
     * Override this method to return a customized utterance-reader. This gives
     * you freedom of storing your utterance YAML files at any location you desire.
     * If you want to decide how to configure an utterance reader individually you
     * can set it inside your request handlers by giving it to AlexaOutput. That said
     * the value you set right here could be overridden.
     * @return customized utterance reader
     */
    public UtteranceReader getUtteranceReader() {
        return new ResourceUtteranceReader();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Speechlet getSpeechlet() {
        return super.getSpeechlet();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void setSpeechlet(final Speechlet speechlet) {
        // enforce speechlet which extends AlexaSpeechlet
        Validate.isInstanceOf(AlexaSpeechlet.class, speechlet, "Servlet expects a speechlet extending AlexaSpeechlet.");
        super.setSpeechlet(speechlet);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response)
            throws IOException {
        // wrap servlet request as we need to read from it before it is read a second time in super doPost
        final AlexaHttpServletRequest wrapper = new AlexaHttpServletRequest(request);
        // intervene post to extract locale but also to override the speechlet
        byte[] serializedSpeechletRequest = IOUtils.toByteArray(wrapper.getInputStream());
        final AlexaSpeechlet speechlet = AlexaSpeechletFactory.createSpeechletFromRequest(serializedSpeechletRequest, getAlexaSpeechlet(), getUtteranceReader());
        // override speechlet with AlexaSpeechlet
        setSpeechlet(speechlet);
        super.doPost(wrapper, response);
    }
}
