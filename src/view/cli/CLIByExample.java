package view.cli;

import controller.ExecutionController;
import exceptions.RepositoryException;
import exceptions.SyntaxException;
import view.UI;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class CLIByExample implements UI {
    private Map<String, Command> commands;
    private ExecutionController controller;

    public CLIByExample() throws RepositoryException, SyntaxException {
        commands = new HashMap<>();

        ExecutionController controller = new ExecutionController();
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

        ExecutionController controllerThreaded = new ExecutionController();
        controllerThreaded.addEmptyProgram("threadsMain");
        controllerThreaded.addStatementString("print(readHeap(a))", "threadsMain");
        controllerThreaded.addStatementString("print(a);a=a;a=a;a=a", "threadsMain");
        controllerThreaded.addStatementString("fork(a=2;print(a);new(addr, 10))", "threadsMain");
        controllerThreaded.addStatementString("a=1;print(a)", "threadsMain");

        ExecutionController controllerWhile = new ExecutionController();
        controllerWhile.addEmptyProgram("forkthread");
        //controllerWhile.addStatementString("while(i<10): fork(print(i));i=i+1", "forkthread");
        controllerWhile.addStatementString("i=0", "forkthread");


        Command c1 = new ExitCommand("1", "Exit the app");
        Command c2 = new RunExample("2", "Read an int from file.txt", controller, "example1");
        Command c3 = new RunExample("3", "Read with if", controller, "example2");
        Command c4 = new RunExample("4", "threads example", controllerThreaded, "threadsMain");
        Command c5 = new RunExample("5", "while fork", controllerWhile, "forkthread");

        this.addCommand(c1);
        this.addCommand(c2);
        this.addCommand(c3);
        this.addCommand(c4);
        this.addCommand(c5);
        System.out.println("Started CLI example");
    }

    private void addCommand(Command cmd) {
        commands.put(cmd.getKey(), cmd);
    }

    private void printMenu() {
        for (Command cmd : commands.values()) {
            String line = String.format("%s : %s", cmd.getKey(), cmd.getDescription());
            System.out.println(line);
        }
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            printMenu();
            System.out.println("Input the option");
            String key = scanner.nextLine();
            try {
                //use the run concurrent method from the controller
                if (commands.get(key).getDescription().contains("thread")) {
                    commands.get(key).executeConcurrent();
                } else {
                    commands.get(key).execute();
                }
            } catch (NullPointerException npe) {
                System.out.println("Invalid cmd");
            }
        }
    }
}
