package model.expression;

import model.interfaces.HeapInterface;
import org.intellij.lang.annotations.RegExp;

import java.util.Map;

public class ConstantExpression extends AbstractExpression {
    private int value;
    @RegExp
    public static final String constantRegex = "^[-+]?(([0])|([1-9]\\d*$))";
    public ConstantExpression(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public int evaluate(Map<String, Integer> symbols, HeapInterface heap) {
        return value;
    }
}
