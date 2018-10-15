package model.statement;

import exceptions.UndefinedOperationException;
import exceptions.UndefinedVariableException;
import model.expression.Expression;
import model.programState.ProgramState;

public class PrintStatement implements Statement {
    Expression expression;

    public PrintStatement(Expression expression) {
        this.expression = expression;
    }

    @Override
    public String toString() {
        return "print(" + expression.toString() + ")";
    }
    @Override
    public ProgramState execute(ProgramState programState) throws UndefinedOperationException,
                                                                    UndefinedVariableException {
        programState.getOutput().add(Integer.toString(expression.evaluate(programState.getSymbols())));
        return programState;
    }
}
