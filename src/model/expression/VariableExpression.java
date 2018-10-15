package model.expression;

import exceptions.UndefinedVariableException;
import model.expression.Expression;

import java.util.Map;

public class VariableExpression implements Expression {
    private String id;

    public VariableExpression(String id) {
        this.id = id;
    }

    @Override
    public int evaluate(Map<String, Integer> symbols) throws UndefinedVariableException {
        if(symbols.containsKey(id))
            return symbols.get(id);
        else
            throw new UndefinedVariableException("Variable " +  id + "not defined.");
    }
}
