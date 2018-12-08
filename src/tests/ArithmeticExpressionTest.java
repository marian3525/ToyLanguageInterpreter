package tests;

import exceptions.SyntaxException;
import exceptions.UndefinedOperationException;
import exceptions.UndefinedVariableException;
import model.expression.ArithmeticExpression;
import model.expression.ConstantExpression;
import model.expression.VariableExpression;
import model.programState.ProgramState;
import org.junit.Test;
public class ArithmeticExpressionTest {

    @Test
    public void testToString() {
    }

    @Test
    public void testEvaluate() {
        testAdd();
        testSubstract();
        testMultiply();
        testDivide();
    }

    private void testAdd() {
        ProgramState state = new ProgramState();

        //between constants
        ArithmeticExpression e1 = new ArithmeticExpression(new ConstantExpression(2),
                new ConstantExpression(1), "+");

        try {
            assert e1.evaluate(state.getSymbols(), state.getHeap()) == 3;
        } catch (UndefinedOperationException | UndefinedVariableException | SyntaxException e) {
            assert false;
        }

        //between vars
        ArithmeticExpression e2 = new ArithmeticExpression(new VariableExpression("a"),
                new VariableExpression("b"),
                "+");

        try {
            assert e1.evaluate(state.getSymbols(), state.getHeap()) == 3;
        } catch (UndefinedOperationException | UndefinedVariableException | SyntaxException e) {
            assert true;
        }

        state.getSymbols().put("a", 10);
        state.getSymbols().put("b", 12);

        //vars defined
        try {
            assert e2.evaluate(state.getSymbols(), state.getHeap()) == 22;
        } catch (UndefinedOperationException | UndefinedVariableException | SyntaxException e) {
            assert false;
        }
    }

    private void testSubstract() {
        ArithmeticExpression e1 = new ArithmeticExpression(new VariableExpression("a"), new VariableExpression("b"), "-");
        ProgramState state = new ProgramState();
    }

    private void testMultiply() {
        ArithmeticExpression e1 = new ArithmeticExpression(new VariableExpression("a"), new VariableExpression("b"), "*");
        ProgramState state = new ProgramState();
        try {
            //variables not defined, should throw
            assert e1.evaluate(state.getSymbols(), state.getHeap()) == 0;
            assert false;
        } catch (UndefinedOperationException | UndefinedVariableException | SyntaxException e) {
            assert true;
        }
        state.getSymbols().put("a", 1);
        state.getSymbols().put("b", 0);

        try {
            //variables defined
            assert e1.evaluate(state.getSymbols(), state.getHeap()) == 0;
        } catch (UndefinedOperationException | UndefinedVariableException | SyntaxException e) {
            assert false;
        }
    }

    private void testDivide() {
        ArithmeticExpression e1 = new ArithmeticExpression(new VariableExpression("a"), new VariableExpression("b"), "/");
        ProgramState state = new ProgramState();
        //divide by 0
        try {
            assert e1.evaluate(state.getSymbols(), state.getHeap()) == 0;
        } catch (UndefinedVariableException | UndefinedOperationException | SyntaxException e) {
            assert true;
        }
    }
}
