package repository;

import exceptions.RepositoryException;
import model.programState.ProgramState;
import model.statement.AbstractStatement;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;

public class Repository implements RepositoryInterface {
    //vector of running programs
    private String logPath = "D:\\CS\\MAP\\ToyLanguageInterpreter\\outputFiles\\log.txt";
    private Map<String, ProgramState> progs;
    private static boolean isCreated = false;

    public Repository() {
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
            throw new RepositoryException("Program " + progName + " already exists!");
        }
        else {
            progs.put(progName, programState);
        }
    }

    public ProgramState getProgramByName(String progName) throws RepositoryException {
        if(progs.containsKey(progName)) {
            return progs.get(progName);
        }
        else {
            throw new RepositoryException("Program with name: " + progName + " does not exist");
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
    public void logProgramState(ProgramState state) throws IOException {
        PrintWriter logFile = new PrintWriter(new BufferedWriter(new FileWriter(logPath, true)));
        //print the executionStack
        Stack<AbstractStatement> s = (Stack<AbstractStatement>) state.getExecutionStack().clone();
        StringBuilder builder = new StringBuilder();

        builder.append("Execution stack:" + System.lineSeparator());
        while (!s.isEmpty()) {
            builder.append(s.peek()).append(System.lineSeparator());
            s.pop();
        }
        logFile.println(builder.toString());
        builder.delete(0, builder.length());    //clear the builder

        //print the symbol table
        builder.append("Symbol Table:" + System.lineSeparator());
        HashMap<String, Integer> symTable = (HashMap<String, Integer>) state.getSymbols();
        for (Object key : symTable.keySet()) {
            builder.append((String) key).append(" --> ").append(symTable.get(key).toString()).append(" | ");
        }
        logFile.println(builder.toString());
        builder.delete(0, builder.length());    //clear the builder

        //print the output vector
        builder.append("Output:" + System.lineSeparator());
        Vector<String> output = state.getOutput();
        for (String out : output) {
            builder.append(out).append(" | ");
        }
        logFile.println(builder.toString());
        builder.delete(0, builder.length());    //clear the builder

        //print the filetable
        builder.append("File Table:" + System.lineSeparator());
        for (Integer k : state.getFiles().getAll().keySet()) {
            builder.append(k).append(" --> ").append(state.getFiles().getAll().get(k)).append(" | ");
        }
        builder.append("----------------------------");
        builder.append(System.lineSeparator());
        logFile.print(builder.toString());

        builder.delete(0, builder.length());    //clear the builder
        logFile.close();
    }
}
