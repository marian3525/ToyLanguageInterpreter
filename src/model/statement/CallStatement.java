package model.statement;

import exceptions.SyntaxException;
import exceptions.UndefinedOperationException;
import exceptions.UndefinedVariableException;
import model.expression.AbstractExpression;
import model.function.Function;
import model.programState.ProgramState;

import java.io.IOException;
import java.util.Collections;
import java.util.Stack;
import java.util.Vector;

public class CallStatement extends AbstractStatement {
    private String functionName;
    private Vector<AbstractExpression> args;
    private String returnVar;

    public CallStatement(String functionName, Vector<AbstractExpression> arguments) {
        this.functionName = functionName;
        this.args = arguments;
        returnVar = "";
    }

    public CallStatement(String functionName, Vector<AbstractExpression> arguments, String returnVarName) {
        this.functionName = functionName;
        this.args = arguments;
        returnVar = returnVarName;
    }

    /**
     * Call syntax: call <functionName>(arg1, arg2..., argn)
     *
     * @param input: statement string
     * @return the CallStatement built from the string
     */
    public static CallStatement getCallStatementFromString(String input) throws SyntaxException {
        //remove the 'call' and extract the function name and params
        String functionName;
        String[] args;
        CallStatement statement = null;
        input = input.replace("call ", "");
        functionName = input.split("\\(")[0];
        args = input.split("\\(")[1].replace(")", "").replace(" ", "")
                .split(",");
        Vector<AbstractStatement> params = new Vector<>();
        for (String arg : args) {
            statement = (CallStatement) getStatementFromString(arg);
        }
        return statement;
    }

    @Override
    public String toString() {
        return "Call: " + functionName + args.toString();
    }

    /**
     * Load the statements of the function on the stack and then call execute on the function
     * Call setReturn(varName) if the return value of the function need to be stored
     * After the function returns, clear the stack of the remaining statements from the prev. called function
     * This situation will occur when the function returns before executing all statements in its body
     *
     * @param programState the current program state
     * @return the modified program state
     * @throws UndefinedOperationException
     * @throws UndefinedVariableException
     * @throws IOException
     */
    @Override
    public ProgramState execute(ProgramState programState) throws UndefinedOperationException, UndefinedVariableException, IOException, SyntaxException {

        Function f = (Function) programState.getFunctions().get(functionName);
        Stack<AbstractStatement> stack = programState.getExecutionStack();
        //load into the execution stack
        Vector<AbstractStatement> statements = f.getStatements();
        //prepare for the stack push in reverse order
        Collections.reverse(statements);

        for (AbstractStatement statement : statements)
            programState.getExecutionStack().push(statement);

        //set the return var name, if any
        if (returnVar.length() > 0) {
            if (programState.getSymbols().get(returnVar) != null)
                f.setReturnVar(returnVar);
        }

        //call the function and clear the stack of its remaining statements after returning, if any
        f.execute(programState);

        while (!stack.isEmpty() && stack.peek().getFunction().equals(functionName)) {
            stack.pop();
        }

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
