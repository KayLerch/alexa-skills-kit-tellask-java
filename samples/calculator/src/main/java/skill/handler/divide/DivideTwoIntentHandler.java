package skill.handler.divide;

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

@AlexaIntentListener(customIntents = "Divide")
public class DivideTwoIntentHandler implements AlexaIntentHandler {
    @Override
    public boolean verify(final AlexaInput input) {

        return input.hasSlotIsNumber("b") && input.hasSlotIsNumber("a");
    }

    @Override
    public AlexaOutput handleRequest(final AlexaInput input) throws AlexaRequestHandlerException, AlexaStateException {
        // number from slot (already ensured is a number in verfiy
        final Integer a = Integer.valueOf(input.getSlotValue("a"));
        // number from slot (already ensured is a number in verfiy
        final Integer b = Integer.valueOf(input.getSlotValue("b"));

        if (b == 0) {
            throw new AlexaRequestHandlerException("Division by 0 not allowed.", input, "SaySorryOnDivideBy0");
        }

        // create new calculation
        final Calculation calc = new Calculation();
        calc.divide(a, b);

        final SimpleCard formulaCard = new SimpleCard();
        formulaCard.setContent(a + " / " + b + " = " + calc.getResult());

        return AlexaOutput.ask("SayDivideResult")
                .withCard(formulaCard)
                .putSlot(new AlexaOutputSlot("a", a).formatAs(AlexaOutputFormat.NUMBER))
                .putSlot(new AlexaOutputSlot("b", b).formatAs(AlexaOutputFormat.NUMBER))
                .putState(calc)
                .withReprompt(true)
                .build();
    }

    @Override
    public AlexaOutput handleError(final AlexaRequestHandlerException exception) {
        final String errorIntent = exception.getErrorIntent();
        return AlexaOutput.tell(errorIntent != null ? errorIntent : "SaySorry").build();
    }
}
