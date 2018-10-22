package tests;


import model.expression.ConstantExpression;
import model.programState.ProgramState;
import org.junit.Test;

public class ConstantExpressionTest {

    @Test
    public void testToString() {
        ConstantExpression e = new ConstantExpression(3);
        ConstantExpression e1 = new ConstantExpression(4);

        assert e.toString().equals("3");
        assert e1.toString().equals("4");
    }

    @Test
    public void evaluate() {
        ProgramState state = new ProgramState();
        ConstantExpression e = new ConstantExpression(3);
        ConstantExpression e1 = new ConstantExpression(4);

        assert e.evaluate(state.getSymbols()) == 3;
        assert e1.evaluate(state.getSymbols()) == 4;
    }
}
