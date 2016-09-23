package skill.handler;

import io.klerch.alexa.tellask.model.AlexaInput;
import io.klerch.alexa.tellask.model.AlexaIntentModel;
import io.klerch.alexa.tellask.model.AlexaOutput;
import io.klerch.alexa.tellask.schema.AlexaIntentHandler;
import io.klerch.alexa.tellask.schema.annotation.AlexaIntentListener;
import io.klerch.alexa.tellask.util.AlexaRequestHandlerException;
import org.apache.log4j.Logger;

import static io.klerch.alexa.tellask.schema.type.AlexaIntentType.INTENT_CANCEL;
import static io.klerch.alexa.tellask.schema.type.AlexaIntentType.INTENT_STOP;

@AlexaIntentListener(builtInIntents = {INTENT_CANCEL, INTENT_STOP })
public class InterruptIntentHandler implements AlexaIntentHandler {
    private static final Logger LOG = Logger.getLogger(AlexaIntentModel.class);

    @Override
    public boolean verify(final AlexaInput input) {
        return true;
    }

    @Override
    public AlexaOutput handleRequest(final AlexaInput input) {
        return AlexaOutput.tell("SayGoodBye").build();
    }

    @Override
    public AlexaOutput handleError(final AlexaRequestHandlerException exception) {
        return AlexaOutput.tell("SaySorry").build();
    }
}
