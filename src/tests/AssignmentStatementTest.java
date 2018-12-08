package tests;

import exceptions.SyntaxException;
import exceptions.UndefinedOperationException;
import exceptions.UndefinedVariableException;
import model.expression.ArithmeticExpression;
import model.expression.ConstantExpression;
import model.expression.VariableExpression;
import model.programState.ProgramState;
import model.statement.AssignmentStatement;
import org.junit.Test;

public class AssignmentStatementTest {

    @Test
    public void testToString() {
        ProgramState state = new ProgramState();
        AssignmentStatement e = new AssignmentStatement("a", new ConstantExpression(3));

        assert e.toString().equals("a = 3");
    }

    @Test
    public void testExecute() {
        ProgramState state = new ProgramState();
        AssignmentStatement e = new AssignmentStatement("a", new ConstantExpression(3));
        try {
            e.execute(state);
            assert state.getSymbols().get("a") == 3;
        } catch (UndefinedVariableException | UndefinedOperationException | SyntaxException ex) {
            assert false;
        }

        e = new AssignmentStatement("b", new ArithmeticExpression(new VariableExpression("a"),
                new ConstantExpression(1),
                "+"));
        try {
            e.execute(state);
            assert state.getSymbols().get("b") == 4;
        } catch (UndefinedVariableException | UndefinedOperationException | SyntaxException ex) {
            assert false;
        }

        e = new AssignmentStatement("a", new ConstantExpression(-1));
        try {
            e.execute(state);
            assert state.getSymbols().get("a") == -1;
        } catch (UndefinedVariableException | UndefinedOperationException | SyntaxException ex) {
            assert false;
        }

    }
}
