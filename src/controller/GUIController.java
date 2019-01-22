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
import java.util.stream.Collectors;

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
    private Label progStatesCount;
    @FXML
    private ListView<String> progStatesList;
    @FXML
    private Button runButton;
    @FXML
    private Button stepButton;

    // execution flags, modified through commands starting with '!'
    private String currentProg = "main";
    private boolean autorun = false;
    private boolean multithreaded = true;
    private boolean quiet = false;

    private SelectionController selectionController;
    /**
     * Called before the @FXML annotated objects are injected.
     */
    public GUIController() {
        this.executionController = new ExecutionController(this);
    }

    /**
     * Called after @FXML objects are injected and the constructor is called
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
        progStatesList.setEditable(false);

        // configure the buttons
        runButton.setOnAction(event -> runProgram(currentProg, false));
        stepButton.setOnAction(event -> stepProgram(currentProg, false));

        // list click handler!
        progStatesList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        progStatesList.setOnMouseClicked(mouseEvent -> {
            currentProg = progStatesList.getSelectionModel().getSelectedItem();
            updateViews(currentProg);
        });

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
     * Called form the selector window controller when a program state is selected
     * @param currentProgram
     */
    void setCurrentProgram(String currentProgram) {
        this.currentProg = currentProgram;
        updateViews(currentProgram);
    }

    /**
     * Update the views if the currently shown program is progName
     * Else, do nothing
     *
     * @param progName the program whose data needs to be updated
     */
    private void updateViews(String progName) {

        if(!progName.equals(progName))
            return;
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
        // set the program states list for the other window
        Set<String> states = executionController.getAllStates().keySet().stream().map(key ->
        {
            try {
                if(executionController.getAllStates().size() != 0)
                    return key + ":" + executionController.getStackString(key);
                else
                    return "";
            } catch (RepositoryException e) {
                // can't happen, using only existing keys to access the stacks
                return "";
            }
        }).collect(Collectors.toSet());
        selectionController.setPrograms(states);

        progStatesCount.setText(String.valueOf(progStates.size()));
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
                    executionController.addEmptyProgram("main");
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
        try {
            executionController.addStatementString(line, "main");
            if (autorun) {
                runProgram("main", quiet);
            }
            updateViews("main");
        } catch (RepositoryException e) {
            log(e.getMessage());
        } catch (SyntaxException e) {
            log(e.getMessage());
        } catch (NullPointerException npe) {
            log("Parsing failed: " + npe.getMessage() + ". If you are trying to use configuration commands, make sure to use '!' in front of them");
        } catch (ArrayIndexOutOfBoundsException eioobe) {
            log("Parsing failed: " + eioobe.getMessage());
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
            if(!multithreaded)
                executionController.step(progName);
            else
                executionController.stepOnAll(new Vector<>(executionController.getAllStates().keySet()));
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
        } catch (InterruptedException e) {
            e.printStackTrace();
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

    /**
     *
     * @param selectionController: controller of the selection window
     */
    public void setSelectorController(SelectionController selectionController) {
        this.selectionController = selectionController;

        // populate the states after all references are not null
        executionController.populateStates();
    }
}
