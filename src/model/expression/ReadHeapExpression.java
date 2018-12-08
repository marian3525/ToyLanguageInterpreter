package model.expression;

import exceptions.SyntaxException;
import model.interfaces.HeapInterface;
import org.intellij.lang.annotations.RegExp;

import java.util.Map;
import java.util.regex.Pattern;

public class ReadHeapExpression extends AbstractExpression {
    //([a-zA-Z_]+[a-zA-Z0-9_]*$)
    @RegExp
    public static final String readHeapExpressionRegex = "readHeap\\((([0]|([1-9]\\d*))|([a-zA-Z_]+[a-zA-Z0-9_]*))\\)";
    private String key;

    public ReadHeapExpression(String varName) {
        this.key = varName;
    }

    @Override
    public String toString() {
        return "readHeap(" + key + ")";
    }

    @Override
    public int evaluate(Map<String, Integer> symbols, HeapInterface heap) throws SyntaxException {
        if (VariableExpression.matchesString(key))
            return heap.get(symbols.get(key));
        else if (ConstantExpression.matchesString(key)) {
            return Integer.parseInt(key);
        } else {
            throw new SyntaxException("Syntax exception:" + key + " is not a variable or constant value");
        }
    }

    public static boolean matchesString(String expressionStr) {
        return Pattern.matches(readHeapExpressionRegex, expressionStr);
    }
}
