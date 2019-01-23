package model.programState;


import exceptions.ProgramException;
import exceptions.SyntaxException;
import exceptions.UndefinedOperationException;
import exceptions.UndefinedVariableException;
import javafx.application.Platform;
import javafx.util.Pair;
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
    private Map<Integer, Integer> latchTable;
    private Map<Integer, Integer> lockTable;
    private Map<Integer, Pair<Integer, List<Integer>>> barrierTable;
    private Map<Integer, Pair<Integer, List<Integer>>> semaphoreTable;
    private static int progCount = 0;       //used to assign an unique id to every progState
    private static int latchCount = 0;
    private static int barrierCount = 0;
    private static int semaphoreCount = 0;
    private static int lockCount = 0;
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

        latchTable = new HashMap<>();
        lockTable = new HashMap<>();
        barrierTable = new HashMap<>();
        semaphoreTable = new HashMap<>();

        id = progCount;
        progCount++;
    }

    /**
     * Copy constructor: copy the symbols. Copy the reference to the heap, filetable and output
     * Used by forkStatement
     */
    public ProgramState(@NotNull ProgramState source) {
        super();

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
        latchTable = source.latchTable;
        lockTable = source.lockTable;
        barrierTable = source.barrierTable;
        semaphoreTable=source.semaphoreTable;
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

    public Map<Integer, Integer> getLatchTable() {
        return latchTable;
    }

    public Map<Integer, Integer> getLockTable() {
        return lockTable;
    }

    public Map<Integer, Pair<Integer, List<Integer>>> getBarrierTable() {
        return barrierTable;
    }
    public Map<Integer, Pair<Integer, List<Integer>>> getSemaphoreTable() {
        return semaphoreTable;
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
            // notify the repo ON THE UI THREAD!
            // notifying on this thread (one from the Executor) will cause an illegal state exception because UI
            // elements will be updated from a thread other than the FX thread
            Platform.runLater(this::notifyObservers);
            return ret;
        }

        else
            return null;
    }

    public Integer getNewLatchAddress() {
        latchCount++;
        return latchCount;
    }

    public int getNewBarrierAddress() {
        barrierCount++;
        return barrierCount;
    }

    public int getNewSemaphoreAddress() {
        semaphoreCount++;
        return semaphoreCount;
    }

    public int getNewLockAddress() {
        lockCount++;
        return lockCount;
    }
}
