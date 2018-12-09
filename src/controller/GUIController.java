package controller;

import exceptions.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import model.util.FileTable;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.stream.Collectors;

public class GUIController implements Initializable {
    ExecutionController executionController;

    //members to be injected
    @FXML
    private MenuBar menuBar;
    @FXML
    private TextArea historyTextArea;       //console where statements can be executed
    @FXML
    private TextField inputTextField;
    @FXML
    private ListView<String> heapList;
    @FXML
    private ListView<String> fileList;
    @FXML
    private ListView<String> stackList;
    @FXML
    private ListView<String> symbolList;
    @FXML
    private TextArea outputTextArea;
    @FXML
    private ComboBox<String> programComboBox;

    // execution flags, modified through commands starting with '!'
    private String progName = "";
    private boolean autorun = false;
    private boolean multithreaded = false;
    private boolean quiet = false;

    /**
     * Called before the @FXML annotated objects are injected.
     */
    public GUIController() {
        executionController = new ExecutionController();
    }

    /**
     * Called after @FXML objects are injected
     *
     * @param location
     * @param resources
     */
    public void initialize(URL location, ResourceBundle resources) {
        bindActions();
        configure();
    }

    /**
     * Bind the actions of each node to a method in the controller
     */
    private void bindActions() {
        menuBar.setOnContextMenuRequested(event -> System.out.println(event.toString()));
        inputTextField.setOnAction(event -> onConsoleLineEntered(inputTextField.getText()));
    }

    private void configure() {
        historyTextArea.setEditable(false);
        heapList.setEditable(false);
        fileList.setEditable(false);
        stackList.setEditable(false);
        symbolList.setEditable(false);
        outputTextArea.setEditable(false);
    }

    private void log(String message) {
        historyTextArea.appendText(">" + message + System.lineSeparator());
    }

    private void updateViews(String progName) {
        Map<Integer, Integer> heap;
        FileTable files;
        Vector<String> stack;
        Map<String, Integer> symbols;
        Vector<String> output;

        try {
            heap = executionController.getHeap(progName).getAll();
            files = executionController.getFiles(progName);
            stack = executionController.getStackString(progName);
            symbols = executionController.getSymbols(progName);
            output = executionController.getOutput(progName);

        } catch (RepositoryException e) {
            log(e.getMessage());
            return;
        }
        ObservableList<String> heapObs = FXCollections.observableArrayList(
                heap.entrySet().stream().map(e -> e.getKey().toString() + e.getValue().toString()).collect(Collectors.toList()));
        ObservableList<String> filesObs = FXCollections.observableArrayList(
                files.getAll().entrySet().stream().map(e -> e.getKey().toString() + "->" + e.getValue().toString()).collect(Collectors.toList()));

        ObservableList<String> stackObs = FXCollections.observableArrayList(stack);

        ObservableList<String> symbolsObs = FXCollections.observableArrayList(
                symbols.entrySet().stream().map(e -> e.getKey() + "->" + e.getValue().toString()).collect(Collectors.toList()));
        StringBuilder outputStr = new StringBuilder();
        for (String elem : output) {
            outputStr.append(elem).append(" ");
        }

        heapList.setItems(heapObs);
        fileList.setItems(filesObs);
        stackList.setItems(stackObs);
        symbolList.setItems(symbolsObs);
        outputTextArea.setText(outputStr.toString());
    }

    private void onConsoleLineEntered(String line) {
        log(line);
        inputTextField.clear();

        //check if the line is a statement or a command to the GUI
        String[] parts = line.split(" ");

        switch (line) {
            case "!quit":
                System.exit(0);
                return;
            case "!quiet":
                //toggle printing of the stack, symbols and output
                quiet = !quiet;
                log("Viewing set to " + quiet);
                break;
            case "!setprog":
                //set program name, name should be in parts[1]
                if (parts.length > 1)
                    if (parts[1].length() >= 1) {
                        progName = parts[1];
                        try {
                            executionController.addEmptyProgram(progName);
                        } catch (RepositoryException e) {
                            //the program with the given name already exists, so it was already created, do nothing
                            log("A program with the name: '" + progName + "' already exists");
                        }
                        return;
                    }
                log("Invalid program name");
                return;
            case "!autorun":
                //run the input automatically after each input line
                autorun = !autorun;
                log("Autorun set to " + autorun);
                return;
            case "!default":
                //default config: view true, progName = prog, autorun true
                quiet = false;
                multithreaded = false;
                progName = "prog";
                try {
                    executionController.addEmptyProgram(progName);
                } catch (RepositoryException e) {
                    log("Default program name taken, no new program was created");
                }
                autorun = true;
                return;
            case "!flags":
                log("Quiet: " + quiet);
                log("Autorun: " + autorun);
                log("Multithreading: " + multithreaded);
                return;
            case "!mt":
                multithreaded = !multithreaded;
                log("Multithreading set to " + multithreaded);
                return;
            case "!help":
                log("Use the Help menu for more info");
            case "!step":
                stepProgram(progName, quiet);
                return;
            case "!run":
                runProgram(progName, quiet);
                return;
        }

        //it is an instruction, add it to the current program
        try {
            executionController.addStatementString(line, progName);
            if (autorun) {
                runProgram(progName, quiet);
            }
            updateViews(progName);
        } catch (RepositoryException e) {
            log(e.getMessage());
        } catch (SyntaxException e) {
            log(e.getMessage());
        } catch (NullPointerException npe) {
            log("Parsing failed: " + npe.getMessage() + ". If you are trying to use configuration commands, make sure to use '!' in front of them");
        }
    }

    private void runProgram(String progName, boolean quiet) {
        try {
            log("Running program...");
            //pick the threading option
            if (multithreaded) {
                try {
                    executionController.runConcurrent();
                    // the completed programStates will be removed, add it again
                    executionController.addEmptyProgram(progName);
                } catch (InterruptedException e) {
                    log("Interrupted Exception");
                }
            } else
                executionController.run(progName);
            if (!quiet) {
                updateViews(progName);
            }
        } catch (UndefinedVariableException e) {
            log("Undefined variable: " + e.getMessage());
        } catch (RepositoryException e) {
            log("Program Exception: " + e.getMessage());
        } catch (UndefinedOperationException e) {
            log("Undefined Operation: " + e.getMessage());
        } catch (IOException ioe) {
            log("IOException: " + ioe.getMessage());
        } catch (SyntaxException e) {
            log("Syntax Exception: " + e.getMessage());
        }
    }

    private void stepProgram(String progName, boolean quiet) {
        try {
            System.out.println("Stepping to the next instruction");
            executionController.step(progName);
            if (!quiet) {
                updateViews(progName);
            }
        } catch (UndefinedVariableException e) {
            log("Undefined variable: " + e.getMessage());
        } catch (RepositoryException e) {
            log("Program Exception: " + e.getMessage());
        } catch (UndefinedOperationException e) {
            log("Undefined Operation: " + e.getMessage());
        } catch (NullPointerException npe) {
            log("Runtime exception: " + npe.getMessage());
        } catch (IOException ioe) {
            log("IOException: " + ioe.getMessage());
        } catch (SyntaxException e) {
            log("Syntax Exception: " + e.getMessage());
        } catch (ProgramException e) {
            log("Program Exception: " + e.getMessage());
        }
    }

}
