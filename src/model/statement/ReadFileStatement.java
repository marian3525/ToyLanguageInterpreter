package model.statement;

import exceptions.SyntaxException;
import exceptions.UndefinedOperationException;
import exceptions.UndefinedVariableException;
import model.expression.AbstractExpression;
import model.programState.ProgramState;

import java.io.BufferedReader;
import java.io.IOException;

import static model.expression.AbstractExpression.getExpressionFromType;
import static model.expression.AbstractExpression.getExpressionType;

public class ReadFileStatement extends AbstractStatement {
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

        String exprType = getExpressionType(fileIdStr);
        fileId = getExpressionFromType(fileIdStr, exprType);
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
