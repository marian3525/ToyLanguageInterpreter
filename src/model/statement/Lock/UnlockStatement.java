package model.statement.Lock;

import exceptions.SyntaxException;
import exceptions.UndefinedOperationException;
import exceptions.UndefinedVariableException;
import model.programState.ProgramState;
import model.statement.AbstractStatement;

import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class UnlockStatement extends AbstractStatement {
    private String varName;
    private static Lock lock = new ReentrantLock();

    public UnlockStatement(String varName) {
        this.varName = varName;
    }
    @Override
    public String toString() {
        return "unlock(" + varName + ")";
    }

    @Override
    public ProgramState execute(ProgramState programState) throws UndefinedOperationException, UndefinedVariableException, IOException, SyntaxException {
        lock.lock();

        Integer idx = programState.getSymbols().get(varName);

        if(idx == null) {
            throw new UndefinedVariableException(varName + " not defined");
        }

        Integer lockValue = programState.getLockTable().get(idx);
        if(lockValue == null) {
            throw new UndefinedVariableException(" Key " + idx + " not in lock table");
        }

        if(lockValue.equals(programState.getId())) {
            programState.getLockTable().put(idx, -1);
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
