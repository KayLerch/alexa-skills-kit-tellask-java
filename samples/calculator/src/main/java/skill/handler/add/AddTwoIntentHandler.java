package skill.handler.add;

import com.amazon.speech.ui.SimpleCard;
import io.klerch.alexa.state.handler.AWSDynamoStateHandler;
import io.klerch.alexa.state.handler.AlexaStateHandler;
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
        // get state handlers for session and dynamoDB of States SDK
        final AlexaStateHandler sessionHandler = input.getSessionStateHandler();
        final AlexaStateHandler dynamoHandler = new AWSDynamoStateHandler(sessionHandler.getSession());

        // try get calculation from session first, if not there read or create in dynamo
        // cause we permanently save the precision a user can set
        final Calculation calc = sessionHandler.readModel(Calculation.class)
                .orElse(dynamoHandler.readModel(Calculation.class)
                        .orElse(dynamoHandler.createModel(Calculation.class)));

        // addend from slot (already ensured is a number in verfiy
        final Integer a = Integer.valueOf(input.getSlotValue("a"));
        // addend from slot (already ensured is a number in verfiy
        final Integer b = Integer.valueOf(input.getSlotValue("b"));

        calc.add(a, b);

        final SimpleCard formulaCard = new SimpleCard();
        formulaCard.setContent(a + " + " + b + " = " + calc.getResult());

        // ensure model is written back to session only (in case it was read out from dynamo)
        // we'd like to avoid unnecessary roundtrips to dynamo at this point cause we'd only
        // change the result which is not saved permanently
        calc.setHandler(sessionHandler);

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
