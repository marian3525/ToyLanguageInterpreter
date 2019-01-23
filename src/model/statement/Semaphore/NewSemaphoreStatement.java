package model.statement.Semaphore;

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

public class NewSemaphoreStatement extends AbstractStatement {
    private String varName;
    private AbstractExpression expression;
    private static Lock lock = new ReentrantLock();

    public NewSemaphoreStatement(String varName, AbstractExpression expression) {
        this.varName = varName;
        this.expression = expression;
    }
    @Override
    public String toString() {
        return "newSemaphore(" + varName + "," + expression.toString();
    }

    @Override
    public ProgramState execute(ProgramState programState) throws UndefinedOperationException, UndefinedVariableException, IOException, SyntaxException {
        lock.lock();

        Integer value = expression.evaluate(programState.getSymbols(), programState.getHeap());
        Integer location = programState.getNewSemaphoreAddress();
        programState.getSemaphoreTable().put(location, new Pair<>(value, new ArrayList<>()));
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
