import exceptions.RepositoryException;
import exceptions.SyntaxException;
import view.UI;
import view.cli.CLI;
import view.cli.CLIByExample;
import view.gui.GUI;

public class Main {


    /**
     * Start the interpreter in either CLI or GUI mode
     *
     * @param args: <cli>, <gui> or <example> to pick the UI mode
     * @throws SyntaxException
     * @throws RepositoryException
     */
    public static void main(String[] args) throws SyntaxException, RepositoryException {
        UI ui;
        if (args.length == 0) {
            ui = new CLI();
        } else if (args.length == 1 && args[0].equals("cli")) {
            ui = new CLI();
        } else if (args.length == 1 && args[0].equals("gui")) {
            ui = new GUI();
        } else if (args.length == 1 && args[0].equals("example")) {
            ui = new CLIByExample();
        } else {
            System.out.println("Invalid argument: " + args);
            return;
        }
        ui.run();
    }
}