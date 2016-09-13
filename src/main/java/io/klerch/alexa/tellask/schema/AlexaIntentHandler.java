package io.klerch.alexa.tellask.schema;

import io.klerch.alexa.state.utils.AlexaStateException;
import io.klerch.alexa.tellask.model.AlexaOutput;
import io.klerch.alexa.tellask.model.AlexaInput;

public interface AlexaIntentHandler {
    AlexaOutput handleIntent(final AlexaInput request) throws AlexaStateException;
}
