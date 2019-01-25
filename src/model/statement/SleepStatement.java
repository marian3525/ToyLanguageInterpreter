package model.statement;

import exceptions.SyntaxException;
import exceptions.UndefinedOperationException;
import exceptions.UndefinedVariableException;
import model.programState.ProgramState;

import java.io.IOException;

public class SleepStatement extends AbstractStatement {

    private int time;

    public SleepStatement(int time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "sleep(" + time + ")";
    }

    @Override
    public ProgramState execute(ProgramState programState) throws UndefinedOperationException, UndefinedVariableException, IOException, SyntaxException {
        if(time != 0) {
            programState.getExecutionStack().push(new SleepStatement(time-1));
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
