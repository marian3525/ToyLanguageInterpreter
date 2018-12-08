package model.statement;

import exceptions.SyntaxException;
import exceptions.UndefinedOperationException;
import exceptions.UndefinedVariableException;
import model.expression.AbstractExpression;
import model.programState.ProgramState;
import org.intellij.lang.annotations.RegExp;
import parsers.ExpressionParser;

import java.io.IOException;

public class CloseFileStatement extends AbstractStatement {

    @RegExp
    private static final String closeFileStatementRegex = "closeFile\\([^\\s]+\\)";

    private AbstractExpression fileId;
    private String functionName;

    public CloseFileStatement(AbstractExpression fileId) {
        this.fileId = fileId;
        this.functionName = "main";
    }

    public CloseFileStatement(AbstractExpression fileId, String functionName) {
        this.fileId = fileId;
        this.functionName = functionName;
    }

    /**
     * Expected syntax closeFile(fileIdExpr)
     *
     * @param input
     * @return
     * @throws SyntaxException
     */
    public static CloseFileStatement getCloseFileStatementFromString(String input) throws SyntaxException {
        String fileId = input.replace("closeFile(", "").replace(")", "");

        AbstractExpression fileIdExpression = ExpressionParser.getExpressionFromString(fileId);

        CloseFileStatement closeFileStatement = new CloseFileStatement(fileIdExpression);
        return closeFileStatement;
    }

    @Override
    public String toString() {
        return "Close file: " + fileId;
    }

    @Override
    public ProgramState execute(ProgramState programState) throws UndefinedOperationException, UndefinedVariableException, IOException, SyntaxException {
        int descriptor = fileId.evaluate(programState.getSymbols(), programState.getHeap());
        //close the file
        programState.getFiles().getFile(descriptor).getValue().close();
        //and delete the entry from the table
        programState.getFiles().getAll().remove(descriptor);

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
        return statementString.matches(closeFileStatementRegex);
    }
}
