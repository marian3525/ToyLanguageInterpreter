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
        String firstStr;
        String secondStr;

        if(first instanceof ArithmeticExpression)
            //put the expression in ()
            firstStr = "(" + first.toString() + ")";
        else    //no () for constants or variables
            firstStr = first.toString();

        if(second instanceof ArithmeticExpression)
            secondStr = "(" + second.toString() + ")";
        else
            secondStr = second.toString();

        return firstStr + op + secondStr;
    }

    @Override
    public int evaluate(Map<String, Integer> symbols) throws UndefinedOperationException, UndefinedVariableException {
        switch(op) {
            case "+": return first.evaluate(symbols) + second.evaluate(symbols);
            case "-": return first.evaluate(symbols) - second.evaluate(symbols);
            case "/":
                return first.evaluate(symbols) / second.evaluate(symbols);
            case "*": return first.evaluate(symbols) * second.evaluate(symbols);
        }
        //if it hasn't returned, the op isn't supported, throw an error
        throw new UndefinedOperationException("Unknown operation: " + op);
    }
}
