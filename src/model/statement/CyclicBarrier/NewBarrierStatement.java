package model.statement.CyclicBarrier;

import exceptions.SyntaxException;
import exceptions.UndefinedOperationException;
import exceptions.UndefinedVariableException;
import javafx.util.Pair;
import model.expression.AbstractExpression;
import model.programState.ProgramState;
import model.statement.AbstractStatement;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class NewBarrierStatement extends AbstractStatement {
    private String varName;
    private AbstractExpression expression;
    private static Lock lock = new ReentrantLock();

    public NewBarrierStatement(String varName, AbstractExpression expression)  {
        this.varName = varName;
        this.expression = expression;
    }
    @Override
    public String toString() {
        return "newBarrier(" + varName + "," + expression.toString() + ")";
    }

    @Override
    public ProgramState execute(ProgramState programState) throws UndefinedOperationException, UndefinedVariableException, IOException, SyntaxException {
        lock.lock();

        Integer value = expression.evaluate(programState.getSymbols(), programState.getHeap());

        Integer location = programState.getNewBarrierAddress();

        programState.getBarrierTable().put(location, new Pair<>(value, new ArrayList<>()));
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
