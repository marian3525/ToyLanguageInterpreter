package model.statement.CountdownLatch;

import exceptions.SyntaxException;
import exceptions.UndefinedOperationException;
import exceptions.UndefinedVariableException;
import model.programState.ProgramState;
import model.statement.AbstractStatement;

import java.io.IOException;

public class AwaitStatement extends AbstractStatement {
    private String varName;

    public AwaitStatement(String varName) {
        this.varName = varName;
    }


    @Override
    public String toString() {
        return "await("+varName+")";
    }

    @Override
    public ProgramState execute(ProgramState programState) throws UndefinedOperationException, UndefinedVariableException, IOException, SyntaxException {
        Integer idx = programState.getSymbols().get(varName);

        if(idx == null) {
            throw new RuntimeException("Variable " + varName + " not defined");
        }
        Integer latchIndex = programState.getLatchTable().get(idx);
        if (latchIndex == null)
            throw new UndefinedVariableException("Could not find " + idx + " key in the latch table");

        if (latchIndex != 0) {
            programState.getExecutionStack().push(this);
        }

        return null;
    }

    @Override
    public String getFunction() {
        return null;
    }

    @Override
    public void setFunction(String functionName) {

    }
}
