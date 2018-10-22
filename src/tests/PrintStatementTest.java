package tests;

import exceptions.UndefinedOperationException;
import exceptions.UndefinedVariableException;
import model.expression.ConstantExpression;
import model.programState.ProgramState;
import model.statement.PrintStatement;
import org.junit.Test;

public class PrintStatementTest {

    @Test
    public void testToString() {
    }

    @Test
    public void execute() {
        ProgramState state = new ProgramState();
        PrintStatement s = new PrintStatement(new ConstantExpression(3));

        try {
            s.execute(state);
            assert state.getOutput().size() == 1;
            assert state.getOutput().elementAt(0).equals("3");

        } catch (UndefinedOperationException | UndefinedVariableException e) {
            assert false;
        }
    }
}
