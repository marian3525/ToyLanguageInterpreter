package model.statement.Lock;

import exceptions.SyntaxException;
import exceptions.UndefinedOperationException;
import exceptions.UndefinedVariableException;
import model.programState.ProgramState;
import model.statement.AbstractStatement;

import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class NewLockStatement extends AbstractStatement {
    private String varName;
    private static Lock lock = new ReentrantLock();
    public NewLockStatement(String varName) {
        this.varName = varName;
    }

    @Override
    public String toString() {
        return "newLock(" + varName + ")";
    }

    @Override
    public ProgramState execute(ProgramState programState) throws UndefinedOperationException, UndefinedVariableException, IOException, SyntaxException {
        lock.lock();

        Integer location = programState.getNewLockAddress();

        programState.getLockTable().put(location, -1);
        programState.getSymbols().put(varName, location);

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
