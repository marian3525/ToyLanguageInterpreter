package model.statement;

import exceptions.UndefinedOperationException;
import exceptions.UndefinedVariableException;
import model.adt.Pair;
import model.programState.ProgramState;
import org.intellij.lang.annotations.RegExp;

import java.io.BufferedReader;
import java.io.IOException;

public class OpenFileStatement extends AbstractStatement {
    @RegExp
    private static final String openFileStatementRegex = "^openFile\\(.*\\)$";
    private static final String programFilesPath = "D:\\CS\\MAP\\ToyLanguageInterpreter\\outputFiles";
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

    public static OpenFileStatement getOpenFileStatementFromString(String input) {
        String varName;     //will store the UID of the file, generated in the table insertion
        String filename;    //the name of the filename to be opened
        input = input.replace(" ", "");     //delete spaces so that variables don't end up with spaces in them

        String[] params = input.split(",");
        //extract the varName
        varName = params[0].replace("openFile(", "");
        filename = params[1].replace(")", "");

        OpenFileStatement openFileStatement = new OpenFileStatement(varName, filename);
        return openFileStatement;
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
        programState.getSymbols().put(varName, programState.getFiles().storeFile(filename, programFilesPath));
        return null;
    }

    @Override
    public String getFunction() {
        return functionName;
    }

    @Override
    public void setFunction(String functionName) {
        this.functionName = functionName;
    }

    /**
     * Check if the given string matches the structure of the statement described by this class
     * @param statementString string to be checked
     * @return true if the class can parse the string and output an object of this type
     *          false if the string doesn't match the class
     */
    public static boolean matchesString(String statementString) {
        return statementString.matches(openFileStatementRegex);
    }
}
