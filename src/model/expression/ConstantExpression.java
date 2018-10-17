package model.expression;

import java.util.Map;

public class ConstantExpression extends Expression {
    private int value;
    public static final String constantRegex = "^[-+]?([0]{1})|([1-9]\\d*$)";
    public ConstantExpression(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public int evaluate(Map<String, Integer> symbols) {
        return value;
    }
}
