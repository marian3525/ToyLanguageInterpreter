package tests;

import controller.Controller;
import exceptions.RepositoryException;
import exceptions.SyntaxException;
import exceptions.UndefinedOperationException;
import exceptions.UndefinedVariableException;
import model.expression.ConstantExpression;
import model.statement.CloseFileStatement;
import model.statement.OpenFileStatement;
import model.statement.ReadFileStatement;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;

public class FileOperationsTest {
    private final String path = "";
    private Controller controller;

    @Before
    public void setUp() throws IOException, RepositoryException {
        controller = new Controller();
        //create 2 test files
        new BufferedWriter(new FileWriter(path + "\\" + "testFile1.txt")).close();
        new BufferedWriter(new FileWriter(path + "\\" + "testFile2.txt")).close();
        controller.addEmptyProgram("test1");
        controller.addEmptyProgram("test2");
    }

    @Test
    public void testToString() {
        CloseFileStatement closeFileStatement1 = new CloseFileStatement(new ConstantExpression(1));
        CloseFileStatement closeFileStatement2 = new CloseFileStatement(new ConstantExpression(2));
        assert closeFileStatement1.toString().equals("Close file: 1");
        assert closeFileStatement2.toString().equals("Close file: 2");

        OpenFileStatement openFileStatement1 = new OpenFileStatement("a", "test3.txt");
        OpenFileStatement openFileStatement2 = new OpenFileStatement("a", "test4.txt");
        assert openFileStatement1.toString().equals("Open file: test3.txt");
        assert openFileStatement2.toString().equals("Open file: test4.txt");

        ReadFileStatement readFileStatement1 = new ReadFileStatement(new ConstantExpression(1), "a");
        ReadFileStatement readFileStatement2 = new ReadFileStatement(new ConstantExpression(1), "b");
        assert readFileStatement1.toString().equals("Read from file: " + 1 + " into var: " + "a");
        assert readFileStatement2.toString().equals("Read from file: " + 1 + " into var: " + "b");
    }

    @Test
    public void testExecute() throws IOException {
        try {
            controller.addStatementString("openFile(a, testFile1.txt", "test1");
            controller.run("test1");
            //check the UID of the new opened file
            assert controller.getSymbols("test1").get("a") == 1;

            // read from an empty file, should read 0
            controller.addStatementString("readFile(1, a)", "test1");
            controller.run("test1");
            assert controller.getSymbols("test1").get("a") == 0;

            controller.addStatementString("closeFile(1)", "test1");
            controller.run("test1");
            //check that the file with the UID 1 has been removed from the fileTable
            assert controller.getFiles("test1").getAll().get(1) == null;

        } catch (SyntaxException | IOException | RepositoryException | UndefinedVariableException | UndefinedOperationException e) {
            assert false;
        }

        //put some test data into test2.txt
        PrintWriter w = new PrintWriter(new BufferedWriter(new FileWriter(path + "outputFiles/programCreatedFiles/testFile2.txt", false)));
        w.println("123");
        w.close();

        try {
            controller.addStatementString("openFile(a, testFile2.txt)", "test2");
            controller.run("test2");
            //check the UID of the new opened file
            assert controller.getSymbols("test2").get("a") == 2;

            // read from the prev. created file, should be 123
            controller.addStatementString("readFile(2, a)", "test2");
            controller.run("test2");
            assert controller.getSymbols("test2").get("a") == 123;

            controller.addStatementString("closeFile(2)", "test2");
            controller.run("test2");
            //check that the file with the UID 1 has been removed from the fileTable
            assert controller.getFiles("test2").getAll().get(2) == null;

        } catch (SyntaxException | IOException | RepositoryException | UndefinedVariableException | UndefinedOperationException e) {
            assert false;
        }
    }

    @After
    public void tearDown() {
        // delete the created test files
        new File(path + "outputFiles/programCreatedFiles/testFile1.txt").delete();
        new File(path + "outputFiles/programCreatedFiles/testFile2.txt").delete();
    }
}
