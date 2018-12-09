package tests;

import controller.ExecutionController;
import exceptions.*;
import model.expression.ConstantExpression;
import model.statement.CloseFileStatement;
import model.statement.OpenFileStatement;
import model.statement.ReadFileStatement;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;

public class FileOperationsTest {
    private final String path = "D:\\CS\\MAP\\ToyLanguageInterpreter\\outputFiles";
    private ExecutionController controller;
    private static int offset = 0; // needed for multiple tests execute in a row


    @Before
    public void setUp() throws IOException, RepositoryException {
        controller = new ExecutionController();
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
            //After the first run, all files are closed and other asserts will give erroneous results
            // use step instead of run so that the files are not closed
            controller.addStatementString("openFile(a, testFile1.txt)", "test1");
            controller.step("test1");
            //check the UID of the new opened file
            assert controller.getSymbols("test1").get("a") == 1 + offset;

            // read from an empty file, should read 0
            controller.addStatementString("readFile(" + String.valueOf(1 + offset) + ", a)", "test1");
            controller.step("test1");
            assert controller.getSymbols("test1").get("a") == 0;

            controller.addStatementString("closeFile(" + String.valueOf(1 + offset) + ")", "test1");
            controller.run("test1");
            //check that the file with the UID 1 has been removed from the fileTable
            assert controller.getFiles("test1").getAll().get(1) == null;

        } catch (SyntaxException | IOException | RepositoryException | UndefinedVariableException | UndefinedOperationException | ProgramException e) {
            assert false;
        }

        //put some test data into test2.txt
        PrintWriter w = new PrintWriter(new BufferedWriter(new FileWriter(path + "\\testFile2.txt", false)));
        w.println("123");
        w.close();

        try {
            controller.addStatementString("openFile(a, testFile2.txt)", "test2");
            controller.step("test2");
            //check the UID of the new opened file
            assert controller.getSymbols("test2").get("a") == 2 + offset;

            // read from the prev. created file, should be 123
            controller.addStatementString("readFile(" + String.valueOf(2 + offset) + ", a)", "test2");
            controller.step("test2");
            assert controller.getSymbols("test2").get("a") == 123;

            controller.addStatementString("closeFile(" + String.valueOf(2 + offset) + ")", "test2");
            controller.run("test2");
            //check that the file with the UID 1 has been removed from the fileTable
            assert controller.getFiles("test2").getAll().get(2 + offset) == null;
            offset += 2;

        } catch (SyntaxException | IOException | RepositoryException | UndefinedVariableException | UndefinedOperationException | ProgramException e) {
            assert false;
        }
    }

    @After
    public void tearDown() {
        // delete the created test files
        new File(path + "\\testFile1.txt").delete();
        new File(path + "\\testFile2.txt").delete();
    }
}
