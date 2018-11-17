package tests;

import controller.Controller;
import exceptions.RepositoryException;
import exceptions.SyntaxException;
import exceptions.UndefinedOperationException;
import exceptions.UndefinedVariableException;
import model.adt.Heap;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class NewEntryStatementTest {
    Controller c;

    @Before
    public void setUp() {
        c = new Controller();
    }

    @Test
    public void testEntryStatement() throws RepositoryException, SyntaxException, UndefinedVariableException, IOException, UndefinedOperationException {
        c.addEmptyProgram("test");

        c.addStatementString("new(a, 11)", "test");
        c.addStatementString("a=10", "test");

        c.run("test");

        Heap h = c.getHeap("test");

        assert h.get(1) == 11;
        assert c.getSymbols("test").get("a") == 1;

        //change the key to trigger a gc action
        c.addStatementString("a=2", "test");
        c.run("test");

        h = c.getHeap("test");
        assert h.get(0) == null;
        assert h.getAll().size() == 0;
    }
}
