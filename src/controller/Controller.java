package controller;

import exceptions.*;
import model.programState.ProgramState;
import model.statement.AbstractStatement;
import model.util.FileTable;
import repository.Repository;
import repository.RepositoryInterface;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Stack;
import java.util.Vector;

import static model.statement.AbstractStatement.*;

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
                s = getAssignmentStatementFromString(input);
                break;

            case "CompoundStatement":
                s = getCompoundStatementFromString(input);
                break;

            case "PrintStatement":
                s = getPrintStatementFromString(input);
                break;

            case "IfStatement":
                s = getIfStatementFromString(input);
                break;

            case "CloseFileStatement":
                s = getCloseFileStatementFromString(input);
                break;

            case "OpenFileStatement":
                s = getOpenFileStatementFromString(input);
                break;

            case "ReadFileStatementTest":
                s = getReadFileStatementFromString(input);
                break;
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
}