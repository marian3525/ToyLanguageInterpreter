package model.expression;

import exceptions.UndefinedOperationException;
import exceptions.UndefinedVariableException;
import model.interfaces.HeapInterface;

import java.util.Map;

public class BooleanExpression extends AbstractExpression {
    private AbstractExpression first;
    private AbstractExpression second;
    private String operator;

    public BooleanExpression(AbstractExpression first, AbstractExpression second, String operator) {
        this.first = first;
        this.second = second;
        this.operator = operator;
    }

    @Override
    public String toString() {
        return first.toString() + operator + second.toString();
    }

    @Override
    public int evaluate(Map<String, Integer> symbols, HeapInterface heap) throws UndefinedOperationException, UndefinedVariableException {

        switch (operator) {
            case "<":
                //r = () -> first.evaluate(symbols, heap) < second.evaluate(symbols, heap) ? 1 : 0;
                return first.evaluate(symbols, heap) < second.evaluate(symbols, heap) ? 1 : 0;
            case "<=":
                return first.evaluate(symbols, heap) <= second.evaluate(symbols, heap) ? 1 : 0;
            case "==":
                return first.evaluate(symbols, heap) == second.evaluate(symbols, heap) ? 1 : 0;
            case "!=":
                return first.evaluate(symbols, heap) != second.evaluate(symbols, heap) ? 1 : 0;
            case ">":
                return first.evaluate(symbols, heap) > second.evaluate(symbols, heap) ? 1 : 0;
            case ">=":
                return first.evaluate(symbols, heap) >= second.evaluate(symbols, heap) ? 1 : 0;
            case "AND":
                return first.evaluate(symbols, heap) != 0 && second.evaluate(symbols, heap) != 0 ? 1 : 0;
            case "OR":
                return first.evaluate(symbols, heap) != 0 || second.evaluate(symbols, heap) != 0 ? 1 : 0;
        }
        return 0;
    }
}
