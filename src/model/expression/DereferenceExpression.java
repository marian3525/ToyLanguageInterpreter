package model.expression;

import exceptions.SyntaxException;
import exceptions.UndefinedOperationException;
import exceptions.UndefinedVariableException;
import model.interfaces.HeapInterface;
import org.intellij.lang.annotations.RegExp;

import java.util.Map;
import java.util.regex.Pattern;

public class DereferenceExpression extends AbstractExpression {
    private String key;
    @RegExp
    private static final String dereferenceExpressionRegex = "^\\*([a-zA-Z_]+[a-zA-Z0-9_]*)$";

    public DereferenceExpression(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "*" + key;
    }

    @Override
    public int evaluate(Map<String, Integer> symbols, HeapInterface heap) throws UndefinedOperationException, UndefinedVariableException, SyntaxException {
        if (!VariableExpression.matchesString(key)) {
            // if it isn't a variable
            throw new SyntaxException(key + " is not a variable. Only variables can be dereferenced");
        } else if (symbols.get(key) == null) {
            // if the variable to be dereferenced doesn't exist
            throw new UndefinedVariableException("Variable " + key + " not defined");
        } else if (!heap.getContent().containsKey(symbols.get(key))) {
            // if the variable exists and it doesn't represent a valid address
            throw new UndefinedVariableException("Attempted to dereference: " + key + " which does not represent a valid heap address");
        } else {
            //if variable which exists, points to a valid address, return the value stored at the said address in the heap
            return heap.get(symbols.get(key));
        }
    }

    public static boolean matchesString(String expression) {
        return Pattern.matches(dereferenceExpressionRegex, expression);
    }
}
