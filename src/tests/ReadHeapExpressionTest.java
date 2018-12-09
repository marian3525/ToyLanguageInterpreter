package tests;

import controller.ExecutionController;
import exceptions.RepositoryException;
import exceptions.SyntaxException;
import exceptions.UndefinedOperationException;
import exceptions.UndefinedVariableException;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class ReadHeapExpressionTest {
    ExecutionController c;

    @Before
    public void setUp() {
        c = new ExecutionController();
    }

    @Test
    public void testEvaluate() throws RepositoryException, SyntaxException, UndefinedVariableException, IOException, UndefinedOperationException {
        c.addEmptyProgram("test");
        c.addStatementString("print(1+readHeap(a))", "test");
        c.addStatementString("new(a, 10)", "test");
        c.run("test");
        assert Integer.valueOf(c.getOutput("test").elementAt(0)) == 1 + 10;
    }
}