package model.expression;

import exceptions.UndefinedOperationException;
import exceptions.UndefinedVariableException;
import model.interfaces.HeapInterface;

import java.util.Map;

public class NotExpression extends AbstractExpression {

    private AbstractExpression expression;

    public NotExpression(AbstractExpression expression) {
        this.expression = expression;
    }

    @Override
    public String toString() {
        return "NOT(" + expression.toString();
    }

    @Override
    public int evaluate(Map<String, Integer> symbols, HeapInterface heap) throws UndefinedOperationException, UndefinedVariableException {
        return (expression.evaluate(symbols, heap) != 0) ? 0 : 1;
    }
}