package skill.handler.launch;

import io.klerch.alexa.tellask.model.AlexaInput;
import io.klerch.alexa.tellask.model.AlexaOutput;
import io.klerch.alexa.tellask.schema.AlexaLaunchHandler;
import io.klerch.alexa.tellask.schema.annotation.AlexaLaunchListener;
import io.klerch.alexa.tellask.util.AlexaRequestHandlerException;

@AlexaLaunchListener
public class LaunchHandler implements AlexaLaunchHandler {
    @Override
    public AlexaOutput handleRequest(AlexaInput alexaInput) {
        return AlexaOutput.ask("SayWelcome").withReprompt(true).build();
    }
    @Override
    public AlexaOutput handleError(final AlexaRequestHandlerException e) {
        return AlexaOutput.tell("SaySorry").build();
    }
}
