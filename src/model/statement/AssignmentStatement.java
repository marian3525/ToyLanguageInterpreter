package model.statement;

import exceptions.UndefinedOperationException;
import exceptions.UndefinedVariableException;
import model.expression.Expression;
import model.programState.ProgramState;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Stack;

public class AssignmentStatement implements Statement {
    String id;
    Expression expression;

    public AssignmentStatement(String id, Expression expression) {
        this.id = id;
        this.expression = expression;
    }

    @Override
    public String toString() {
        return id + " = " + expression.toString();
    }

    @Override
    public ProgramState execute(@NotNull ProgramState programState) throws UndefinedOperationException, UndefinedVariableException {

        Stack<Statement> stack = programState.getExecutionStack();
        Map<String, Integer> symbols = programState.getSymbols();
        int expressionValue = expression.evaluate(symbols);

        //if the key doesn't exist, create a new one with the given value
        //if the key already exists, update the pair
        symbols.put(id, expressionValue);

        return programState;
    }
}
