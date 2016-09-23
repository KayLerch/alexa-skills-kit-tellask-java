package skill.handler.add;

import com.amazon.speech.ui.SimpleCard;
import io.klerch.alexa.state.utils.AlexaStateException;
import io.klerch.alexa.tellask.model.AlexaInput;
import io.klerch.alexa.tellask.model.AlexaIntentModel;
import io.klerch.alexa.tellask.model.AlexaOutput;
import io.klerch.alexa.tellask.model.AlexaOutputSlot;
import io.klerch.alexa.tellask.schema.AlexaIntentHandler;
import io.klerch.alexa.tellask.schema.annotation.AlexaIntentListener;
import io.klerch.alexa.tellask.schema.type.AlexaOutputFormat;
import io.klerch.alexa.tellask.util.AlexaRequestHandlerException;
import org.apache.log4j.Logger;
import skill.model.Calculation;

@AlexaIntentListener(customIntents = "Add")
public class AddTwoIntentHandler implements AlexaIntentHandler {
    @Override
    public boolean verify(final AlexaInput input) {

        return input.hasSlotIsNumber("b") && input.hasSlotIsNumber("a");
    }

    @Override
    public AlexaOutput handleRequest(final AlexaInput input) throws AlexaRequestHandlerException, AlexaStateException {
        // addend from slot (already ensured is a number in verfiy
        final Integer a = Integer.valueOf(input.getSlotValue("a"));
        // addend from slot (already ensured is a number in verfiy
        final Integer b = Integer.valueOf(input.getSlotValue("b"));

        // create new calculation
        final Calculation calc = new Calculation();
        calc.add(a, b);

        final SimpleCard formulaCard = new SimpleCard();
        formulaCard.setContent(a + " + " + b + " = " + calc.getResult());

        return AlexaOutput.ask("SayAddResult")
                .withCard(formulaCard)
                .putSlot(new AlexaOutputSlot("a", a).formatAs(AlexaOutputFormat.NUMBER))
                .putSlot(new AlexaOutputSlot("b", b).formatAs(AlexaOutputFormat.NUMBER))
                .putState(calc)
                .withReprompt(true)
                .build();
    }

    @Override
    public AlexaOutput handleError(final AlexaRequestHandlerException exception) {
        return AlexaOutput.tell("SaySorry").build();
    }
}
