package model.statement;

import exceptions.SyntaxException;
import exceptions.UndefinedOperationException;
import exceptions.UndefinedVariableException;
import model.expression.AbstractExpression;
import model.programState.ProgramState;

import java.io.IOException;

import static model.expression.AbstractExpression.getExpressionFromType;
import static model.expression.AbstractExpression.getExpressionType;

public class CloseFileStatement extends AbstractStatement {

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

    public static CloseFileStatement getCloseFileStatementFromString(String input) throws SyntaxException {
        AbstractExpression fileIdExpression;

        String fileId = input.replace("closeFile(", "").replace(")", "");
        String expressionType = getExpressionType(fileId);

        fileIdExpression = getExpressionFromType(fileId, expressionType);

        CloseFileStatement closeFileStatement = new CloseFileStatement(fileIdExpression);
        return closeFileStatement;
    }

    @Override
    public String toString() {
        return "Close file: " + fileId;
    }

    @Override
    public ProgramState execute(ProgramState programState) throws UndefinedOperationException, UndefinedVariableException, IOException {
        int descriptor = fileId.evaluate(programState.getSymbols(), programState.getHeap());
        //close the file
        programState.getFiles().getFile(descriptor).getValue().close();
        //and delete the entry from the table
        programState.getFiles().getAll().remove(descriptor);

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
