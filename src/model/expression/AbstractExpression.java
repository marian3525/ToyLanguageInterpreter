package model.expression;

import exceptions.SyntaxException;
import exceptions.UndefinedOperationException;
import exceptions.UndefinedVariableException;
import model.interfaces.HeapInterface;

import java.util.Map;

public abstract class AbstractExpression {

    public abstract String toString();

    public abstract int evaluate(Map<String, Integer> symbols, HeapInterface heap) throws UndefinedOperationException, UndefinedVariableException, SyntaxException;
}
