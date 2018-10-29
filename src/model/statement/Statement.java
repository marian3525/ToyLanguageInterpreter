package model.statement;

import exceptions.UndefinedOperationException;
import exceptions.UndefinedVariableException;
import model.programState.ProgramState;

import java.io.IOException;

public abstract class Statement {
    public abstract String toString();

    public abstract ProgramState execute(ProgramState programState) throws UndefinedOperationException, UndefinedVariableException, IOException;
}
