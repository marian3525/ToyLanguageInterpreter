package view;

import controller.Controller;
import exceptions.RepositoryException;
import exceptions.SyntaxException;
import exceptions.UndefinedOperationException;
import exceptions.UndefinedVariableException;

import java.io.IOException;

public class RunExample extends Command {
    private Controller controller;
    private String progName;

    public RunExample(String key, String desc, Controller controller, String progName) {
        super(key, desc);
        this.controller = controller;
        this.progName = progName;
    }

    @Override
    public void execute() {
        try {
            controller.run(progName);
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
        } catch (SyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void executeConcurrent() {
        try {
            controller.runConcurrent();
        } catch (RepositoryException e) {
            System.out.println("Program Exception: " + e.getMessage());
        } catch (NullPointerException npe) {
            System.out.println("Runtime exception: " + npe.getMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
