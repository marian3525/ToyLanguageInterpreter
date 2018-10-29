package tests;

import model.expression.ConstantExpression;
import model.expression.VariableExpression;
import model.programState.ProgramState;
import model.statement.AssignmentStatement;
import model.statement.CompoundStatement;
import model.statement.PrintStatement;
import model.statement.Statement;
import org.junit.Test;
public class CompoundStatementTest {

    @Test
    public void testExecute() {
        ProgramState state = new ProgramState();

        Statement first = new AssignmentStatement("a", new ConstantExpression(3));
        Statement second = new PrintStatement(new VariableExpression("a"));

        CompoundStatement s = new CompoundStatement(first, second);

        try {
            s.execute(state);
        } catch (exceptions.UndefinedOperationException e) {
            e.printStackTrace();
        } catch (exceptions.UndefinedVariableException e) {
            e.printStackTrace();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }

        assert state.getExecutionStack().peek() == first;
        state.getExecutionStack().pop();
        assert state.getExecutionStack().peek() == second;
    }

    @Test
    public void testToString() {
        Statement first = new AssignmentStatement("a", new ConstantExpression(3));
        Statement second = new PrintStatement(new VariableExpression("a"));

        CompoundStatement s = new CompoundStatement(first, second);
        assert s.toString().equals("(" + first.toString() + ";" + second.toString() + ")");
    }
}
