package model.statement;

import exceptions.SyntaxException;
import exceptions.UndefinedOperationException;
import exceptions.UndefinedVariableException;
import model.expression.AbstractExpression;
import model.programState.ProgramState;

import java.io.IOException;

public class NewEntryStatement extends AbstractStatement {

    private String varName;
    private AbstractExpression expression;

    public NewEntryStatement(String varName, AbstractExpression expression) {
        this.varName = varName;
        this.expression = expression;
    }

    @Override
    public String toString() {
        return null;
    }

    @Override
    public ProgramState execute(ProgramState programState) throws UndefinedOperationException, UndefinedVariableException, IOException, SyntaxException {
        return null;
    }

    @Override
    public String getFunction() {
        return null;
    }

    @Override
    public void setFunction(String functionName) {

    }
}
