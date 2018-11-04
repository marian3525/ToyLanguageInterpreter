package view;

import controller.Controller;
import exceptions.RepositoryException;
import exceptions.SyntaxException;
import exceptions.UndefinedOperationException;
import exceptions.UndefinedVariableException;

import java.io.IOException;
import java.util.Scanner;

public class UI {
    private Controller controller;

    public UI() {
        controller = new Controller();
    }

    public void start() {
        runUI();
    }

    private String readFromConsole(String msg) {
        System.out.print(msg);
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }

    private void printHelp() {
        StringBuilder b = new StringBuilder();
        b.append("Mini manual: How to use the TPL\n" +
                "Everything is case sensitive\n" +
                "Syntax:\n" +
                "*declare a variable: a=1 OR varName=12*7+(1-1) OR a=b (if b is already defined)\n" +
                "*compound statement in one line: a=12+7;b=1+a (the statements will be executed in this order)\n" +
                "*print statement: print(a) OR print(a*9-17*(b+7))\n" +
                "*if statement: if a then print(a) else print(b) (execute then branch if a != 0)\n" +
                "*file operations:\n" +
                "                  -open a file: openFile(<variable to store the descriptor>, <filename>)\n" +
                "                              Note: filename doesn't contain \"\n" +
                "                  -read an integer from a file: readFile(<descriptor>, <var to read into>)\n" +
                "                  -close a file: closeFile(<descriptor>)\n"
        );
        System.out.println(b.toString());
    }

    private void printInternals(String progName) {
        try {
            System.out.println("Stack:");
            for (String s : controller.getStackString(progName)) {
                System.out.println(s);
            }
        } catch (RepositoryException e) {
            System.out.println(e.toString());
        }
        try {
            System.out.println("\nSymbols:");
            for (String key : controller.getSymbols(progName).keySet()) {
                System.out.println("VarName: " + key + ": " + controller.getSymbols(progName).get(key));
            }
        } catch (RepositoryException e) {
            System.out.println(e.toString());
        }
        try {
            System.out.println("\nOutput:");
            for (String o : controller.getOutput(progName)) {
                System.out.println(o);
            }
        } catch (RepositoryException e) {
            System.out.println(e.toString());
        }
    }

    private void runUI() {
        boolean quitting = false;
        String progName = "";
        boolean printing = false;
        boolean autorun = false;

        while (!quitting) {
            String cmd = readFromConsole("(" + progName + ")" + ">");
            String[] parts = cmd.split(" ");
            if (cmd.endsWith(" ")) {
                //cmd = cmd.replace(" ", "");
            }
            //the first word is the command
            switch (parts[0]) {
                case "quit":
                    quitting = true;
                    continue;

                case "view":
                    //toggle printing of the stack, symbols and output
                    printing = !printing;
                    System.out.println("Viewing set to " + printing);
                    continue;

                case "setprog":
                    //set program name, name should be in parts[1]
                    if (parts.length > 1)
                        if (parts[1].length() >= 1) {
                            progName = parts[1];
                            try {
                                controller.addEmptyProgram(progName);
                            } catch (RepositoryException e) {
                                //the program with the given name already exists, so it was already created, do nothing
                            }
                            continue;
                        }
                    System.out.println("Invalid program name");
                    continue;
                case "autorun":
                    //run the input automatically after each input line
                    autorun = !autorun;
                    System.out.println("Autorun set to " + autorun);
                    continue;
                case "default":
                    //default config: view true, progName = prog, autorun true
                    printing = true;
                    progName = "prog";
                    try {
                        controller.addEmptyProgram(progName);
                    } catch (RepositoryException e) {
                        System.out.println("Default program name taken, no new program was created");
                    }
                    autorun = true;
                    continue;
                case "flags":
                    System.out.println("Viewing: " + printing);
                    System.out.println("Autorun: " + autorun);
                    continue;
                case "help":
                    printHelp();
                    continue;
                case "step":
                    stepProgram(progName, printing);
                    continue;
                case "run":
                    runProgram(progName, printing);
                    continue;
            }
            //it is an instruction, add it to the current program
            try {
                controller.addStatementString(cmd, progName);
                if (autorun) {
                    runProgram(progName, printing);
                }
            } catch (RepositoryException e) {
                System.out.println(e.getMessage());
            } catch (SyntaxException e) {
                System.out.println(e.getMessage());
            } catch (NullPointerException npe) {
                System.out.println("Parsing failed: " + npe.getMessage());
            }
        }
    }

    private void runProgram(String progName, boolean printing) {
        try {
            System.out.println("Running program...");
            controller.run(progName);
            if (printing) {
                printInternals(progName);
            }
        } catch (UndefinedVariableException e) {
            System.out.println("Undefined variable: " + e.getMessage());
        } catch (RepositoryException e) {
            System.out.println("Program Exception: " + e.getMessage());
        } catch (UndefinedOperationException e) {
            System.out.println("Undefined Operation: " + e.getMessage());
        } catch (IOException ioe) {
            System.out.println("IOException: " + ioe.getMessage());
        } catch (SyntaxException e) {
            e.printStackTrace();
        }
    }

    private void stepProgram(String progName, boolean printing) {
        try {
            System.out.println("Stepping to the next instruction");
            controller.step(progName);
            if (printing) {
                printInternals(progName);
            }
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
}
