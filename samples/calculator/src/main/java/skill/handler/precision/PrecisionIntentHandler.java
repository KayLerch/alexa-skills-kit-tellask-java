package skill.handler.precision;

import io.klerch.alexa.state.handler.AWSDynamoStateHandler;
import io.klerch.alexa.state.handler.AlexaStateHandler;
import io.klerch.alexa.state.utils.AlexaStateException;
import io.klerch.alexa.tellask.model.AlexaInput;
import io.klerch.alexa.tellask.model.AlexaOutput;
import io.klerch.alexa.tellask.schema.AlexaIntentHandler;
import io.klerch.alexa.tellask.schema.annotation.AlexaIntentListener;
import io.klerch.alexa.tellask.util.AlexaRequestHandlerException;
import skill.model.Calculation;

@AlexaIntentListener(customIntents = "Precision")
public class PrecisionIntentHandler implements AlexaIntentHandler {
    @Override
    public boolean verify(AlexaInput input) {
        return input.hasSlotIsNumber("decimalplaces");
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

        // set precision
        calc.setPrecision(Integer.valueOf(input.getSlotValue("decimalplaces")));

        // ensure this value is written back to dynamo (in case calculation object was read from session)
        calc.setHandler(dynamoHandler);

        return AlexaOutput.ask("SayNewPrecision")
                .putState(calc)
                .build();
    }

    @Override
    public AlexaOutput handleError(AlexaRequestHandlerException exception) {
        return AlexaOutput.tell("SaySorry").build();
    }
}
