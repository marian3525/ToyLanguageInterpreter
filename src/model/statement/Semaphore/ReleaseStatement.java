package model.statement.Semaphore;

import exceptions.SyntaxException;
import exceptions.UndefinedOperationException;
import exceptions.UndefinedVariableException;
import javafx.util.Pair;
import model.programState.ProgramState;
import model.statement.AbstractStatement;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ReleaseStatement extends AbstractStatement {
    private String varName;
    private static Lock lock = new ReentrantLock();

    public ReleaseStatement(String varName) {
        this.varName = varName;
    }

    @Override
    public String toString() {
        return "release(" + varName + ")";
    }

    @Override
    public ProgramState execute(ProgramState programState) throws UndefinedOperationException, UndefinedVariableException, IOException, SyntaxException {
        lock.lock();

        Integer idx = programState.getSymbols().get(varName);
        if(idx == null) {
            throw new UndefinedVariableException(varName + " undefined");
        }

        Pair<Integer, List<Integer>> semaphoreValue = programState.getSemaphoreTable().get(idx);
        Integer nMax = semaphoreValue.getKey();
        List<Integer> threads = semaphoreValue.getValue();

        if(threads.contains(programState.getId())) {
            threads.remove(programState.getId());
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
