package model.statement;

import exceptions.UndefinedOperationException;
import exceptions.UndefinedVariableException;
import model.expression.AbstractExpression;
import model.programState.ProgramState;

import java.io.BufferedReader;
import java.io.IOException;

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
        int descriptor = fileId.evaluate(programState.getSymbols());
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
