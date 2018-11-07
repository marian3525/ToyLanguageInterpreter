package view;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class UIByExample {
    private Map<String, Command> commands;

    public UIByExample() {
        commands = new HashMap<>();
    }

    public void addCommand(Command cmd) {
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
                commands.get(key).execute();
            } catch (NullPointerException npe) {
                System.out.println("Invalid cmd");
            }
        }
    }
}
