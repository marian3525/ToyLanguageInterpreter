package tests;

import controller.ExecutionController;
import exceptions.RepositoryException;
import exceptions.SyntaxException;
import exceptions.UndefinedOperationException;
import exceptions.UndefinedVariableException;
import model.adt.Heap;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class WriteHeapStatementTest {
    ExecutionController c;

    @Before
    public void setUp() {
        c = new ExecutionController();
    }

    @Test
    public void testWriteHeap() throws RepositoryException, SyntaxException, UndefinedVariableException, IOException, UndefinedOperationException {
        c.addEmptyProgram("test");
        c.addStatementString("writeHeap(a, 11)", "test");
        c.addStatementString("a=0", "test");
        //attempt to write to an undefined location in the heap, should throw
        try {
            c.run("test");
            assert false;
        } catch (UndefinedOperationException uoe) {
            assert true;
        }

        c.addStatementString("new(a, 11)", "test");
        c.run("test");

        Heap h = c.getHeap("test");
        assert h.get(c.getSymbols("test").get("a")) != null;
        assert h.get(c.getSymbols("test").get("a")) == 11;
        c.addStatementString("b=readHeap(" + c.getSymbols("test").get("a").toString() + ")", "test");
        c.run("test");
        assert c.getHeap("test").get(c.getSymbols("test").get("b")) == 11;

    }
}
