/**
 * Created by Kay Lerch (https://twitter.com/KayLerch)
 *
 * Contribute to https://github.com/KayLerch/alexa-skills-kit-tellask-java
 *
 * Attached license applies.
 * This source is licensed under GNU GENERAL PUBLIC LICENSE Version 3 as of 29 June 2007
 */
package io.klerch.alexa.tellask.dummies;

import io.klerch.alexa.tellask.model.wrapper.AlexaSpeechlet;

public class SampleAlexaSpeechlet extends AlexaSpeechlet {
    /**
     * A new extended speechlet handler working in the context of a locale
     *
     * @param locale the locale provided by the speechlet request
     */
    public SampleAlexaSpeechlet(String locale) {
        super(locale);
    }
}
