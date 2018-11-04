package model.function;

import exceptions.SyntaxException;
import exceptions.UndefinedOperationException;
import exceptions.UndefinedVariableException;
import model.expression.ConstantExpression;
import model.programState.ProgramState;
import model.statement.AbstractStatement;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;

public class Function extends AbstractFunction {
    private Vector<AbstractStatement> statements;           // statements which build the function body
    private Vector<ConstantExpression> arguments;  //the arguments when the function is called
    private Map<String, Integer> locals;            //the local vars, formal params
    private String functionName;
    private String returnVar;

    /**
     * @param parameters: list of formal arguments as strings
     */
    public Function(String functionName, @NotNull Vector<String> parameters) {
        this.arguments = new Vector<>();
        this.statements = new Vector<>();
        this.locals = new HashMap<>();
        this.functionName = functionName;
        this.returnVar = "";

        for (String var : parameters) {
            //locals store 0 until the function call  when the actual arguments are
            //specified
            locals.put(var, 0);
        }
    }

    /**
     * @param statement: AbstractStatement to be added to the function body
     */
    @Override
    public void addStatement(AbstractStatement statement) {
        statement.setFunction(functionName);
        statements.add(statement);
    }

    /**
     * @param statements: statements that build the function body
     */
    @Override
    public void addStatements(Vector<AbstractStatement> statements) {

        for (AbstractStatement statement : statements) {
            statement.setFunction(functionName);
            this.statements.add(statement);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    Vector<ConstantExpression> getArguments() {
        return (Vector<ConstantExpression>) arguments.clone();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Vector<AbstractStatement> getStatements() {
        return (Vector<AbstractStatement>) statements.clone();
    }

    /**
     * Load the program statements into the execution stack
     *
     * @param state: the current program state
     * @return the modified state
     */
    @Override
    public ProgramState load(ProgramState state) {
        return state;
    }

    @Override
    public ProgramState execute(ProgramState programState) throws UndefinedOperationException, UndefinedVariableException, IOException, SyntaxException {
        Stack<AbstractStatement> stack = programState.getExecutionStack();
        //while the stack is not empty and the top statement is from this function, keep executing
        programState.setFunctionFinished(false);
        while (!stack.isEmpty() && stack.peek().getFunction().equals(functionName) && !programState.getFunctionFinished()) {
            stack.peek().execute(programState);
        }
        return programState;
    }

    @Override
    public String getFunction() {
        return functionName;
    }

    @Override
    public void setFunction(String functionName) {

    }

    @Override
    public String toString() {
        return null;
    }

    public void setReturnVar(String returnVar) {
        this.returnVar = returnVar;
    }
}
