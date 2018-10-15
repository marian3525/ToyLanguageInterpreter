package model.statement;

import exceptions.UndefinedOperationException;
import exceptions.UndefinedVariableException;
import model.programState.ProgramState;

public interface Statement {
    String toString();
    ProgramState execute(ProgramState programState) throws UndefinedOperationException, UndefinedVariableException;
}
