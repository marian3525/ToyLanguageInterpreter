package model.statement.Lock;

import exceptions.SyntaxException;
import exceptions.UndefinedOperationException;
import exceptions.UndefinedVariableException;
import model.programState.ProgramState;
import model.statement.AbstractStatement;

import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockStatement extends AbstractStatement {
    private String varName;
    private static Lock lock = new ReentrantLock();

    public LockStatement(String varName) {
        this.varName = varName;
    }

    @Override
    public String toString() {
        return "lock(" + varName + ")";
    }

    @Override
    public ProgramState execute(ProgramState programState) throws UndefinedOperationException, UndefinedVariableException, IOException, SyntaxException {
        lock.lock();
        Integer idx = programState.getSymbols().get(varName);

        if(idx == null) {
            throw new UndefinedVariableException("Variable  " + varName + " not defined");
        }

        Integer lockValue = programState.getLockTable().get(idx);

        if(lockValue == null) {
            throw new UndefinedVariableException("Key " + idx + "not found in the lock table");
        }

        if(lockValue == -1) {
            programState.getLockTable().put(idx, programState.getId());
        }
        else {
            programState.getExecutionStack().push(this);
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
