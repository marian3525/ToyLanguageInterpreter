package tests;

import exceptions.UndefinedOperationException;
import exceptions.UndefinedVariableException;
import model.expression.ArithmeticExpression;
import model.expression.ConstantExpression;
import model.expression.Expression;
import model.expression.VariableExpression;
import model.programState.ProgramState;
import model.statement.*;
import org.junit.Test;

public class IfStatementTest {
    @Test
    public void testToString() {

    }

    @Test
    public void execute() {
        Expression c1Const = new ConstantExpression(2);
        ProgramState state = new ProgramState();

        state.getSymbols().put("a", 2);
        Expression c1Var = new VariableExpression("a");

        Expression c1Arith = new ArithmeticExpression(new ConstantExpression(1), new VariableExpression("a"), "+");

        Statement thenStatement = new PrintStatement(new ConstantExpression(3));
        Statement elseStatement = new PrintStatement(new ConstantExpression(4));

        IfStatement i = new IfStatement(c1Const, thenStatement, elseStatement);
        IfStatement j = new IfStatement(c1Var, thenStatement, elseStatement);
        IfStatement k = new IfStatement(c1Arith, thenStatement, elseStatement);
        IfStatement l = new IfStatement(new ConstantExpression(0), thenStatement, elseStatement);
        state.getSymbols().put("b", 0);
        IfStatement m = new IfStatement(new VariableExpression("b"), thenStatement, elseStatement);
        IfStatement n = new IfStatement(new ArithmeticExpression(new ConstantExpression(10),
                new ConstantExpression(10), "-"), thenStatement, elseStatement);

        try {
            i.execute(state);
            assert state.getExecutionStack().pop() == thenStatement;
            j.execute(state);
            assert state.getExecutionStack().pop() == thenStatement;
            k.execute(state);
            assert state.getExecutionStack().pop() == thenStatement;
            l.execute(state);
            assert state.getExecutionStack().pop() == elseStatement;
            m.execute(state);
            assert state.getExecutionStack().pop() == elseStatement;
            n.execute(state);
            assert state.getExecutionStack().pop() == elseStatement;

        } catch (UndefinedOperationException | UndefinedVariableException e) {
            assert false;
        }

        CompoundStatement s = new CompoundStatement(new AssignmentStatement("a", new ArithmeticExpression
                (new VariableExpression("a"), new ConstantExpression(1), "+")),
                new PrintStatement(new VariableExpression("a")));
        IfStatement compIf = new IfStatement(new ConstantExpression(1), new PrintStatement(new VariableExpression("a")),
                s);
        IfStatement compIf1 = new IfStatement(new ConstantExpression(0), new PrintStatement(new VariableExpression("a")),
                s);
        try {
            compIf.execute(state);
            assert state.getExecutionStack().peek().toString().equals("print(a)");
            compIf1.execute(state);
            //(a = a + 1;print(a)
            assert state.getExecutionStack().peek().toString().equals("(a = a+1;print(a))");

        } catch (UndefinedOperationException | UndefinedVariableException e) {
            assert false;
        }
    }
}
