package model.statement;

import exceptions.SyntaxException;
import exceptions.UndefinedOperationException;
import exceptions.UndefinedVariableException;
import model.expression.AbstractExpression;
import model.programState.ProgramState;
import org.intellij.lang.annotations.RegExp;
import parsers.ExpressionParser;

import java.io.BufferedReader;
import java.io.IOException;

public class ReadFileStatement extends AbstractStatement {
    @RegExp
    private static final String readFileStatementRegex = "^readFile\\(.*\\)$";
    private AbstractExpression fileId;
    private String varName;
    private String functionName;

    public ReadFileStatement(AbstractExpression fileId, String varName) {
        this.fileId = fileId;
        this.varName = varName;
        this.functionName = "main";
    }

    public ReadFileStatement(AbstractExpression fileId, String varName, String functionName) {
        this.fileId = fileId;
        this.varName = varName;
        this.functionName = functionName;
    }

    public static ReadFileStatement getReadFileStatementFromString(String input) throws SyntaxException {
        AbstractExpression fileId;
        String fileIdStr;
        String varName;
        input = input.replace(" ", "");     //delete spaces so that variables don't end up with spaces in them

        String[] params = input.split(",");
        //extract the varName
        fileIdStr = params[0].replace("readFile(", "");
        varName = params[1].replace(")", "");

        fileId = ExpressionParser.getExpressionFromString(fileIdStr);
        ReadFileStatement readFileStatement = new ReadFileStatement(fileId, varName);
        return readFileStatement;
    }

    @Override
    public String toString() {
        return "Read from file: " + fileId + " into var: " + varName;
    }

    /**
     * @param programState
     * @return
     * @throws UndefinedOperationException
     * @throws UndefinedVariableException
     * @throws IOException
     */
    @Override
    public ProgramState execute(ProgramState programState) throws UndefinedOperationException, UndefinedVariableException, IOException {
        int descriptor = fileId.evaluate(programState.getSymbols(), programState.getHeap());
        int readValue;
        BufferedReader reader = programState.getFiles().getFile(descriptor).getValue();

        if (reader == null) {
            throw new IOException("No file with the given descriptor");
        }

        String line = reader.readLine();
        if (line == null) {
            readValue = 0;
        } else {
            readValue = Integer.parseInt(line);
        }
        //update the symTable
        programState.getSymbols().put(varName, readValue);
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
        return statementString.matches(readFileStatementRegex);
    }
}
