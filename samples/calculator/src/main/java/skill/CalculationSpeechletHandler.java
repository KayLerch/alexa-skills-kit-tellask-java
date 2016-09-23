package skill;

import io.klerch.alexa.tellask.model.wrapper.AlexaRequestStreamHandler;
import io.klerch.alexa.tellask.schema.UtteranceReader;
import io.klerch.alexa.tellask.schema.annotation.AlexaApplication;
import io.klerch.alexa.tellask.util.resource.ResourceUtteranceReader;
import io.klerch.alexa.tellask.util.resource.S3UtteranceReader;

@AlexaApplication(applicationIds = "amzn1.ask.skill.7617...")
public class CalculationSpeechletHandler extends AlexaRequestStreamHandler {
    @Override
    public UtteranceReader getUtteranceReader() {
        return new ResourceUtteranceReader("out/");
    }
}
