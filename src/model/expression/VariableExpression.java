package model.expression;

import exceptions.UndefinedVariableException;
import model.interfaces.HeapInterface;
import org.intellij.lang.annotations.RegExp;

import java.util.Map;
import java.util.regex.Pattern;

public class VariableExpression extends AbstractExpression {
    private String id;
    @RegExp
    public static final String variableRegex = "^([a-zA-Z_]+[a-zA-Z0-9_]*)";

    public VariableExpression(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return id;
    }

    @Override
    public int evaluate(Map<String, Integer> symbols, HeapInterface heap) throws UndefinedVariableException {
        if(symbols.containsKey(id))
            return symbols.get(id);
        else
            throw new UndefinedVariableException("Variable " +  id + " not defined.");
    }

    public static boolean matchesString(String expressionStr) {
        return Pattern.matches(variableRegex, expressionStr);
    }
}
