/**
 * Created by Kay Lerch (https://twitter.com/KayLerch)
 *
 * Contribute to https://github.com/KayLerch/alexa-skills-kit-tellask-java
 *
 * Attached license applies.
 * This source is licensed under GNU GENERAL PUBLIC LICENSE Version 3 as of 29 June 2007
 */
package io.klerch.alexa.tellask.dummies.servlet;

import io.klerch.alexa.tellask.dummies.SampleAlexaSpeechlet;
import io.klerch.alexa.tellask.model.wrapper.AlexaSpeechlet;
import io.klerch.alexa.tellask.model.wrapper.AlexaSpeechletServlet;
import io.klerch.alexa.tellask.schema.annotation.AlexaApplication;

@AlexaApplication(applicationIds = "applicationId")
public class SampleServlet extends AlexaSpeechletServlet {
    @Override
    public Class<? extends AlexaSpeechlet> getAlexaSpeechlet() {
        return SampleAlexaSpeechlet.class;
    }
}
