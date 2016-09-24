package skill.model;

import io.klerch.alexa.state.model.AlexaScope;
import io.klerch.alexa.state.model.AlexaStateIgnore;
import io.klerch.alexa.state.model.AlexaStateModel;
import io.klerch.alexa.state.model.AlexaStateSave;
import io.klerch.alexa.tellask.schema.annotation.AlexaSlotSave;
import io.klerch.alexa.tellask.schema.type.AlexaOutputFormat;
import org.apache.commons.lang3.Validate;

import java.text.DecimalFormat;

public class Calculation extends AlexaStateModel {
    @AlexaStateSave(Scope = AlexaScope.USER)
    @AlexaSlotSave(slotName = "precision", formatAs = AlexaOutputFormat.NUMBER)
    private int precision = 1;

    @AlexaSlotSave(slotName = "result", formatAs = AlexaOutputFormat.NUMBER)
    @AlexaStateSave
    private double result = 0;

    public Calculation() {
    }

    public void setPrecision(final int precision) {
        this.precision = Math.abs(precision);
    }

    public void add(final Integer a) {
        result = round(result + a);
    }

    public void add(final Integer a, final Integer b) {
        result = round(a + b);
    }

    public void subtract(final Integer a) {
        result = round(result - a);
    }

    public void subtract(final Integer a, final Integer b) {
        result = round(a - b);
    }

    public void multiply(final Integer a) {
        result = round(result * a);
    }

    public void multiply(final Integer a, final Integer b) {
        result = round(a * b);
    }

    public void divide(final Integer a) {
        result = round(result / a);
    }

    public void divide(final Integer a, final Integer b) {
        result = round(a / b);
    }

    public double getResult() {
        return result;
    }

    private double round (final double value) {
        if (value % 1 == 0) return value;
        final int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }
}
