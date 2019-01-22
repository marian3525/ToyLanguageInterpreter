package model.programState;


import exceptions.ProgramException;
import exceptions.SyntaxException;
import exceptions.UndefinedOperationException;
import exceptions.UndefinedVariableException;
import javafx.application.Platform;
import model.adt.Heap;
import model.interfaces.HeapInterface;
import model.statement.AbstractStatement;
import model.util.FileTable;
import model.util.Observable;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;

public class ProgramState extends Observable {
    private Stack<AbstractStatement> executionStack;
    private Map<String, Integer> symbols;
    private Vector<String> output;
    private FileTable files;
    private HeapInterface heap;
    private static int progCount = 0;       //used to assign an unique id to every progState
    private int id;
    private int lastReturn;
    private boolean functionFinished;

    public ProgramState() {
        // init the Observable
        super();

        executionStack = new Stack<>();
        symbols = new HashMap<>();
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
    public ProgramState(@NotNull ProgramState source) {
        executionStack = new Stack<>();
        symbols = new HashMap<>();

        // copy
        source.symbols.forEach(symbols::put);

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
        notifyObservers();
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
        if (top != null) {
            ProgramState ret = top.execute(this);
            // notify the repo
            Platform.runLater(this::notifyObservers);
            return ret;
        }

        else
            return null;
    }
}
