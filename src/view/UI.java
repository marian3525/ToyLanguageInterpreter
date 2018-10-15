package view;

import controller.Controller;
import exceptions.ProgramException;
import exceptions.UndefinedOperationException;
import exceptions.UndefinedVariableException;
import org.jetbrains.annotations.NotNull;

import java.util.Scanner;

public class UI {
    private Controller controller;

    public UI(@NotNull Controller controller) {
        this.controller = controller;
        runUI();
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

    private void runUI() {
        //main UI loop
        String progName = "prog";
        boolean quitting = false;
        String input;

        while(!quitting) {
            input = readFromConsole(">>>");

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

            //read program line and wait for the next one if the current line
            
        }
    }
}
