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
import java.util.stream.Collectors;


public class Controller {
    private RepositoryInterface repo;

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
        AbstractStatement top;

        try {
            top = state.getExecutionStack().pop();
        } catch (EmptyStackException ese) {
            throw new ProgramException("End of program reached");
        }
        top.execute(state);
        //gc
        Collection<Integer> values = state.getSymbols().values();
        Map<Integer, Integer> all = state.getHeap().getContent();

        Map<Integer, Integer> cleanHeap = gc(values, all);
        state.getHeap().setContent(cleanHeap);

        //repo.logProgramState(state);  //todo remove comment after debug
    }

    /**
     * Run all the instructions in the current program
     * @param progName the name of the program to be run
     * @throws ProgramException if the specified can't be run because it doesn't exist
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
                break;
            }
        }
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
}