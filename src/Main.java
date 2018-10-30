import controller.Controller;
import exceptions.RepositoryException;
import exceptions.SyntaxException;
import view.commandLineInterface.CLIByExample;
import view.commandLineInterface.UI;
import view.commandLineInterface.commands.ExitCommand;
import view.commandLineInterface.commands.RunExample;

public class Main {

    private static void runCLIByExample() throws RepositoryException, SyntaxException {
        CLIByExample cli = new CLIByExample();
        cli.addCommand(new ExitCommand("1", "Exit"));

        Controller controller1 = new Controller();
        controller1.addEmptyProgram("example");

        controller1.addStatementString("print(a)", "example");
        controller1.addStatementString("readFile(a, a)", "example");
        controller1.addStatementString("openFile(a, example1.txt)", "example");

        cli.addCommand(new RunExample("ex1", "Read variable 'a' from file", controller1));
        cli.run();
    }

    private static void runUI() {
        UI ui = new UI();
        ui.start();
    }

    public static void main(String[] args) throws SyntaxException, RepositoryException {
        //runCLIByExample();
        runUI();
    }
}