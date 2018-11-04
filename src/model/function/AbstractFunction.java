package model.function;

import model.expression.ConstantExpression;
import model.programState.ProgramState;
import model.statement.AbstractStatement;

import java.util.Vector;

public abstract class AbstractFunction extends AbstractStatement {
    abstract Vector<ConstantExpression> getArguments();

    abstract void addStatements(Vector<AbstractStatement> statements);

    abstract void addStatement(AbstractStatement statement);

    abstract Vector<AbstractStatement> getStatements();

    abstract ProgramState load(ProgramState state);
}
