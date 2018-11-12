package model.programState;


import model.adt.Heap;
import model.function.AbstractFunction;
import model.interfaces.HeapInterface;
import model.statement.AbstractStatement;
import model.util.FileTable;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;

public class ProgramState {
    private Stack<AbstractStatement> executionStack;
    private Map<String, Integer> symbols;
    private Map<String, AbstractFunction> functionTable;
    private Vector<String> output;
    private FileTable files;
    private HeapInterface heap;
    private int lastReturn;
    private boolean functionFinished;

    public ProgramState() {
        executionStack = new Stack<>();
        symbols = new HashMap<>();
        functionTable = new HashMap<>();
        output = new Vector<>(10);
        files = new FileTable();
        heap = new Heap();
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

    public void logToFile(String filename) throws IOException {
        PrintWriter logFile = new PrintWriter(new FileWriter(filename, false));
        //todo: move printing from repo to programState
    }
}
