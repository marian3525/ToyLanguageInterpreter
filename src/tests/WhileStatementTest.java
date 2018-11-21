package tests;

import controller.Controller;
import exceptions.RepositoryException;
import exceptions.SyntaxException;
import exceptions.UndefinedOperationException;
import exceptions.UndefinedVariableException;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class WhileStatementTest {
    Controller c;

    @Before
    public void setUp() throws RepositoryException {
        c = new Controller();
        c.addEmptyProgram("test");
    }

    @Test
    public void testExecute() throws RepositoryException, SyntaxException, UndefinedVariableException, IOException, UndefinedOperationException {
        c.addStatementString("while(i<=10): print(i);i=i+1", "test");
        c.addStatementString("i=0", "test");
        c.run("test");

        assert c.getSymbols("test").get("i") == 11;
        assert c.getOutput("test").size() == 11;

        c.addEmptyProgram("test1");
        c.addStatementString("print(a)", "test1");
        c.addStatementString("while(i<=10): a = a * i;i=i+1", "test1");
        c.addStatementString("a=1;i=1", "test1");

        c.run("test1");

        assert c.getSymbols("test1").size() == 2;
        assert c.getOutput("test1").size() == 1;
        assert c.getOutput("test1").elementAt(0).equals("3628800");
    }
}
