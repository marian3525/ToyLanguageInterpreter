package tests;


import exceptions.SyntaxException;
import model.expression.AbstractExpression;
import model.expression.ArithmeticExpression;
import model.expression.ConstantExpression;
import org.junit.Test;

import java.util.Vector;

import static org.junit.Assert.assertArrayEquals;

public class AbstractExpressionTest {

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
        assertArrayEquals(expected.toArray(), AbstractExpression.tokenize(expr).toArray());

        String input = "32*(1+readHeap(varName))*3";
        String[] toks = {"32", "*", "(", "1", "+", "readHeap(varName)", ")", "*", "3"};

        Vector<String> tok = AbstractExpression.tokenize(input);
        for (int i = 0; i < toks.length; i++) {
            assert toks[i].equals(tok.elementAt(i));
        }
    }
    @Test
    public void infixToPostfixTest() {
        String infix = "2*3-8/2";
        try {
            Vector<String> postfix = AbstractExpression.infixToPostfix(infix);
            assertArrayEquals(postfix.toArray(), new String[]{"2","3","*","8","2","/","-"});
        } catch (SyntaxException e) {
            assert(false);
        }
        infix = "2*3-8";
        try {
            Vector<String> postfix = AbstractExpression.infixToPostfix(infix);
            assertArrayEquals(postfix.toArray(), new String[]{"2","3","*","8","-"});
        } catch (SyntaxException e) {
            assert(false);
        }
        infix = "2*(3-8)";
        try {
            Vector<String> postfix = AbstractExpression.infixToPostfix(infix);
            assertArrayEquals(postfix.toArray(), new String[]{"2", "3", "8", "-", "*"});
        } catch (SyntaxException e) {
            assert(false);
        }
        infix = "21*(30-8)";
        try {
            Vector<String> postfix = AbstractExpression.infixToPostfix(infix);
            assertArrayEquals(postfix.toArray(), new String[]{"21", "30", "8", "-", "*"});
        } catch (SyntaxException e) {
            assert(false);
        }
        infix = "21*(30-8*readHeap(varName))";
        try {
            Vector<String> postfix = AbstractExpression.infixToPostfix(infix);
            assertArrayEquals(postfix.toArray(), new String[]{"21", "30", "8", "readHeap(varName)", "*", "-", "*"});
        } catch (SyntaxException e) {
            assert (false);
        }
    }

    @Test
    public void postfixToInfixTest() {
        String postfix = "235*+";
        //Vector<String> infix = AbstractExpression.postfixToInfix(postfix);
        //assertArrayEquals(infix.toArray(), new String[] {"2", "+", "3", "*", "5"});
    }
    @Test
    public void buildExpressionFromPostfix() throws SyntaxException {
        Vector<String> postfix1 = new Vector<String>(10);
        postfix1.add("2");
        postfix1.add("3");
        postfix1.add("4");
        postfix1.add("*");
        postfix1.add("+");
        Vector<String> postfix2 = AbstractExpression.infixToPostfix("2+(3+2*8)/2");

        AbstractExpression e1 = AbstractExpression.buildExpressionFromPostfix(postfix1);
        AbstractExpression e2;
        e2 = AbstractExpression.buildExpressionFromPostfix(postfix2);


        AbstractExpression e1ShouldBe = new ArithmeticExpression(new ConstantExpression(2), new ArithmeticExpression
                (new ConstantExpression(3), new ConstantExpression(4), "*"), "+");
        assert(e1.toString().equals(e1ShouldBe.toString()));

        String e2ShouldBe = "2+((3+(2*8))/2)";
        assert(e2.toString().equals(e2ShouldBe));
    }
}
