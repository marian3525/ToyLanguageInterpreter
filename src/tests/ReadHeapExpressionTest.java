package tests;

import controller.Controller;
import exceptions.RepositoryException;
import exceptions.SyntaxException;
import exceptions.UndefinedOperationException;
import exceptions.UndefinedVariableException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

class ReadHeapExpressionTest {
    Controller c;

    @BeforeEach
    void setUp() {
        c = new Controller();
    }

    @Test
    void testEvaluate() throws RepositoryException, SyntaxException, UndefinedVariableException, IOException, UndefinedOperationException {
        c.addEmptyProgram("test");
        c.addStatementString("print(1+readHeap(a))", "test");
        c.addStatementString("new(a, 10)", "test");
        c.run("test");
        assert Integer.valueOf(c.getOutput("test").elementAt(0)) == 1 + 10;
    }
}