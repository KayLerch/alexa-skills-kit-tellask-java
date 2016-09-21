/**
 * Created by Kay Lerch (https://twitter.com/KayLerch)
 *
 * Contribute to https://github.com/KayLerch/alexa-skills-kit-tellask-java
 *
 * Attached license applies.
 * This source is licensed under GNU GENERAL PUBLIC LICENSE Version 3 as of 29 June 2007
 */
package io.klerch.alexa.tellask.model;

import io.klerch.alexa.state.handler.AlexaStateHandler;
import io.klerch.alexa.state.model.AlexaStateIgnore;
import io.klerch.alexa.state.model.AlexaStateModel;
import io.klerch.alexa.state.utils.AlexaStateException;
import io.klerch.alexa.tellask.schema.annotation.AlexaSlotSave;
import org.apache.log4j.Logger;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Is a wrapper to the AlexaStateModel to make them compatible with the Tellask SDK.
 * What sits on top of the state models is the ability to tag fields as AlexaSlotSave resulting
 * in their values being filled in the corresponding utterance placeholders.
 */
public class AlexaIntentModel {
    @AlexaStateIgnore
    private static final Logger LOG = Logger.getLogger(AlexaIntentModel.class);
    @AlexaStateIgnore
    private AlexaStateModel model;

    private Function<Field, AlexaOutputSlot> makeASlot = (final Field field) -> {
        field.setAccessible(true);
        final AlexaSlotSave slotSave = field.getAnnotation(AlexaSlotSave.class);
        try {
            return new AlexaOutputSlot(slotSave.slotName(), model.get(field)).formatAs(slotSave.formatAs());
        } catch (AlexaStateException e) {
            LOG.error(e);
            return null;
        }
    };

    /**
     * Turns a state model into an intent model. You likely need this when reading
     * model from an AlexaStateHandler and you want to hand this model over to an
     * AlexaOutput object in order to let it's updated state save automatically and
     * to have AlexaSlotSave-tagged fields go into utterance-placeholders
     * @param model state model
     */
    public AlexaIntentModel (final AlexaStateModel model) {
        this.model = model;
    }

    /**
     * Gets the underlying state model
     * @return the underlying state model
     */
    public AlexaStateModel getModel() {
        return this.model;
    }

    /**
     * Gets the state handler of the underlying state model.
     * @return the state handler of the underlying state model
     */
    public AlexaStateHandler getHandler() {
        return this.model.getHandler();
    }

    /**
     * Saves state of the underlying state model using the associated state handler.
     * However, if you hand this model over to an AlexaOutput saving the updated state
     * is taken care of by the speechlet handler.
     * @throws AlexaStateException throws on error saving state or no handler associated to the state model
     */
    public void saveState() throws AlexaStateException {
        this.model.saveState();
    }

    /**
     * True, if the model has at least one field tagged as AlexaSlotSave
     * @return True, if the model has at least one field tagged as AlexaSlotSave
     */
    public boolean hasOutputSlot() {
        return getSlotSavedFields().findFirst().isPresent();
    }

    /**
     * True, if the model has a field with given slot-name
     * @param slotName the slot-name to look after
     * @return True, if the model has a field with given slot-name
     */
    public boolean hasOutputSlot(final String slotName) {
        return getSlotSavedField(slotName).isPresent();
    }

    /**
     * Returns an AlexaOutputSlot object with all the information coming from one of its fields
     * tagged with AlexaSlotSave which got the given slot-name
     * @param slotName the slot-name to look after
     * @return an AlexaOutputSlot object with all the information coming from one of its fields
     * tagged with AlexaSlotSave which got the given slot-name
     */
    public Optional<AlexaOutputSlot> getOutputSlot(final String slotName) {
        return getSlotSavedField(slotName).map(makeASlot);
    }

    private Stream<Field> getSlotSavedFields() {
        return Arrays.stream(model.getClass().getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(AlexaSlotSave.class));
    }

    private Optional<Field> getSlotSavedField(final String slotName) {
        return getSlotSavedFields()
                .filter(field -> field.getAnnotation(AlexaSlotSave.class).slotName().equals(slotName))
                .findFirst();
    }
}
