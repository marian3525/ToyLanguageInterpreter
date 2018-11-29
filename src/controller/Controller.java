package controller;

import exceptions.*;
import javafx.util.Pair;
import model.adt.Heap;
import model.programState.ProgramState;
import model.statement.AbstractStatement;
import model.util.FileTable;
import org.jetbrains.annotations.NotNull;
import parsers.StatementParser;
import repository.Repository;
import repository.RepositoryInterface;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;


public class Controller {
    private RepositoryInterface repo;
    private ExecutorService executorService;

    public Controller() {
        repo = new Repository();
    }

    /**
     * The execution will continue with the last instruction on top of the execution stack each time
     * step() is called
     * @param progName: the name of the program to be run. The program must exist in memory
     * @throws ProgramException if the specified can't be run because it doesn't exist
     * @throws UndefinedOperationException  if an operation without definition is encountered
     * @throws UndefinedVariableException   if an previously undefined variable is found on the rhs of an expression
     */
    public void step(String progName) throws RepositoryException, UndefinedOperationException, UndefinedVariableException, IOException, SyntaxException, ProgramException {
        ProgramState state = repo.getProgramByName(progName);

        state.step();

        //gc
        Collection<Integer> values = state.getSymbols().values();
        Map<Integer, Integer> all = state.getHeap().getContent();

        Map<Integer, Integer> cleanHeap = gc(values, all);
        state.getHeap().setContent(cleanHeap);

        repo.logProgramState(state);
    }

    /**
     * Run all the instructions in the current program
     * @param progName the name of the program to be run
     * @throws ProgramException if the specified program can't be run because it doesn't exist
     * @throws UndefinedOperationException  if an operation without definition is encountered
     * @throws UndefinedVariableException   if an previously undefined variable is found on the rhs of an expression
     */
    public void run(String progName) throws RepositoryException, UndefinedVariableException, UndefinedOperationException, IOException, SyntaxException {
        ProgramState state = repo.getProgramByName(progName);
        while (true) {
            try {
                step(progName);
            } catch (ProgramException pe) {
                //end of program reached
                //clean up the files left opened
                //closeFiles(progName);
                break;
            }
        }
    }

    private void stepOnAll(List<String> progNames) throws InterruptedException, RepositoryException {
        // needed for testing where stepOnAll is required, but not runAll
        if (executorService == null)
            executorService = Executors.newFixedThreadPool(2);

        Map<String, ProgramState> programs = new HashMap<>();
        for (String progName : progNames) {
            programs.put(progName, repo.getProgramByName(progName));
        }
        List<ProgramState> programStates = new ArrayList<>(programs.values());

        //print the program state before execution
        //programStates.forEach((state)-> repo.logProgramState(state));

        //run one step concurrently on all programs in the list
        //prepare the list
        List<Callable<ProgramState>> callList = programStates.stream()
                .map((ProgramState programState) -> (Callable<ProgramState>) (programState::step))
                .collect(Collectors.toList());

        //start the execution of the callables, returns a new list of progStates, threads
        List<ProgramState> newStates = executorService.invokeAll(callList).stream()
                .map(
                        programStateFuture -> {
                            try {
                                return programStateFuture.get();
                            } catch (Exception e) {
                                return null;
                            }
                        }
                ).filter(Objects::nonNull)
                .collect(Collectors.toList());

        //add the newly created threads to the old ones to be stored in the repo
        for (ProgramState p : newStates) {
            programs.put(String.valueOf(p.getId()), p);
        }
        repo.setPrograms(programs);
        //log the state
        //newStates.forEach(progState -> repo.logProgramState(progState));
    }

    public void runConcurrent() throws InterruptedException, RepositoryException {
        executorService = Executors.newFixedThreadPool(2);

        //remove completed programs
        Map<String, ProgramState> progStates = removeCompleted(repo.getPrograms());

        //print the program state before execution
        progStates.forEach((str, state) -> repo.logProgramState(state));

        while (progStates.size() > 0) {
            //gc for all the current programStates
            progStates.forEach((name, state) ->
                    state.getHeap().setContent(gc(state.getSymbols().values(), state.getHeap().getContent())));

            stepOnAll(new ArrayList<>(progStates.keySet()));

            progStates.forEach((str, state) -> repo.logProgramState(state));
            //remove completed
            progStates = removeCompleted(repo.getPrograms());
        }
        executorService.shutdownNow();

        repo.setPrograms(progStates);
    }

    /**
     * Create a new program which can receive and execute instructions.
     * Called before starting to receive statements from the UI
     * @param progName Name of the program to be created and added into the repo
     */
    public void addEmptyProgram(String progName) throws RepositoryException {
        ProgramState state = new ProgramState();

        repo.addProgram(progName, state);
    }

    /**
     * Parse the input string and create the corresponding statements.
     * Assuming only valid program lines are used as params
     *
     * @param input String received form the UI assumed to be syntactically correct
     *              E.g. a=1+2
     *                   print(a)
     *
     */
    public void addStatementString(@NotNull String input, @NotNull String progName) throws SyntaxException, RepositoryException {

        AbstractStatement s = StatementParser.getStatementFromString(input);
        repo.getProgramByName(progName).getExecutionStack().push(s);
    }

    @SuppressWarnings("unchecked")
    public Vector<String> getStackString(String progName) throws RepositoryException {
        Stack<AbstractStatement> s = (Stack<AbstractStatement>) repo.getProgramByName(progName).getExecutionStack().clone();

        Vector<String> v = new Vector<>(10);

        while (!s.isEmpty()) {
            v.add(s.pop().toString());
        }
        return v;
    }

    public Vector<String> getOutput(String progName) throws RepositoryException {
        return repo.getProgramByName(progName).getOutput();
    }

    public Map<String, Integer> getSymbols(String progName) throws RepositoryException {
        return repo.getProgramByName(progName).getSymbols();
    }

    public FileTable getFiles(String progName) throws RepositoryException {
        return repo.getProgramByName(progName).getFiles();
    }

    public Heap getHeap(String progName) throws RepositoryException {
        return (Heap) repo.getProgramByName(progName).getHeap();
    }

    public Map<String, ProgramState> getAllStates() {
        return repo.getPrograms();
    }
    /**
     * Remove the values from the heap which are not referenced by any of the values from the symbols
     *
     * @param symTableValues
     * @param heap
     * @return
     */
    private Map<Integer, Integer> gc(Collection<Integer> symTableValues, Map<Integer, Integer> heap) {
        //symTableValues.contains(heap.get(0));

        Map<Integer, Integer> o = heap.entrySet().stream()
                .filter(e -> symTableValues.contains(e.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        return o;
    }

    private Map<String, ProgramState> removeCompleted(Map<String, ProgramState> inMap) {

        for (String progName : inMap.keySet()) {
            try {
                // close the files on progStates that completed execution
                if (!repo.getProgramByName(progName).isNotCompleted())
                    closeFiles(progName);
            } catch (RepositoryException e) {
                e.printStackTrace();
            }
        }
        return inMap.entrySet().stream()
                .filter((program) -> program.getValue().isNotCompleted())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private void closeFiles(String progName) throws RepositoryException {
        Map<Integer, Pair<String, BufferedReader>> files = repo.getProgramByName(progName).getFiles().getAll();

        files.keySet().
                forEach(
                        (Integer descriptor) -> {
                            try {
                                files.get(descriptor).getValue().close();
                            } catch (IOException e) {
                            }
                        }
                );
    }
}