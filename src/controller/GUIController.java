package controller;

import exceptions.*;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import model.adt.Pair;
import model.util.FileTable;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Vector;

public class GUIController implements Initializable, model.util.Observer {

    private ExecutionController executionController;

    //members to be injected
    @FXML
    private MenuBar menuBar;
    @FXML
    private TextArea historyTextArea;       //console where statements can be executed
    @FXML
    private TextField inputTextField;
    @FXML
    private TableView<Map.Entry<Integer, Integer>> heapTable;
    @FXML
    private TableView<Map.Entry<Integer, Pair<String, BufferedReader>>> fileTable;
    @FXML
    private TableView<Map.Entry<String, Integer>> symbolTable;
    @FXML
    private ListView<String> executionList;
    @FXML
    private ListView<String> outputList;
    @FXML
    private TextField progStatesCount;
    @FXML
    private ListView<String> progStatesList;
    @FXML
    private Button runButton;
    @FXML
    private Button stepButton;

    // execution flags, modified through commands starting with '!'
    private String currentProg = "";
    private boolean autorun = false;
    private boolean multithreaded = false;
    private boolean quiet = false;

    /**
     * Called before the @FXML annotated objects are injected.
     */
    public GUIController() {
        //create the execution controller and register this object on the Observers list
        executionController = new ExecutionController(this);
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
        update();
    }

    /**
     * Bind the actions of each node to a method in the controller
     */
    private void bindActions() {
        menuBar.setOnContextMenuRequested(event -> System.out.println(event.toString()));
        inputTextField.setOnAction(event -> onConsoleLineEntered(inputTextField.getText()));
    }

    @SuppressWarnings("unchecked")
    private void configure() {
        // output only views
        historyTextArea.setEditable(false);
        heapTable.setEditable(false);
        fileTable.setEditable(false);
        symbolTable.setEditable(false);
        executionList.setEditable(false);
        outputList.setEditable(false);
        progStatesCount.setEditable(false);
        progStatesList.setEditable(false);

        // configure the buttons
        runButton.setOnAction(event -> runProgram(currentProg, false));
        stepButton.setOnAction(event -> stepProgram(currentProg, false));

        progStatesList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        //configure the tables with the required columns

        // config for the heapTable
        TableColumn<Map.Entry<Integer, Integer>, Integer> addressColumn = new TableColumn<>("Address");
        addressColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().getKey()));

        TableColumn<Map.Entry<Integer, Integer>, Integer> valueColumn = new TableColumn<>("Value");
        valueColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().getValue()));

        heapTable.getColumns().addAll(addressColumn, valueColumn);

        // config the file table
        TableColumn<Map.Entry<Integer, Pair<String, BufferedReader>>, Integer> descriptorColumn = new TableColumn<>("Descriptor");
        descriptorColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().getKey()));

        TableColumn<Map.Entry<Integer, Pair<String, BufferedReader>>, String> fileColumn = new TableColumn<>("File");
        fileColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().getValue().getKey()));

        fileTable.getColumns().addAll(descriptorColumn, fileColumn);

        // symbol table
        TableColumn<Map.Entry<String, Integer>, String> variableNameColumn = new TableColumn("Variable Name");
        variableNameColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().getKey()));

        TableColumn<Map.Entry<String, Integer>, Integer> varValueColumn = new TableColumn("Value");
        varValueColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().getValue()));

        symbolTable.getColumns().addAll(variableNameColumn, varValueColumn);
    }

    private void log(String message) {
        historyTextArea.appendText(">" + message + System.lineSeparator());
    }

    /**
     * Update the views if the currently shown program is progName
     * Else, do nothing
     *
     * @param progName the program whose data needs to be updated
     */
    private void updateViews(String progName) {

        // variables to store the updated state of the current program
        Map<Integer, Integer> heap;
        FileTable files;
        Vector<String> stack;
        Map<String, Integer> symbols;
        Vector<String> output;
        Set<String> progStates;
        // attempt to get updated values
        try {
            heap = executionController.getHeap(progName).getAll();
            files = executionController.getFiles(progName);
            stack = executionController.getStackString(progName);
            symbols = executionController.getSymbols(progName);
            output = executionController.getOutput(progName);
            progStates = executionController.getAllStates().keySet();
        } catch (RepositoryException e) {
            log(e.getMessage());
            return;
        }
        currentProg = progStatesList.getSelectionModel().getSelectedItem();

        heapTable.setItems(FXCollections.observableArrayList(heap.entrySet()));
        heapTable.refresh();

        fileTable.setItems(FXCollections.observableArrayList(files.getAll().entrySet()));
        fileTable.refresh();

        symbolTable.setItems(FXCollections.observableArrayList(symbols.entrySet()));
        symbolTable.refresh();

        executionList.setItems(FXCollections.observableArrayList(stack));
        executionList.refresh();

        outputList.setItems(FXCollections.observableArrayList(output));
        outputList.refresh();

        progStatesList.setItems(FXCollections.observableArrayList(progStates));
        progStatesList.refresh();
        progStatesCount.textProperty().setValue(String.valueOf(progStates.size()));
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
                        currentProg = parts[1];
                        try {
                            executionController.addEmptyProgram(currentProg);
                        } catch (RepositoryException e) {
                            //the program with the given name already exists, so it was already created, do nothing
                            log("A program with the name: '" + currentProg + "' already exists");
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
                currentProg = "prog";
                try {
                    executionController.addEmptyProgram(currentProg);
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
                log("Multi-threading set to " + multithreaded);
                return;
            case "!help":
                log("Use the Help menu for more info");
            case "!step":
                stepProgram(currentProg, quiet);
                return;
            case "!run":
                runProgram(currentProg, quiet);
                return;
        }

        //it is an instruction, add it to the current program
        //TODO, currentprog is null on fork()
        try {
            executionController.addStatementString(line, currentProg);
            if (autorun) {
                runProgram(currentProg, quiet);
            }
            updateViews(currentProg);
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

    /**
     * Called form the Observable Repo when changed occur
     */
    @Override
    public void update() {
        for (String progName : executionController.getRepo().getPrograms().keySet()) {
            log("update from Observable received");
            updateViews(progName);
        }
    }
}
