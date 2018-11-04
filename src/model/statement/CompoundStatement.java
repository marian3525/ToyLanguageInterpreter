package model.statement;

import exceptions.UndefinedOperationException;
import exceptions.UndefinedVariableException;
import model.programState.ProgramState;

import java.io.IOException;

public class CompoundStatement extends AbstractStatement {
    private AbstractStatement first;
    private AbstractStatement second;
    private String functionName;

    public CompoundStatement(AbstractStatement first, AbstractStatement second) {
        this.first = first;
        this.second = second;
        this.functionName = "main";
    }

    public CompoundStatement(AbstractStatement first, AbstractStatement second, String functionName) {
        this.first = first;
        this.second = second;
        this.functionName = functionName;
    }

    @Override
    public ProgramState execute(ProgramState programState) throws UndefinedOperationException, UndefinedVariableException, IOException {
        programState.getExecutionStack().push(second);
        programState.getExecutionStack().push(first);
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

    @Override
    public String toString() {
        return "(" + first.toString() + ";" + second.toString() + ")";
    }
}
