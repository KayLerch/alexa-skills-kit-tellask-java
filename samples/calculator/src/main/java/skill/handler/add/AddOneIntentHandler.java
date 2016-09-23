package skill.handler.add;

import com.amazon.speech.ui.SimpleCard;
import io.klerch.alexa.state.utils.AlexaStateException;
import io.klerch.alexa.tellask.model.AlexaInput;
import io.klerch.alexa.tellask.model.AlexaOutput;
import io.klerch.alexa.tellask.model.AlexaOutputSlot;
import io.klerch.alexa.tellask.schema.AlexaIntentHandler;
import io.klerch.alexa.tellask.schema.annotation.AlexaIntentListener;
import io.klerch.alexa.tellask.schema.type.AlexaOutputFormat;
import io.klerch.alexa.tellask.util.AlexaRequestHandlerException;
import skill.model.Calculation;

@AlexaIntentListener(customIntents = "Add")
public class AddOneIntentHandler implements AlexaIntentHandler {
    @Override
    public boolean verify(final AlexaInput input) {
        return !input.hasSlotIsNumber("b") && input.hasSlotIsNumber("a");
    }

    @Override
    public AlexaOutput handleRequest(final AlexaInput input) throws AlexaRequestHandlerException, AlexaStateException {
        // addend from slot (already ensured is a number in verfiy
        final Integer a = Integer.valueOf(input.getSlotValue("a"));
        // get or create calculation from session object
        final Calculation calc = input.getSessionStateHandler().readModel(Calculation.class)
                .orElse(input.getSessionStateHandler().createModel(Calculation.class));

        // former result will be the other addend
        final double lastResult = calc.getResult();
        // add number to result
        calc.add(a);

        final SimpleCard formulaCard = new SimpleCard();
        formulaCard.setContent(lastResult + " + " + a + " = " + calc.getResult());

        return AlexaOutput.ask("SayAddResult")
                .withCard(formulaCard)
                .putSlot(new AlexaOutputSlot("a", lastResult).formatAs(AlexaOutputFormat.NUMBER))
                .putSlot(new AlexaOutputSlot("b", a).formatAs(AlexaOutputFormat.NUMBER))
                .putState(calc)
                .withReprompt(true)
                .build();
    }

    @Override
    public AlexaOutput handleError(final AlexaRequestHandlerException exception) {
        return AlexaOutput.tell("SaySorry").build();
    }
}