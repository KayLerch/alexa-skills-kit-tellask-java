package io.klerch.alexa.tellask.model;

import io.klerch.alexa.state.model.AlexaStateIgnore;
import io.klerch.alexa.state.model.AlexaStateModel;
import io.klerch.alexa.state.utils.AlexaStateException;
import io.klerch.alexa.tellask.schema.AlexaSlotSave;
import org.apache.log4j.Logger;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

public abstract class AlexaIntentModel extends AlexaStateModel {
    @AlexaStateIgnore
    private final Logger log = Logger.getLogger(AlexaIntentModel.class);

    private Function<Field, AlexaOutputSlot> makeASlot = (final Field field) -> {
        field.setAccessible(true);
        final AlexaSlotSave slotSave = field.getAnnotation(AlexaSlotSave.class);
        try {
            return new AlexaOutputSlot(slotSave.SlotName(), get(field)).formatAs(slotSave.FormatAs());
        } catch (AlexaStateException e) {
            log.error(e);
            return null;
        }
    };

    public boolean hasOutputSlot() {
        return getSlotSavedFields().findFirst().isPresent();
    }

    public boolean hasOutputSlot(final String slotName) {
        return getSlotSavedField(slotName).isPresent();
    }

    public Optional<AlexaOutputSlot> getOutputSlot(final String slotName) {
        return getSlotSavedField(slotName).map(makeASlot);
    }

    private Stream<Field> getSlotSavedFields() {
        return Arrays.stream(this.getClass().getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(AlexaSlotSave.class));
    }

    private Optional<Field> getSlotSavedField(final String slotName) {
        return getSlotSavedFields()
                .filter(field -> field.getAnnotation(AlexaSlotSave.class).SlotName().equals(slotName))
                .findFirst();
    }
}
