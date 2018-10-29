package controller;

import exceptions.*;
import model.adt.Stack;
import model.adt.Vector;
import model.expression.ArithmeticExpression;
import model.expression.ConstantExpression;
import model.expression.Expression;
import model.expression.VariableExpression;
import model.programState.ProgramState;
import model.statement.*;
import org.jetbrains.annotations.NotNull;
import repository.Repository;
import repository.RepositoryInterface;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

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
    public void step(String progName) throws RepositoryException, UndefinedOperationException, UndefinedVariableException, IOException {
         ProgramState state = repo.getProgramByName(progName);
         Statement top = state.getExecutionStack().pop();
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
    public void run(String progName) throws RepositoryException, UndefinedVariableException, UndefinedOperationException, IOException {
        ProgramState state = repo.getProgramByName(progName);
        while (!state.getExecutionStack().isEmpty()) {
            Statement top = state.getExecutionStack().pop();
            top.execute(state);
            repo.logProgramState(state);
        }

    }

    /**
     * Create a new program which can receive and execute instructions
     * @param progName Name of the program to be created and added into the repo
     */
    public void addEmptyProgram(String progName) throws RepositoryException {
        ProgramState state = new ProgramState();

            repo.addProgram(progName, state);
    }

    /**
     *  Used to decide on the type of a statement based on its semantics
     * @param statementStr: statement as string
     * @return Assignment/Compound/If/Print depending on the statement type
     */
    private String getStatementType(@NotNull String statementStr) {
        String[] aux = statementStr.split("=");
        String[] openAndReadCheck = statementStr.split(",");

        if (statementStr.startsWith("openFile") && openAndReadCheck.length == 2) {
            return "OpenFileStatement";
        }
        if (statementStr.startsWith("readFile") && openAndReadCheck.length == 2) {
            return "ReadFileStatement";
        }
        if (statementStr.startsWith("closeFile") && openAndReadCheck.length == 1) {
            return "CloseFileStatement";
        }

        if(aux.length == 2 && !statementStr.contains(";"))
            return "AssignmentStatement";
        aux = statementStr.split(";");
        if (aux.length >= 2 && !statementStr.contains("if")) {
            return "CompoundStatement";
        }
        if(statementStr.contains("if") && statementStr.contains("then") && statementStr.contains("else")) {
            return "IfStatement";
        }
        else if(statementStr.contains("print(") && statementStr.endsWith(")")) {
            return "PrintStatement";
        }
        return null;
    }

    /**
     * Find the type of expression which fits the given semantics
     * @param expression: expression whose type needs to be identified
     * @return: the type of expression as class
     */
    @NotNull
    private String getExpressionType(String expression) {
        if(expression.matches(ConstantExpression.constantRegex)) {
            return "ConstantExpression";
        }
        else if(expression.matches(VariableExpression.variableRegex)) {
            return "VariableExpression";
        }
        else {
            return "ArithmeticExpression";
        }
    }

    /**
     *
     * @param input a compound statement given as a string, e.g. a=2+3;b=1
     * @return  An instance of CompoundStatement created by parsing the given string
     * @throws SyntaxException  if the input string doesn't have valid CompoundStatement syntax
     */
    private CompoundStatement getCompoundStatementFromString(String input) throws SyntaxException {
        //? separator by ';'?
        //a=2;b=2*a+5 OR a=b;b=3 OR a=c*3;b=c*4; OR a=a+1;print(a)
        //the 2 sides can have different statement types, identify it for each side
        String[] sides = input.split(";", 2);
        //if we have the statement: st1;st2;st3 after split: {st1, st2;st3}
        //should result in 2 strings
        String first = sides[0];
        String second = sides[1];
        String firstType = getStatementType(first);
        String secondType = getStatementType(second);

        Statement firstStatement;
        Statement secondStatement;

        firstStatement = getStatementFromType(first, firstType);
        secondStatement = getStatementFromType(second, secondType);

        CompoundStatement s = new CompoundStatement(firstStatement, secondStatement);
        return s;
    }

    /**
     *
     * @param statement String representation of the statement, assumed to be syntactically valid
     * @param statementType The type of statement, Assignment, Compound, If, Print...
     * @return  A Statement built from the given string
     * @throws SyntaxException if the input string is has syntactic errors
     */
    private Statement getStatementFromType(String statement, String statementType) throws SyntaxException {
        switch (statementType) {
            case "AssignmentStatement":
                return getAssignmentStatementFromString(statement);
            case "CompoundStatement":
                return getCompoundStatementFromString(statement);
            case "PrintStatement":
                return getPrintStatementFromString(statement);
            case "IfStatement":
                break;
        }
        return null;
    }

    /**
     *
     * @param expressionStr string representation of an expression
     * @param expressionType type of expression: (constant, variable or arithmetic)
     * @return  An Expression built from the given string
     * @throws SyntaxException  if there are syntax errors in the input string
     */
    private Expression getExpressionFromType(String expressionStr, String expressionType) throws SyntaxException {
        switch(expressionType) {
            case "ConstantExpression":
                return new ConstantExpression(Integer.parseInt(expressionStr));
            case "VariableExpression":
                String varName = expressionStr.split("=")[0];
                return new VariableExpression(varName);
            case "ArithmeticExpression":
                String rhs = expressionStr;//.split("=")[1];       //crash on print(a+1)
                Vector<String> postfix = Expression.infixToPostfix(rhs);
                return Expression.buildExpressionFromPostfix(postfix);
        }
        return null;
    }

    /**
     *
     * @param input string representation of a print statement
     * @return  A PrintStatement build from the input string
     * @throws SyntaxException if the input string is not syntactically valid
     */
    private PrintStatement getPrintStatementFromString(String input) throws SyntaxException {
        //depending on the parameter, treat each case
        //syntax: print(a) OR print(a+2*b) OR print(2)
        //          var         expr            const
        //extract the expression string
        Expression expr=null;

        String param = input.replace("print(", "").replace(")", "").replace(";", "");

        //figure out what kind of expression param is:
        expr = getExpressionFromType(param, getExpressionType(param));

        PrintStatement s = new PrintStatement(expr);
        return s;
    }

    /**
     *
     * @param input: String repr. of an assignment, e.g. "a=2+3" or "a=2*b+8*c"
     * @return  A AssignmentStatement built from the string
     * @throws SyntaxException
     */
    private AssignmentStatement getAssignmentStatementFromString(String input) throws SyntaxException {
        //then it has the syntax: var_name=const_value OR var_name = another_var OR var_name = arith_expr
        //remove the ';' and split by '='
        input = input.replace(";", "");
        String sides[] = input.split("=");
        String varName = sides[0];      //there will always be exactly one variable in the lhs
        AssignmentStatement s = null;
        //depending on whether the rhs is a const, var or arith_expr

        switch (getExpressionType(sides[1])) {
            case "ConstantExpression":
                int value = Integer.parseInt(sides[1]);
                s = new AssignmentStatement(varName, new ConstantExpression(value));
                break;

            case "VariableExpression":
                String rhsVarName = sides[1];
                s = new AssignmentStatement(varName, new VariableExpression(rhsVarName));
                break;

            case "ArithmeticExpression":
                String rhsExpr = sides[1];
                ArithmeticExpression arithmeticExpression;
                Vector<String> postfix = Expression.infixToPostfix(rhsExpr);
                arithmeticExpression = Expression.buildExpressionFromPostfix(postfix);
                s = new AssignmentStatement(varName, arithmeticExpression);
                break;
        }
        return s;
    }

    /**
     * syntax expected: if expr/var/const then statement1 else statement2;
     * statement1 and statement2 can be compound statements, so don't remove the ; yet
     * @param input: if statement string which is syntactically valid
     * @return an IfStatement parsed from the given string
     */
    private IfStatement getIfStatementFromString(String input) throws SyntaxException {
        Expression condition;
        Statement thenStatement;
        Statement elseStatement;
        String conditionType;
        String thenStatementType;
        String elseStatementType;
        String[] tokens = input.split(" ");

        //condition at pos. 1
        // thenStatement at 3
        // elseStatement at 5
        conditionType = getExpressionType(tokens[1]);
        thenStatementType = getStatementType(tokens[3]);
        //elseStatementType = getStatementType(tokens[5].replace(";", ""));   //it would replace the ;
        //in compound statements from the else branch as well
        elseStatementType = getStatementType(tokens[5]);

        condition = getExpressionFromType(tokens[1], conditionType);
        thenStatement = getStatementFromType(tokens[3], Objects.requireNonNull(thenStatementType));
        elseStatement = getStatementFromType(tokens[5], Objects.requireNonNull(elseStatementType));

        IfStatement ifStatement = new IfStatement(condition, thenStatement, elseStatement);
        return ifStatement;
    }

    private CloseFileStatement getCloseFileStatementFromString(String input) throws SyntaxException {
        Expression fileIdExpression;

        String fileId = input.replace("closeFile(", "").replace(")", "");
        String expressionType = getExpressionType(fileId);

        fileIdExpression = getExpressionFromType(fileId, expressionType);

        CloseFileStatement closeFileStatement = new CloseFileStatement(fileIdExpression);
        return closeFileStatement;
    }

    private OpenFileStatement getOpenFileStatementFromString(String input) {
        String varName;     //will store the UID of the file, generated in the table insertion
        String filename;    //the name of the filename to be opened
        input = input.replace(" ", "");     //delete spaces so that variables don't end up with spaces in them

        String[] params = input.split(",");
        //extract the varName
        varName = params[0].replace("openFile(", "");
        filename = params[1].replace(")", "");

        OpenFileStatement openFileStatement = new OpenFileStatement(varName, filename);
        return openFileStatement;
    }

    private ReadFileStatement getReadFileStatementFromString(String input) throws SyntaxException {
        Expression fileId;
        String fileIdStr;
        String varName;
        input = input.replace(" ", "");     //delete spaces so that variables don't end up with spaces in them

        String[] params = input.split(",");
        //extract the varName
        fileIdStr = params[0].replace("readFile(", "");
        varName = params[1].replace(")", "");

        String exprType = getExpressionType(fileIdStr);
        fileId = getExpressionFromType(fileIdStr, exprType);
        ReadFileStatement readFileStatement = new ReadFileStatement(fileId, varName);
        return readFileStatement;
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

        Statement s = null;
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
            case "ReadFileStatement":
                s = getReadFileStatementFromString(input);
                break;
        }
        repo.getProgramByName(progName).getExecutionStack().push(s);
    }

    @SuppressWarnings("unchecked")
    public Vector<String> getStackString(String progName) throws RepositoryException {
        Stack<Statement> s = repo.getProgramByName(progName).getExecutionStack().clone();

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
}