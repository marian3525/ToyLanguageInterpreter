package tests;

import controller.Controller;
import exceptions.RepositoryException;
import exceptions.SyntaxException;
import exceptions.UndefinedOperationException;
import exceptions.UndefinedVariableException;
import model.expression.BooleanExpression;
import model.expression.VariableExpression;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;


public class BooleanExpressionTest {
    Controller c;

    @Before
    public void setUp() throws RepositoryException {
        c = new Controller();
        c.addEmptyProgram("test");
    }

    @Test
    public void buildBooleanExpressionFromString() throws SyntaxException {
        String expr1 = "var1 < var2";
        String expr2 = "var1 <= var2";
        String expr3 = "var1 >= var2";
        String expr4 = "var1 > var2";
        String expr5 = "var1 != var2";
        String expr6 = "var1 == var2";
        String[] exprs = {expr1, expr2, expr3, expr4, expr5, expr6};
        BooleanExpression[] exprShouldBe = {
                new BooleanExpression(new VariableExpression("var1"), new VariableExpression("var2"), "<"),
                new BooleanExpression(new VariableExpression("var1"), new VariableExpression("var2"), "<="),
                new BooleanExpression(new VariableExpression("var1"), new VariableExpression("var2"), ">="),
                new BooleanExpression(new VariableExpression("var1"), new VariableExpression("var2"), ">"),
                new BooleanExpression(new VariableExpression("var1"), new VariableExpression("var2"), "!="),
                new BooleanExpression(new VariableExpression("var1"), new VariableExpression("var2"), "==")
        };

        for (int i = 0; i < exprs.length; i++) {
            assert BooleanExpression.buildBooleanExpressionFromString(exprs[i]).toString().
                    equals(exprShouldBe[i].toString());
        }
    }

    @Test
    public void testToString() {
        BooleanExpression[] exprs = {
                new BooleanExpression(new VariableExpression("var1"), new VariableExpression("var2"), "<"),
                new BooleanExpression(new VariableExpression("var1"), new VariableExpression("var2"), "<="),
                new BooleanExpression(new VariableExpression("var1"), new VariableExpression("var2"), ">="),
                new BooleanExpression(new VariableExpression("var1"), new VariableExpression("var2"), ">"),
                new BooleanExpression(new VariableExpression("var1"), new VariableExpression("var2"), "!="),
                new BooleanExpression(new VariableExpression("var1"), new VariableExpression("var2"), "==")
        };
        String expr1 = "var1 < var2";
        String expr2 = "var1 <= var2";
        String expr3 = "var1 >= var2";
        String expr4 = "var1 > var2";
        String expr5 = "var1 != var2";
        String expr6 = "var1 == var2";
        String[] exprsShouldBe = {expr1, expr2, expr3, expr4, expr5, expr6};

        for (int i = 0; i < exprs.length; i++) {
            assert exprs[i].toString().equals(exprsShouldBe[i]);
        }
    }

    @Test
    public void testEvaluate() throws SyntaxException, RepositoryException, UndefinedVariableException, IOException, UndefinedOperationException {
        //boolean expressions should be able to be evaluated correctly in arithmetic expressions
        String expr1 = "var1<var2+1";
        String expr2 = "var1<=var2 *2";
        String expr3 = "(var1>=var2)-3";
        String expr4 = "(var1>var2)+5";
        String expr5 = "var1+3!=var2";
        String expr6 = "(var1+3)==var2";
        String[] exprs = {expr1, expr2, expr3, expr4, expr5, expr6};
        int[] correctResults = {1, 1, -3, 5, 1, 0};

        c.addStatementString("var1=1", "test");
        c.addStatementString("var2=2", "test");
        c.run("test");

        //add the expressions in reverse order in the stack
        for (int i = exprs.length - 1; i >= 0; i--) {
            c.addStatementString("print(a)", "test");
            c.addStatementString("a=" + exprs[i], "test");
        }
        c.run("test");
        for (int i = 0; i < exprs.length; i++) {
            assert String.valueOf(correctResults[i]).equals(c.getOutput("test").elementAt(i));
        }
    }
}