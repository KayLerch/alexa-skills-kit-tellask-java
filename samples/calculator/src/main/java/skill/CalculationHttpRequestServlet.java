package skill;

import io.klerch.alexa.tellask.model.wrapper.AlexaSpeechletServlet;
import io.klerch.alexa.tellask.schema.UtteranceReader;
import io.klerch.alexa.tellask.schema.annotation.AlexaApplication;
import io.klerch.alexa.tellask.util.resource.ResourceUtteranceReader;

@AlexaApplication(applicationIds = "amzn1.ask.skill.7617...")
public class CalculationHttpRequestServlet extends AlexaSpeechletServlet {
    @Override
    public UtteranceReader getUtteranceReader() {
        return new ResourceUtteranceReader("out/");
    }
}
