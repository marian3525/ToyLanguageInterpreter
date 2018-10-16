package model.statement;

import exceptions.UndefinedOperationException;
import exceptions.UndefinedVariableException;
import model.expression.Expression;
import model.programState.ProgramState;

public class IfStatement implements Statement {
    private Expression condition;
    private Statement thenStatement;
    private Statement elseStatement;
    public static final String ifRegex = "";

    public IfStatement(Expression condition, Statement thenStatement, Statement elseStatement) {
        this.condition = condition;
        this.thenStatement = thenStatement;
        this.elseStatement = elseStatement;
    }

    @Override
    public String toString() {
        return "if(" + condition.toString() + ") then(" + thenStatement.toString() + ") else(" +
                elseStatement.toString() + ")";
    }

    @Override
    public ProgramState execute(ProgramState programState) throws UndefinedOperationException, UndefinedVariableException {
        if(condition.evaluate(programState.getSymbols()) != 0) {
            programState.getExecutionStack().push(thenStatement);
        }
        else {
            programState.getExecutionStack().push(elseStatement);
        }
        return programState;
    }
}
