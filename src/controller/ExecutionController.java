package controller;

import exceptions.*;
import javafx.util.Pair;
import model.adt.Heap;
import model.expression.ArithmeticExpression;
import model.expression.ConstantExpression;
import model.expression.VariableExpression;
import model.interfaces.ProcTableInterface;
import model.programState.ProgramState;
import model.statement.*;
import model.util.FileTable;
import model.util.Observer;
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
import java.util.concurrent.RejectedExecutionException;
import java.util.stream.Collectors;


public class ExecutionController {
    private RepositoryInterface repo;
    private ExecutorService executorService;

    /**
     * Constructor used by the cli and tests which don't implement the observer pattern
     */
    public ExecutionController() {
        repo = new Repository();
    }

    public ExecutionController(Observer o) {
        repo = new Repository();
        ((Repository) repo).registerObserver(o);
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
        while (true) {
            try {
                step(progName);
            } catch (ProgramException pe) {
                //end of program reached
                //clean up the files left opened
                closeFiles(progName);
                break;
            }
        }
    }

    void stepOnAll(List<String> progNames) throws InterruptedException, RepositoryException {
        // needed for testing where stepOnAll is required, but not runAll
        if (executorService == null)
            executorService = Executors.newFixedThreadPool(2);

        Map<String, ProgramState> programs = new HashMap<>();
        for (String progName : progNames) {
            programs.put(progName, repo.getProgramByName(progName));
        }
        List<ProgramState> programStates = new ArrayList<>(programs.values());

        //print the program state before execution
        programStates.forEach((state)-> repo.logProgramState(state));

        //run one step concurrently on all programs in the list
        //prepare the list
        List<Callable<ProgramState>> callList = programStates.stream()
                .map((ProgramState programState) -> (Callable<ProgramState>) (programState::step))
                .collect(Collectors.toList());


        //start the execution of the callables, returns a new list of progStates, threads
        try {
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
            newStates.forEach(progState -> repo.logProgramState(progState));

            repo.setPrograms(removeCompleted(programs));

            //log the state
        } catch(RejectedExecutionException ree) {
            System.out.println("Execution rejected by the Executor, no program states to run");
        }
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
        /**
         * Add the statement given as a string in input to the execution stack of the program with the given name
         * @param input: statement string
         * @param progName: the name of the program state in which the statement will be added
         */
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

    public RepositoryInterface getRepo() {
        return repo;
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
                // close the files on progStates that completed execution and remove it from the map
                if (!repo.getProgramByName(progName).isNotCompleted()) {
                    closeFiles(progName);
                }
            } catch (RepositoryException e) {

            }
        }
        return inMap.entrySet().stream()
                // log the contents of the programs which now have an empty stack and are about to be removed
                .filter(program -> {
                    boolean finished = !program.getValue().isNotCompleted();
                    if(finished) {repo.logProgramState(program.getValue());
                    // false
                    return true;
                    }
                    else {
                        return true;
                    }
                })
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

    void populateStates() {
        /**
         * Populate the repo with program states
         * Throws if the syntax in the string statements is incorrect or when trying to add a program name which already
         * exists
         */
        // add program states with the statements given as strings

        try {
            /*
            addEmptyProgram("example1");
            addStatementString("print(a)", "example1");
            addStatementString("closeFile(1)", "example1");
            addStatementString("readFile(a,a)", "example1");
            addStatementString("openFile(a,file.txt)", "example1");

            addEmptyProgram("example2");
            addStatementString("closeFile(var_f)", "example2");
            addStatementString("if var_c then readFile(var_f,var_c);print(var_c) else print(0)"
                    , "example2");
            addStatementString("readFile(var_f,var_c);print(var_c)", "example2");
            addStatementString("openFile(var_f,file.txt)", "example2");

            addEmptyProgram("threadsMain");
            addStatementString("print(readHeap(a))", "threadsMain");
            addStatementString("print(a);a=a;a=a;a=a", "threadsMain");
            addStatementString("fork(a=2;print(a);new(addr, 10))", "threadsMain");
            addStatementString("a=1;print(a)", "threadsMain");

            addEmptyProgram("simple");
            addStatementString("print(0)", "simple");
*/
            // add program states the ugly way
            repo.addProgram("exam1", new ProgramState());

            AbstractStatement first = new AssignmentStatement("v", new ConstantExpression(10));

            AbstractStatement insideFork = new CompoundStatement(
                    new AssignmentStatement("v", new ArithmeticExpression(new VariableExpression("v"), new ConstantExpression(1), "-")),
                    new CompoundStatement(
                            new AssignmentStatement("v", new ArithmeticExpression(new VariableExpression("v"), new ConstantExpression(1), "-")),
                            new PrintStatement(new VariableExpression("v"))
                            )
            );

            AbstractStatement lastStatements = new CompoundStatement(new SleepStatement(10), new PrintStatement(
                    new ArithmeticExpression(new VariableExpression("v"), new ConstantExpression(10), "*")
            ));

            AbstractStatement fStatement = new ForkStatement(insideFork);
            AbstractStatement last = new CompoundStatement(fStatement, lastStatements);

            repo.getProgramByName("exam1").getExecutionStack().push(last);
            repo.getProgramByName("exam1").getExecutionStack().push(first);


            // add procedures

            // add main
            repo.addProgram("exam2", new ProgramState());

            String name = "sum";
            List<String> formals = Arrays.asList("a", "b");
            AbstractStatement body = new CompoundStatement(new AssignmentStatement("v",
                    new ArithmeticExpression(new VariableExpression("a"),
                                                new VariableExpression("b"),
                                                                        "+")), new PrintStatement(new VariableExpression("v")));
            repo.addProcedure(name, new Pair<>(formals, body), "exam2");

            name = "product";
            formals = Arrays.asList("a", "b");
            body = new CompoundStatement(
                    new AssignmentStatement("v",
                                new ArithmeticExpression(new VariableExpression("a"),
                                            new VariableExpression("b"),
                                        "*")),
                    new PrintStatement(new VariableExpression("v")));
            repo.addProcedure(name, new Pair<>(formals, body), "exam2");


            AbstractStatement second = new ForkStatement(
                    new CompoundStatement(
                            new CallStatement("product", Arrays.asList(new VariableExpression("v"), new VariableExpression("w"))),
                            new ForkStatement(
                               new CallStatement("sum", Arrays.asList(new VariableExpression("v"), new VariableExpression("w")))
                            )
                            ));

            AbstractStatement one = new CompoundStatement(
                    new AssignmentStatement("v", new ConstantExpression(2)),
                    new AssignmentStatement("w", new ConstantExpression(5))
            );

            AbstractStatement two = new CompoundStatement(
                    new CallStatement("sum", Arrays.asList(new ArithmeticExpression(new VariableExpression("v"), new ConstantExpression(10), "*"), new VariableExpression("w"))),
                    new PrintStatement(new VariableExpression("v"))
            );

            repo.getProgramByName("exam2").getExecutionStack().push(second);
            repo.getProgramByName("exam2").getExecutionStack().push(two);
            repo.getProgramByName("exam2").getExecutionStack().push(one);


        }
        catch(RepositoryException ignored) {

        }
    }

    public ProcTableInterface getProcedures(String progName) throws RepositoryException{
        return repo.getProgramByName(progName).getProcTable();
    }
}