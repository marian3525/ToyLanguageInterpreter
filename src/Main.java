import controller.Controller;
import exceptions.RepositoryException;
import exceptions.SyntaxException;
import view.*;

public class Main {

    private static void runUIExample() throws RepositoryException, SyntaxException {
        UIByExample ui = new UIByExample();

        Controller controller = new Controller();
        controller.addEmptyProgram("example1");
        controller.addStatementString("print(a)", "example1");
        controller.addStatementString("closeFile(1)", "example1");
        controller.addStatementString("readFile(a,a)", "example1");
        controller.addStatementString("openFile(a,file.txt)", "example1");

        controller.addEmptyProgram("example2");
        controller.addStatementString("closeFile(var_f)", "example2");
        controller.addStatementString("if var_c then readFile(var_f,var_c);print(var_c) else print(0)"
                , "example2");
        controller.addStatementString("readFile(var_f,var_c);print(var_c)", "example2");
        controller.addStatementString("openFile(var_f,file.txt)", "example2");

        Command c1 = new ExitCommand("1", "Exit the app");
        Command c2 = new RunExample("2", "Read an int from file.txt", controller, "example1");
        Command c3 = new RunExample("3", "Read with if", controller, "example2");

        ui.addCommand(c1);
        ui.addCommand(c2);
        ui.addCommand(c3);
        ui.run();
    }

    private static void runUI() {
        UI ui = new UI();
        ui.start();
    }

    public static void main(String[] args) {
        //runUIExample();
        runUI();
    }
}