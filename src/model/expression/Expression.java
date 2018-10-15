package model.expression;

import exceptions.UndefinedOperationException;
import exceptions.UndefinedVariableException;

import java.util.Map;

public interface Expression {
    String toString();
    int evaluate(Map<String, Integer> symbols) throws UndefinedOperationException, UndefinedVariableException;
}
