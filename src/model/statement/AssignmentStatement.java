package model.statement;

import exceptions.UndefinedOperationException;
import exceptions.UndefinedVariableException;
import model.expression.AbstractExpression;
import model.programState.ProgramState;
import org.intellij.lang.annotations.RegExp;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Map;
import java.util.Stack;


public class AssignmentStatement extends AbstractStatement {
    private String id;
    private AbstractExpression expression;
    private String functionName;
    @RegExp
    public static final String assignmentRegex= "^[a-zA-Z_]+[a-zA-Z0-9_]*=[+-]?([0]{1}$|[1-9][0-9]*$)";

    public AssignmentStatement(String id, AbstractExpression expression) {
        this.id = id;
        this.expression = expression;
        this.functionName = "main";
    }

    public AssignmentStatement(String id, AbstractExpression expression, String functionName) {
        this.id = id;
        this.expression = expression;
        this.functionName = functionName;
    }

    @Override
    public String toString() {
        return id + " = " + expression.toString();
    }

    @Override
    public ProgramState execute(@NotNull ProgramState programState) throws UndefinedOperationException, UndefinedVariableException, IOException {

        Stack<AbstractStatement> stack = programState.getExecutionStack();
        Map<String, Integer> symbols = programState.getSymbols();
        int expressionValue = expression.evaluate(symbols);

        //if the key doesn't exist, create a new one with the given value
        //if the key already exists, update the pair
        symbols.put(id, expressionValue);

        return programState;
    }

    @Override
    public String getFunction() {
        return functionName;
    }

    @Override
    public void setFunction(String functionName) {
        this.functionName = functionName;
    }
}
