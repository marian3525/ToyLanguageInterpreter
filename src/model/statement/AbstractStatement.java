package model.statement;

import exceptions.SyntaxException;
import exceptions.UndefinedOperationException;
import exceptions.UndefinedVariableException;
import model.programState.ProgramState;

import java.io.IOException;

public abstract class AbstractStatement {
    public abstract String toString();

    public abstract ProgramState execute(ProgramState programState) throws UndefinedOperationException, UndefinedVariableException, IOException, SyntaxException;

    public abstract String getFunction();

    public abstract void setFunction(String functionName);
}
