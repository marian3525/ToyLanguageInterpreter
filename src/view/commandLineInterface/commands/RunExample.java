package view.commandLineInterface.commands;

import controller.Controller;
import exceptions.RepositoryException;
import exceptions.UndefinedOperationException;
import exceptions.UndefinedVariableException;

import java.io.IOException;

public class RunExample extends Command {
    private Controller controller;

    public RunExample(String key, String desc, Controller controller) {
        super(key, desc);
        this.controller = controller;
    }

    @Override
    public void execute() {
        try {
            controller.run("example");
        } catch (UndefinedVariableException e) {
            System.out.println("Undefined variable: " + e.getMessage());
        } catch (RepositoryException e) {
            System.out.println("Program Exception: " + e.getMessage());
        } catch (UndefinedOperationException e) {
            System.out.println("Undefined Operation: " + e.getMessage());
        } catch (NullPointerException npe) {
            System.out.println("Runtime exception: " + npe.getMessage());
        } catch (IOException ioe) {
            System.out.println("IOException: " + ioe.getMessage());
        }
    }
}