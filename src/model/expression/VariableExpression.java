package model.expression;

import exceptions.UndefinedVariableException;
import model.expression.Expression;

import java.util.Map;

public class VariableExpression extends Expression {
    private String id;
    public static final String variableRegex = "^([a-zA-Z_]+[a-zA-Z0-9_]*)";

    public VariableExpression(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return id;
    }

    @Override
    public int evaluate(Map<String, Integer> symbols) throws UndefinedVariableException {
        if(symbols.containsKey(id))
            return symbols.get(id);
        else
            throw new UndefinedVariableException("Variable " +  id + " not defined.");
    }
}
