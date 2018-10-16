package model.expression;

import exceptions.UndefinedOperationException;
import exceptions.UndefinedVariableException;

import java.util.Map;

public class ArithmeticExpression extends Expression{
    private Expression first;
    private Expression second;
    private String op;

    public ArithmeticExpression(Expression first, Expression second, String op) {
        this.first = first;
        this.second = second;
        this.op = op;
    }

    @Override
    public String toString() {
        return (first.toString() + op + second.toString());
    }

    @Override
    public int evaluate(Map<String, Integer> symbols) throws UndefinedOperationException, UndefinedVariableException {
        switch(op) {
            case "+": return first.evaluate(symbols) + second.evaluate(symbols);
            case "-": return first.evaluate(symbols) - second.evaluate(symbols);
            case "/": return first.evaluate(symbols) / second.evaluate(symbols);    //TODO throw ArithmeticException if the second is 0z
            case "*": return first.evaluate(symbols) * second.evaluate(symbols);
        }
        //if it hasn't returned, the op isn't supported, throw an error
        throw new UndefinedOperationException("Unknown operation: " + op);
    }
}
