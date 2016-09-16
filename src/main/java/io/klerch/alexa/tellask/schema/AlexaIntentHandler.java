package io.klerch.alexa.tellask.schema;

import io.klerch.alexa.tellask.model.AlexaOutput;
import io.klerch.alexa.tellask.model.AlexaInput;

public interface AlexaIntentHandler {
    AlexaOutput handleIntent(final AlexaInput input) throws Exception;
    AlexaOutput handleError(final AlexaInput input, final Throwable exception);
}
