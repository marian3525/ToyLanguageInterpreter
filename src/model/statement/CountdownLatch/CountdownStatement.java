package model.statement.CountdownLatch;

import exceptions.SyntaxException;
import exceptions.UndefinedOperationException;
import exceptions.UndefinedVariableException;
import model.programState.ProgramState;
import model.statement.AbstractStatement;

import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CountdownStatement extends AbstractStatement {
    private static Lock lock = new ReentrantLock();
    private String varName;

    public CountdownStatement(String varName) {
        this.varName = varName;
    }

    @Override
    public String toString() {
        return "countDown(" + varName + ")";
    }

    @Override
    public ProgramState execute(ProgramState programState) throws UndefinedOperationException, UndefinedVariableException, IOException, SyntaxException {
        lock.lock();

        Integer idx = programState.getSymbols().get(varName);

        if(idx == null) {
            throw new UndefinedVariableException("Variable " + varName + " does not defined");
        }
        Integer latchValue = programState.getLatchTable().get(idx);

        if(latchValue == null) {
            throw new UndefinedVariableException("No such variable. " + idx + " is not a key in the latch table");
        }

        // push onto the stack?

        if(latchValue > 0) {
            programState.getLatchTable().put(idx, latchValue - 1);
        }

        lock.unlock();

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
