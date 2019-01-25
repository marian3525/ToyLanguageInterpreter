package repository;

import exceptions.RepositoryException;
import javafx.util.Pair;
import model.programState.ProgramState;
import model.statement.AbstractStatement;
import model.util.Observable;
import model.util.Observer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

public class Repository extends Observable implements RepositoryInterface, Observer {
    private String logPath = "log.txt";
    // The repo observes all the program states and it is itself observed by the GUI controller
    private Map<String, ProgramState> progs;

    public Repository() {
        super();
        progs = new HashMap<>();
        try {
            //clear the file
            PrintWriter logFile = new PrintWriter(new BufferedWriter(new FileWriter(logPath, false)));
            logFile.close();
        } catch (IOException e) {
        }
    }

    public void addProgram(String progName, ProgramState programState) throws RepositoryException {
        if(progs.containsKey(progName)) {
            throw new RepositoryException("Program '" + progName + "' already exists!");
        }
        else {
            progs.put(progName, programState);
            programState.registerObserver(this);
            notifyObservers();
        }
    }

    @Override
    public void addProcedure(String name, Pair<List<String>, AbstractStatement> proc, String progName) {
        progs.get(progName).getProcTable().put(name, proc);
    }

    public ProgramState getProgramByName(String progName) throws RepositoryException {
        if(progs.containsKey(progName)) {
            return progs.get(progName);
        }
        else {
            throw new RepositoryException("Program with name: '" + progName + "' does not exist");
        }
    }

    @Override
    public void setPath(String path) {
        this.logPath = path;
    }
    /**
     * Print the program state in the given path in the format:
     * ExeStack:
     * Top of the stack as a string
     * Top-1 of the stack as a string
     * .......
     * Bottom of the stack as a string
     * <p>
     * SymTable:
     * var_name1 --> value1
     * var_name2 --> value2
     * ....
     * <p>
     * Out:
     * value1
     * value2
     * .......
     * FileTableInterface:
     * id1 --> filename1
     * id2 --> filename2
     *
     * @param state: the program state to be printed
     * @throws IOException if creating the file failed
     */
    @SuppressWarnings("unchecked")
    @Override
    public void logProgramState(ProgramState state) {
        PrintWriter logFile;
        try {
            logFile = new PrintWriter(new BufferedWriter(new FileWriter(logPath, true)));

            String[] keys = {"stack", "symbols", "output", "files", "heap"};
            Map<String, String> stringMap = getStrings(state);

            //print the name of the program with this progState:
            List<String> matches = progs.keySet().stream()
                    .filter(pkey -> progs.get(pkey) == state)
                    .collect(Collectors.toList());
            if(matches.size() > 0) {

                String progName = matches.get(0);

                logFile.println(System.lineSeparator() + "ProgramState name: " + progName);
                for (String key : keys) {
                    logFile.print(stringMap.get(key));
                    logFile.println("--------------------------------------------------");
                }
                logFile.close();
            }
        } catch (IOException e) { }
    }

    @Override
    public Map<String, ProgramState> getPrograms() {
        return progs;
    }

    @Override
    public void setPrograms(Map<String, ProgramState> newPrograms) {
        progs = newPrograms;
        notifyObservers();
    }

    /**
     * Build a map containing the string representation of the internal state.
     * map.get("stack") will store the content of the stack etc
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    @Override
    public Map<String, String> getStrings(ProgramState state) {

        Map<String, String> stringMap = new HashMap<>();
        String progNameString, stackString, outputString, symbolsString, filesString, heapString;
        Stack<AbstractStatement> s = (Stack<AbstractStatement>) state.getExecutionStack().clone();

        //build each string with a builder
        StringBuilder builder = new StringBuilder();
        //stack string
        builder.append("Execution stack:").append(System.lineSeparator());
        while (!s.isEmpty()) {
            builder.append(s.peek()).append(System.lineSeparator());
            s.pop();
        }
        stackString = builder.toString();
        builder.delete(0, builder.length());

        //output string
        builder.append("Output:").append(System.lineSeparator());
        Vector<String> output = state.getOutput();
        for (String out : output) {
            builder.append(out).append(" | ");
        }
        outputString = builder.toString();
        builder.delete(0, builder.length());    //clear the builder

        //symbol table
        builder.append("Symbol Table:").append(System.lineSeparator());
        HashMap<String, Integer> symTable = (HashMap<String, Integer>) state.getSymbols();
        for (Object key : symTable.keySet()) {
            builder.append((String) key).append(" --> ").append(symTable.get(key).toString()).append(" | ");
        }
        builder.append(System.lineSeparator());
        symbolsString = builder.toString();
        builder.delete(0, builder.length());    //clear the builder

        //filetable
        builder.append("File Table:").append(System.lineSeparator());
        for (Integer k : state.getFiles().getAll().keySet()) {
            builder.append(k).append(" --> ").append(state.getFiles().getAll().get(k)).append(" | ");
        }
        filesString = builder.toString();
        builder.delete(0, builder.length());    //clear the builder

        //heap
        builder.append("Heap:").append(System.lineSeparator());
        for (Integer k : state.getHeap().getAll().keySet()) {
            builder.append(k).append("-->").append(state.getHeap().get(k)).append(System.lineSeparator());
        }
        heapString = builder.toString();

        stringMap.put("output", outputString);
        stringMap.put("stack", stackString);
        stringMap.put("symbols", symbolsString);
        stringMap.put("files", filesString);
        stringMap.put("heap", heapString);

        return stringMap;
    }

    @Override
    public void update() {
        // received update from one of the program states, notify the Observers higher up of the change
        notifyObservers();
    }
}
