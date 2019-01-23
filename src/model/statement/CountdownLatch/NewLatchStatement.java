package model.statement.CountdownLatch;

import exceptions.SyntaxException;
import exceptions.UndefinedOperationException;
import exceptions.UndefinedVariableException;
import model.expression.AbstractExpression;
import model.programState.ProgramState;
import model.statement.AbstractStatement;

import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class NewLatchStatement extends AbstractStatement {
    private String varName;
    private AbstractExpression expression;
    private static Lock lock = new ReentrantLock();

    public NewLatchStatement(String varName, AbstractExpression expression) {
        this.varName = varName;
        this.expression = expression;
    }

    @Override
    public String toString() {
        return "newLatch("+varName + "," + expression.toString() + ")";
    }

    @Override
    public ProgramState execute(ProgramState programState) throws UndefinedOperationException, UndefinedVariableException, IOException, SyntaxException {
        lock.lock();
        Integer latchAddr = programState.getNewLatchAddress();
        programState.getLatchTable().put(latchAddr, expression.evaluate(programState.getSymbols(), programState.getHeap()));
        programState.getSymbols().put(varName, latchAddr);
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
