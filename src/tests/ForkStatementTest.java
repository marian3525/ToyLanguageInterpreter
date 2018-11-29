package tests;

import controller.Controller;
import exceptions.*;
import model.expression.ConstantExpression;
import model.programState.ProgramState;
import model.statement.AssignmentStatement;
import model.statement.ForkStatement;
import model.statement.PrintStatement;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;

public class ForkStatementTest {
    Controller c;

    @Before
    public void setUp() throws RepositoryException {
        c = new Controller();
        c.addEmptyProgram("test");
    }

    @Test
    public void testToString() {
        AssignmentStatement a = new AssignmentStatement("a", new ConstantExpression(3));
        ForkStatement f = new ForkStatement(a);
        assert f.toString().equals("fork(a = 3)");
    }

    @Test
    public void testExecute() {
        PrintStatement printStatement = new PrintStatement(new ConstantExpression(3));
        ForkStatement f = new ForkStatement(printStatement);
        ProgramState state = new ProgramState();

        state.getExecutionStack().push(f);
        try {
            ProgramState newState = state.step();
            assert newState != state;
            assert newState.getExecutionStack().peek() == printStatement;

        } catch (ProgramException | UndefinedVariableException | SyntaxException | UndefinedOperationException | IOException e) {
            assert false;
        }
    }

    @Test
    public void testExecuteThroughController() throws RepositoryException, SyntaxException, UndefinedVariableException, IOException, UndefinedOperationException, InterruptedException {
        Controller c = new Controller();
        c.addEmptyProgram("test");
        ArrayList<String> progs = new ArrayList<>();
        progs.add("test");

        c.addStatementString("fork(a=11;print(a))", "test");
        c.addStatementString("a=12;print(a)", "test");
        assert c.getAllStates().size() == 1;
        c.runConcurrent();
    }
}