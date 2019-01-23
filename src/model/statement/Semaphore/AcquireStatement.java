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

public class AcquireStatement extends AbstractStatement {
    private String varName;
    private static Lock lock = new ReentrantLock();

    public AcquireStatement(String varName) {
        this.varName = varName;
    }




    @Override
    public String toString() {
        return "acquire(" + varName + ")";
    }

    @Override
    public ProgramState execute(ProgramState programState) throws UndefinedOperationException, UndefinedVariableException, IOException, SyntaxException {
        lock.lock();

        Integer idx = programState.getSymbols().get(varName);

        if(idx == null) {
            throw new UndefinedVariableException(varName + " not defined");
        }

        Pair<Integer, List<Integer>> semaphoreValue = programState.getSemaphoreTable().get(idx);

        List<Integer> threads = semaphoreValue.getValue();
        Integer nMax = semaphoreValue.getKey();

        if(nMax != threads.size()) {
            if(threads.contains(programState.getId())) {
                throw new SyntaxException("Already acquired");
            }
            threads.add(programState.getId());
            programState.getSemaphoreTable().put(idx, new Pair<>(nMax, threads));
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
