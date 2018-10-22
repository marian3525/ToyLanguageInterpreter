package tests;

import controller.Controller;
import exceptions.RepositoryException;
import exceptions.SyntaxException;
import exceptions.UndefinedOperationException;
import exceptions.UndefinedVariableException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ControllerTest {
    Controller c;

    @Before
    public void setUp() {
        c = new Controller();
    }

    @After
    public void tearDown() {
    }

    @Test
    public void step() throws UndefinedVariableException, UndefinedOperationException, SyntaxException, RepositoryException {
        Controller controller = new Controller();
        controller.addEmptyProgram("test");

        assert(controller.getOutput("test").size()==0);
        assert(controller.getSymbols("test").size()==0);
        assert(controller.getStackString("test").size()==0);

        controller.addStatementString("a=2", "test");

        assert (controller.getStackString("test").size()==1);

        controller.step("test");

        assert(controller.getSymbols("test").size()==1);
        assert(controller.getSymbols("test").get("a") == 2);
        assert (controller.getStackString("test").size()==0);

        controller.addStatementString("print(a+1)", "test");

        assert(controller.getStackString("test").size()==1);
        controller.step("test");
        assert(controller.getOutput("test").size()==1);
        assert (controller.getOutput("test").elementAt(0).equals(String.valueOf(3)));

        controller.addStatementString("b=a*(3+1)", "test");
        assert controller.getStackString("test").size() != 0;
        controller.step("test");
        assert(controller.getSymbols("test").get("b") == 8);

        //now with number with 2+ digits to check if the parser transforms to postfix correctly
        controller.addStatementString("c=b*(13+21)", "test");
        assert controller.getStackString("test").size() != 0;
        controller.step("test");
        assert(controller.getSymbols("test").get("c") == 272);
    }

    @Test
    public void addEmptyProgram() {
        Controller c = new Controller();
        //attempt to get output of nonexistent program, should throw
        try {
            assert c.getOutput("prog") != null;
        } catch (RepositoryException ex) {
            assert true;
        }

        try {
            c.addEmptyProgram("prog");
            assert true;
        } catch (RepositoryException e) {
            assert false;
        }
        //added program, should not throw now
        try {
            assert c.getOutput("prog") != null;
        } catch (RepositoryException ex) {
            assert false;
        }
    }

    @Test
    public void addStatementString() {
    }

    @Test
    public void getStackString() {
    }

    @Test
    public void getOutput() {
    }

    @Test
    public void getSymbols() {
    }
}
