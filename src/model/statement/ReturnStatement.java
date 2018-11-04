package model.statement;

import exceptions.UndefinedOperationException;
import exceptions.UndefinedVariableException;
import model.expression.AbstractExpression;
import model.programState.ProgramState;

import java.io.IOException;

public class ReturnStatement extends AbstractStatement {

    private String functionName;
    private AbstractExpression value;

    public ReturnStatement(String functionName, AbstractExpression value) {
        this.functionName = functionName;
        this.value = value;
    }

    @Override
    public String toString() {
        return "Return from: " + functionName + " with " + value;
    }

    @Override
    public ProgramState execute(ProgramState programState) throws UndefinedOperationException, UndefinedVariableException, IOException {
        //flag the end of the execution
        programState.setFunctionFinished(true);
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
