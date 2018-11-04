package model.statement;

import exceptions.UndefinedOperationException;
import exceptions.UndefinedVariableException;
import javafx.util.Pair;
import model.programState.ProgramState;

import java.io.BufferedReader;
import java.io.IOException;

public class OpenFileStatement extends AbstractStatement {
    private static final String programFilesPath = "D:\\CS\\MAP\\ToyLanguageInterpreter\\outputFiles\\programCreatedFiles";
    private String varName;
    private String filename;
    private String functionName;

    public OpenFileStatement(String varName, String filename) {
        this.varName = varName;
        this.filename = filename;
        this.functionName = "main";
    }

    public OpenFileStatement(String varName, String filename, String functionName) {
        this.varName = varName;
        this.filename = filename;
        this.functionName = functionName;
    }

    @Override
    public String toString() {
        return "Open file: " + filename;
    }

    @Override
    public ProgramState execute(ProgramState programState) throws UndefinedOperationException, UndefinedVariableException, IOException {
        //check if filename is already opened
        for (Pair<String, BufferedReader> f : programState.getFiles().getAll().values()) {
            if (f.getKey().equals(filename)) {
                //file already opened
                throw new IOException("File" + filename + " already opened");
            }
        }
        //update the varName with the new UID (descriptor) and store the file
        programState.getSymbols().put(varName,
                programState.getFiles().storeFile(filename, programFilesPath));
        return programState;
    }

    @Override
    public String getFunction() {
        return functionName;
    }

    @Override
    public void setFunction(String functionName) {
        this.functionName = functionName;
    }
}
