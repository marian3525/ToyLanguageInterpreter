package model.statement;

import exceptions.SyntaxException;
import exceptions.UndefinedOperationException;
import exceptions.UndefinedVariableException;
import model.programState.ProgramState;

import java.io.IOException;

public class ReadHeapStatement extends AbstractStatement {
    private String varName;
    private String functionName;

    public ReadHeapStatement(String varName) {
        this.varName = varName;
    }

    /**
     * Syntax: readHeap(varName_addr)
     * It returns the read value
     *
     * @param statement
     * @return
     */
    static ReadHeapStatement getReadHeapStatementFromString(String statement) {
        statement = statement.replace("readHeap(", "").replace(")", "");
        String varName = statement;
        ReadHeapStatement readHeapStatement = new ReadHeapStatement(varName);
        return readHeapStatement;
    }

    @Override
    public String toString() {
        return "Read from address: " + varName;
    }

    @Override
    public ProgramState execute(ProgramState programState) throws UndefinedOperationException, UndefinedVariableException, IOException, SyntaxException {

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
}
