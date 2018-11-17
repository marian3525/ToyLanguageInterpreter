package controller;

import exceptions.*;
import model.adt.Heap;
import model.programState.ProgramState;
import model.statement.*;
import model.util.FileTable;
import repository.Repository;
import repository.RepositoryInterface;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static model.statement.AbstractStatement.getStatementType;

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
    public void step(String progName) throws RepositoryException, UndefinedOperationException, UndefinedVariableException, IOException, SyntaxException {
        ProgramState state = repo.getProgramByName(progName);
        AbstractStatement top = state.getExecutionStack().pop();
        top.execute(state);
        //gc
        repo.getProgramByName(progName).getHeap().setContent(
                gc(repo.getProgramByName(progName).getSymbols().values(),
                        repo.getProgramByName(progName).getHeap().getAll())
        );
        repo.logProgramState(state);
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

        while (!state.getExecutionStack().isEmpty()) {
            AbstractStatement top = state.getExecutionStack().pop();
            top.execute(state);

            //gc
            Collection<Integer> values = state.getSymbols().values();
            Map<Integer, Integer> all = state.getHeap().getContent();

            Map<Integer, Integer> cleanHeap = gc(values, all);
            state.getHeap().setContent(cleanHeap);

            repo.logProgramState(state);
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
     * attempt to find the type of statement
     * simple assignment: split by '=' if contained and check for a rhs and a lhs
     * compound statement: split by ; and split the 2 halves for assignments by splitting by '='
     *
     * @param input String received form the UI assumed to be syntactically correct
     *              E.g. a=1+2;
     *                   print(a)
     *
     */
    public void addStatementString(String input, String progName) throws SyntaxException, RepositoryException {

        AbstractStatement s = null;
        if(input == null || progName == null)
            return;
        switch (Objects.requireNonNull(getStatementType(input))) {
            case "AssignmentStatement":
                s = AssignmentStatement.getAssignmentStatementFromString(input);
                break;

            case "CompoundStatement":
                s = CompoundStatement.getCompoundStatementFromString(input);
                break;

            case "PrintStatement":
                s = PrintStatement.getPrintStatementFromString(input);
                break;

            case "IfStatement":
                s = IfStatement.getIfStatementFromString(input);
                break;

            case "CloseFileStatement":
                s = CloseFileStatement.getCloseFileStatementFromString(input);
                break;

            case "OpenFileStatement":
                s = OpenFileStatement.getOpenFileStatementFromString(input);
                break;

            case "ReadFileStatement":
                s = ReadFileStatement.getReadFileStatementFromString(input);
                break;
            case "NewEntryStatement":
                s = NewHeapEntryStatement.getNewHeapEntryStatementFromString(input);
                break;
            case "WriteHeapStatement":
                s = WriteHeapStatement.getWriteHeapStatementFromString(input);
        }
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