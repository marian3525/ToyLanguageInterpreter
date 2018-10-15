package model.expression;

import java.util.Map;

public class ConstantExpression implements Expression {
    private int value;

    public ConstantExpression(int value) {
        this.value = value;
    }
    @Override
    public int evaluate(Map<String, Integer> symbols) {
        return value;
    }
}
