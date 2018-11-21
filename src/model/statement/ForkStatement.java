package model.statement;

import model.programState.ProgramState;

public class ForkStatement extends AbstractStatement {
    //.clone on the symtable to pass to the new thread
    @Override
    public String toString() {
        return null;
    }

    @Override
    public ProgramState execute(ProgramState programState) {
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
