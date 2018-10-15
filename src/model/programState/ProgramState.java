package model.programState;

import model.statement.Statement;

import java.util.*;

public class ProgramState {
    private Stack<Statement> executionStack;
    private Map<String, Integer> symbols;
    private Vector<String> output;

    public ProgramState() {
        executionStack = new Stack<>();
        symbols = new HashMap<>();
        output = new Vector<>(10);
    }

    public Stack<Statement> getExecutionStack() {
        return executionStack;
    }

    public Map<String, Integer> getSymbols() {
        return symbols;
    }

    public Vector<String> getOutput() {
        return output;
    }

}
