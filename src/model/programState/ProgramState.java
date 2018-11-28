package model.programState;


import exceptions.ProgramException;
import exceptions.SyntaxException;
import exceptions.UndefinedOperationException;
import exceptions.UndefinedVariableException;
import model.adt.Heap;
import model.function.AbstractFunction;
import model.interfaces.HeapInterface;
import model.statement.AbstractStatement;
import model.util.FileTable;

import java.io.IOException;
import java.util.*;

public class ProgramState {
    private Stack<AbstractStatement> executionStack;
    private Map<String, Integer> symbols;
    private Map<String, AbstractFunction> functionTable;
    private Vector<String> output;
    private FileTable files;
    private HeapInterface heap;
    private static int progCount = 0;       //used to assign an unique id to every progState
    private int id;
    private int lastReturn;
    private boolean functionFinished;

    public ProgramState() {
        executionStack = new Stack<>();
        symbols = new HashMap<>();
        functionTable = new HashMap<>();
        output = new Vector<>(10);
        files = new FileTable();
        heap = new Heap();
        id = progCount;
        progCount++;
    }

    /**
     * Copy constructor: copy the symbols. Copy the reference to the heap, filetable and output
     * Used by forkStatement
     */
    public ProgramState(ProgramState source) {
        executionStack = new Stack<>();
        symbols = new HashMap<>();
        functionTable = new HashMap<>();

        // copy
        source.symbols.forEach(symbols::put);
        source.functionTable.forEach(functionTable::put);

        // reference
        output = source.output;
        // reference
        files = source.files;
        // reference
        heap = source.heap;
        id = progCount;
        progCount++;
    }


    public Stack<AbstractStatement> getExecutionStack() {
        return executionStack;
    }

    public Map<String, Integer> getSymbols() {
        return symbols;
    }

    public Map<String, AbstractFunction> getFunctions() {
        return functionTable;
    }

    public Vector<String> getOutput() {
        return output;
    }

    public FileTable getFiles() {
        return files;
    }

    public HeapInterface getHeap() {
        return heap;
    }

    /**
     * @return the return value of the last function call
     */
    public int getLastReturn() {
        return lastReturn;
    }

    public void setLastReturn(int lastReturn) {
        this.lastReturn = lastReturn;
    }

    public boolean getFunctionFinished() {
        return functionFinished;
    }

    public void setFunctionFinished(boolean finished) {
        this.functionFinished = finished;
    }

    public int getId() {
        return id;
    }

    public boolean isNotCompleted() {
        return !executionStack.empty();
    }

    public ProgramState step() throws ProgramException, UndefinedVariableException, UndefinedOperationException, SyntaxException, IOException {
        AbstractStatement top;

        try {
            top = executionStack.pop();
        } catch (EmptyStackException ese) {
            throw new ProgramException("End of program reached");
        }
        if (top != null)
            return top.execute(this);
        else
            return null;
    }
}
