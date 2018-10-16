package controller;

import exceptions.*;
import model.expression.ArithmeticExpression;
import model.expression.ConstantExpression;
import model.expression.Expression;
import model.expression.VariableExpression;
import model.programState.ProgramState;
import model.statement.*;
import org.intellij.lang.annotations.RegExp;
import org.jetbrains.annotations.NotNull;
import repository.Repository;

import java.util.Map;
import java.util.Objects;
import java.util.Stack;
import java.util.Vector;

public class Controller {
    Repository repo;

    public Controller() {
        repo = Repository.makeRepository();
    }

    public void step(String progName) throws ProgramException, UndefinedOperationException, UndefinedVariableException {
         ProgramState state = repo.getProgramByName(progName);
         Statement top = state.getExecutionStack().pop();
         top.execute(state);
    }

    public void run(String progName) throws ProgramException, UndefinedVariableException, UndefinedOperationException {
        ProgramState state = repo.getProgramByName(progName);
        while(! state.getExecutionStack().empty()) {
            Statement top = state.getExecutionStack().pop();
            top.execute(state);
        }

    }

    public int execute(String input) throws UndefinedVariableException, UndefinedOperationException {
        AssignmentStatement a = new AssignmentStatement("a",
                new ArithmeticExpression(new ConstantExpression(3), new ConstantExpression(2), "+"));
        ProgramState p = new ProgramState();
        p = a.execute(p);
        return p.getSymbols().get("a");
    }

    public void addEmptyProgram(String progName) {
        ProgramState state = new ProgramState();
        try {
            repo.addProgram(progName, state);
        } catch (RepositoryException e) {
            e.printStackTrace();
        }
    }
    /**
     * Split the input string in its components separated with space used as separator
     */
    public Vector<String> tokenize(String input) {

        Vector<String> tokens = new Vector<>(10);
        String[] auxTok = input.split("[-+*/|\\s*]");

        for(String t : auxTok) {
            if(!t.contains(" "))
                tokens.add(t);
        }
        return tokens;
    }

    /**
     *
     * @param statementStr: statement as string
     * @return Assignment/Compound/If/Print depending on the statement type
     */
    private String getStatementType(@NotNull String statementStr) {
        if(statementStr.matches(AssignmentStatement.assignmentRegex)) { //TODO assignments like a=2+b don't fit here
            return "AssignmentStatement";
        }
        else{
            String[] aux = statementStr.split(";");
                if(aux.length == 2) {
                    return "CompoundStatement";
            }
        }
        if(statementStr.matches(IfStatement.ifRegex)) {
            return "IfStatement";
        }
        else if(statementStr.matches(PrintStatement.printRegex)) {
            return "PrintStatement";
        }
        return null;
    }

    /**
     * @param expression: expression whose type need to be identified
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

    private Expression getExpressionFromType(String expressionStr, String expressionType) throws SyntaxException {
        switch(expressionType) {
            case "ConstantExpression":
                return new ConstantExpression(Integer.parseInt(expressionStr));
            case "VariableExpression":
                String varName = expressionStr.split("=")[0];
                return new VariableExpression(varName);
            case "ArithmeticExpression":
                String rhs = expressionStr.split("=")[1];
                Vector<String> postfix = Expression.infixToPostfix(rhs);
                return Expression.buildExpressionFromPostfix(postfix);
        }
        return null;
    }

    private PrintStatement getPrintStatementFromString(String input) throws SyntaxException {
        //depending on the parameter, treat each case
        //syntax: print(a) OR print(a+2*b) OR print(2)
        //          var         expr            const
        //extract the expression string
        Expression expr=null;

        String param = input.replace("print(", "").replace(")", "");

        //figure out what kind of expression param is:
        expr = getExpressionFromType(param, getExpressionType(param));

        PrintStatement s = new PrintStatement(expr);
        return s;
    }
    private AssignmentStatement getAssignmentStatementFromString(String input) throws SyntaxException {
        //then it has the syntax: var_name=const_value OR var_name = another_var OR var_name = arith_expr
        //remove the ';' and split by '='
        input = input.replace(";", "");
        String sides[] = input.split("=");
        String varName = sides[0];      //there will always be exactly one variable in the lhs
        AssignmentStatement s = null;
        //depending whether the rhs is a const, var or arith_expr

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
        elseStatementType = getStatementType(tokens[5]);

        condition = getExpressionFromType(tokens[1], conditionType);
        thenStatement = getStatementFromType(tokens[3], thenStatementType);
        elseStatement = getStatementFromType(tokens[5], elseStatementType);

        IfStatement ifStatement = new IfStatement(condition, thenStatement, elseStatement);
        return ifStatement;
    }
    /**
     * Parse the input string and create the corresponding statements.
     * Assuming only valid program lines are used as params
     * Algorithm:
     * @param input String received form the UI assumed to be syntactically correct
     *              E.g. a=1+2;
     *                   print(a)
     *
     */
    public void addStatementString(String input, String progName) throws SyntaxException, ProgramException, UndefinedVariableException, UndefinedOperationException {
        //attempt to find the type of statement
        //simple assignment: split by '=' if contained and check for a rhs and a lhs
        //compound statement: split by ; and split the 2 halves for assignments by splitting by '='
        //or regex
        Statement s = null;
        if(input == null || progName == null)
            return;
        switch (getStatementType(input)) {
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
        }
        repo.getProgramByName(progName).getExecutionStack().push(s);
    }

    public Vector<String> getStackString(String progName) throws ProgramException {
        Stack<Statement> s = (Stack<Statement>) repo.getProgramByName(progName).getExecutionStack().clone();
        Vector<String> v = new Vector<>(10);

        while(!s.empty()) {
            v.add(s.pop().toString());
        }
        return v;
    }
    public Vector<String> getOutput(String progName) throws ProgramException {
        return repo.getProgramByName(progName).getOutput();
    }

    public Map<String, Integer> getSymbols(String progName) throws ProgramException {
        return repo.getProgramByName(progName).getSymbols();
    }
}