package view;

import controller.Controller;
import exceptions.ProgramException;
import exceptions.SyntaxException;
import exceptions.UndefinedOperationException;
import exceptions.UndefinedVariableException;
import model.expression.Expression;
import org.jetbrains.annotations.NotNull;

import java.util.Scanner;

public class UI {
    private Controller controller;

    public UI() {
        controller = new Controller();
        try {
            debug();
        } catch (ProgramException e) {
            e.printStackTrace();
        } catch (UndefinedVariableException e) {
            e.printStackTrace();
        } catch (UndefinedOperationException e) {
            e.printStackTrace();
        }
        //runUI();
    }

    private String readFromConsole(String msg) {
        System.out.print(msg);
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }
    private void showHelp() {
        String help="";
        help += "Usage:" +
                "\n1. add <car/moto/truck> <repairPrice> <id> | Add a new entry with the specified attributes" +
                "\n2. remove <id> | remove the entry with the given id" +
                "\n3. solve | solve the problem" +
                "\n4. print | print all entries" +
                "\n5. quit | exit the app" +
                "\n6. help";
        System.out.println(help);
    }

    private void readStatements(String input, String progName) throws SyntaxException, ProgramException, UndefinedVariableException, UndefinedOperationException {
        do {
            controller.addStatementString(input, progName);
            input = readFromConsole(progName + "::");
        }
        while(input.contains(";"));
        controller.addStatementString(input, progName);
    }

    private void runUI() {
        //main UI loop
        String progName = "";
        boolean quitting = false;
        String input;
        /*
            Program syntax: progName::=statement1;statement2;
                            progName::=a=2;
         */
        input = readFromConsole("ProgName: ");
        progName = input;
        controller.addEmptyProgram(progName);

        while(!quitting) {
            if(input.equals("quit")) {
                quitting = true;
            }

            if(input.equals("step")) {
                try {
                    controller.step(progName);
                }
                catch (UndefinedVariableException undefVarException) {
                    System.out.println(undefVarException.getMessage());
                    //undefVarException.printStackTrace();
                }
                catch (UndefinedOperationException undefOpException) {
                    System.out.println(undefOpException.getMessage());
                    //undefOpException.printStackTrace();
                }
                catch(ProgramException progException) {
                    System.out.println(progException.getMessage());
                    //progException.printStackTrace();
                }
            }
            if(input.equals("run")) {
                try {
                    controller.run(progName);
                } catch (UndefinedVariableException e) {
                    System.out.println(e.getMessage());
                    //e.printStackTrace();
                } catch (UndefinedOperationException e) {
                    System.out.println(e.getMessage());
                    //e.printStackTrace();
                } catch (ProgramException e) {
                    System.out.println(e.getMessage());
                    //e.printStackTrace();
                }

            }

        }
    }
    private void debug() throws ProgramException, UndefinedVariableException, UndefinedOperationException {
        String programName1 = "program1";
        String programName2 = "program2";

        String[] program1 = {"a=1;a=3"};

        controller.addEmptyProgram(programName1);
        try {
            for(String instruction : program1)
                controller.addStatementString(instruction, programName1);

            System.out.println("Stack:");
            for(String s : controller.getStackString(programName1)) {
                System.out.println(s);
            }
            System.out.println("\nSymbols:");

            for(String key : controller.getSymbols(programName1).keySet()) {
                System.out.println("VarName: " + key + ": " + controller.getSymbols(programName1).get(key));
            }
        } catch (SyntaxException e) {
            e.printStackTrace();
        } catch (ProgramException e) {
            e.printStackTrace();
        } catch (UndefinedVariableException e) {
            e.printStackTrace();
        } catch (UndefinedOperationException e) {
            e.printStackTrace();
        }

        while(controller.getStackString(programName1).size()!=0) {

            readFromConsole("");

            controller.step(programName1);

            System.out.println("Stack:");
            for(String s : controller.getStackString(programName1)) {
                System.out.println(s);
            }
            System.out.println("\nSymbols:");

            for(String key : controller.getSymbols(programName1).keySet()) {
                System.out.println("VarName: " + key + ": " + controller.getSymbols(programName1).get(key));
            }
        }
    }
}
