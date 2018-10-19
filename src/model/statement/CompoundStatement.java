package model.statement;

import model.programState.ProgramState;

public class CompoundStatement extends Statement {
    private Statement first;
    private Statement second;

    public CompoundStatement(Statement first, Statement second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public ProgramState execute(ProgramState programState) {
        programState.getExecutionStack().push(second);
        programState.getExecutionStack().push(first);
        return programState;
    }

    @Override
    public String toString() {
        return "(" + first.toString() + ";" + second.toString() + ")";
    }
}
