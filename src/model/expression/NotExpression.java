package model.expression;

import exceptions.SyntaxException;
import exceptions.UndefinedOperationException;
import exceptions.UndefinedVariableException;
import model.interfaces.HeapInterface;
import org.intellij.lang.annotations.RegExp;

import java.util.Map;
import java.util.regex.Pattern;

public class NotExpression extends AbstractExpression {

    @RegExp
    public static final String notRegex = "not\\(.*\\)$";
    private AbstractExpression expression;

    public NotExpression(AbstractExpression expression) {
        this.expression = expression;
    }

    @Override
    public String toString() {
        return "NOT(" + expression.toString();
    }

    @Override
    public int evaluate(Map<String, Integer> symbols, HeapInterface heap) throws UndefinedOperationException, UndefinedVariableException, SyntaxException {
        return (expression.evaluate(symbols, heap) != 0) ? 0 : 1;
    }

    public static boolean matchesString(String expressionStr) {
        return Pattern.matches(notRegex, expressionStr);
    }
}
