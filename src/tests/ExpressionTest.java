package tests;


import exceptions.ProgramException;
import exceptions.SyntaxException;
import model.adt.Vector;
import model.expression.ArithmeticExpression;
import model.expression.ConstantExpression;
import model.expression.Expression;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

public class ExpressionTest {

    @Test
    public void testTokenize() {
        String expr = "a+(b-21+44*v3)";
        Vector<String> expected = new Vector<>(10);
        expected.add("a");
        expected.add("+");
        expected.add("(");
        expected.add("b");
        expected.add("-");
        expected.add("21");
        expected.add("+");
        expected.add("44");
        expected.add("*");
        expected.add("v3");
        expected.add(")");
        //assert(false);
        assertArrayEquals(expected.toArray(), Expression.tokenize(expr).toArray());
    }
    @Test
    public void infixToPostfix() {
        String infix = "2*3-8/2";
        try {
            Vector<String> postfix = Expression.infixToPostfix(infix);
            assertArrayEquals(postfix.toArray(), new String[]{"2","3","*","8","2","/","-"});
        } catch (SyntaxException e) {
            assert(false);
        }
        infix = "2*3-8";
        try {
            Vector<String> postfix = Expression.infixToPostfix(infix);
            assertArrayEquals(postfix.toArray(), new String[]{"2","3","*","8","-"});
        } catch (SyntaxException e) {
            assert(false);
        }
        infix = "2*(3-8)";
        try {
            Vector<String> postfix = Expression.infixToPostfix(infix);
            assertArrayEquals(postfix.toArray(), new String[]{"2", "3", "8", "-", "*"});
        } catch (SyntaxException e) {
            assert(false);
        }
        infix = "21*(30-8)";
        try {
            Vector<String> postfix = Expression.infixToPostfix(infix);
            assertArrayEquals(postfix.toArray(), new String[]{"21", "30", "8", "-", "*"});
        } catch (SyntaxException e) {
            assert(false);
        }
    }

    @Test
    public void buildExpressionFromPostfix() throws ProgramException, SyntaxException {
        Vector<String> postfix1 = new Vector<String>(10);
        postfix1.add("2");
        postfix1.add("3");
        postfix1.add("4");
        postfix1.add("*");
        postfix1.add("+");
        Vector<String> postfix2=null;
        postfix2 = Expression.infixToPostfix("2+(3+2*8)/2");

        Expression e1 = Expression.buildExpressionFromPostfix(postfix1);
        Expression e2;
        e2 = Expression.buildExpressionFromPostfix(postfix2);


        Expression e1ShouldBe = new ArithmeticExpression(new ConstantExpression(2), new ArithmeticExpression
                (new ConstantExpression(3), new ConstantExpression(4), "*"), "+");
        assert(e1.toString().equals(e1ShouldBe.toString()));

        String e2ShouldBe = "2+((3+(2*8))/2)";
        assert(e2.toString().equals(e2ShouldBe));
    }
}
