package model.statement.CyclicBarrier;

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

public class AwaitBarrierStatement extends AbstractStatement {
    private String varName;
    private static Lock lock = new ReentrantLock();

    public AwaitBarrierStatement(String varName) {
        this.varName = varName;
    }

    @Override
    public String toString() {
        return "awaitBarrier(" + varName + ")";
    }

    @Override
    public ProgramState execute(ProgramState programState) throws UndefinedOperationException, UndefinedVariableException, IOException, SyntaxException {
        lock.lock();

        Integer idx = programState.getSymbols().get(varName);

        if(idx == null) {
            throw new UndefinedVariableException("Variable " + varName + " not defined");
        }
        Pair<Integer, List<Integer>> barrierValue = programState.getBarrierTable().get(idx);
        List<Integer> threads = barrierValue.getValue();
        Integer n1 = barrierValue.getKey();
        Integer nt = threads.size();

        if(n1 > nt) {
            if(barrierValue.getValue().contains(programState.getId()))
                programState.getExecutionStack().push(this);
            else {
                threads.add(programState.getId());
                programState.getBarrierTable().put(idx, new Pair<>(n1, threads));
            }
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
