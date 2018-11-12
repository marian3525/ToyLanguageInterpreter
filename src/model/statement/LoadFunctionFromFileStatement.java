package model.statement;

import exceptions.SyntaxException;
import exceptions.UndefinedOperationException;
import exceptions.UndefinedVariableException;
import javafx.util.Pair;
import model.function.Function;
import model.programState.ProgramState;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Vector;

/**
 * Class that will read a function with the given name from a file
 * Note: a file may have multiple function defined inside it
 */

public class LoadFunctionFromFileStatement extends AbstractStatement {
    private String functionName;
    private String path;

    public LoadFunctionFromFileStatement(String functionName, String path) {
        this.functionName = functionName;
        this.path = path;
    }

    public LoadFunctionFromFileStatement(String functionName) {
        this.functionName = functionName;
        this.path = "";
    }

    /**
     * Syntax: load <functionName>
     *
     * @param input
     * @return
     */
    static LoadFunctionFromFileStatement getLoadFunctionStatementFromString(String input) {
        String functionName = input.split(" ")[1];
        LoadFunctionFromFileStatement statement = new LoadFunctionFromFileStatement(functionName);
        return statement;
    }

    @Override
    public String toString() {
        return "Load: " + functionName + "from file: " + path;
    }

    /**
     * Open the file at the given path if it is not opened already
     * Search for the definition of the function with the given name and load it into the programState
     *
     * @param programState the current program state
     * @return the modified program state
     * @throws UndefinedOperationException
     * @throws UndefinedVariableException
     * @throws IOException
     */
    @Override
    public ProgramState execute(ProgramState programState) throws UndefinedOperationException, UndefinedVariableException, IOException, SyntaxException {
        //check if the file was already opened with an OpenFileStatement
        String line;
        BufferedReader reader = null;
        Function f;

        for (Pair<String, BufferedReader> p : programState.getFiles().getAll().values()) {
            if (path.contains(p.getKey())) {
                reader = p.getValue();
            }
        }

        if (reader == null) {
            //open and search for the function
            reader = new BufferedReader(new FileReader(path));
        }

        while ((line = reader.readLine()) != null) {
            if (line.contains("function " + functionName)) {
                //found the function, read the params
                line = line.split("\\(")[0];
                line = line.replace(")", "");
                String[] p = line.split(",");
                Vector<String> params = new Vector<>(Arrays.asList(p));

                f = new Function(functionName, params);

                //read the function body
                while ((line = reader.readLine()) != null) {
                    //if reached the end of the function
                    if (line.equals("end-" + functionName)) {
                        return programState;
                    }

                    //extract the statement from the line read and add it to the function
                    AbstractStatement statement = AbstractStatement.getStatementFromString(line
                    );
                    f.addStatement(statement);
                }
            }
        }
        return programState;
    }

    @Override
    public String getFunction() {
        return null;
    }

    @Override
    public void setFunction(String functionName) {

    }
}
