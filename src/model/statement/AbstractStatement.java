package model.statement;

import exceptions.SyntaxException;
import exceptions.UndefinedOperationException;
import exceptions.UndefinedVariableException;
import model.expression.AbstractExpression;
import model.expression.ArithmeticExpression;
import model.expression.ConstantExpression;
import model.expression.VariableExpression;
import model.programState.ProgramState;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;
import java.util.Vector;

import static model.expression.AbstractExpression.getExpressionFromType;
import static model.expression.AbstractExpression.getExpressionType;

public abstract class AbstractStatement {
    public abstract String toString();

    /**
     * Used to decide on the type of a statement based on its semantics
     *
     * @param statementStr: statement as string
     * @return Assignment/Compound/If/Print depending on the statement type
     */
    public static String getStatementType(@NotNull String statementStr) throws SyntaxException {
        String[] aux = statementStr.split("=");
        String[] openAndReadCheck = statementStr.split(",");
        //also check if it contains ';'; it might be a compound statement
        if (statementStr.startsWith("openFile") && openAndReadCheck.length == 2 && !statementStr.contains(";")) {
            return "OpenFileStatement";
        }
        if (statementStr.startsWith("readFile") && openAndReadCheck.length == 2 && !statementStr.contains(";")) {
            return "ReadFileStatement";
        }
        if (statementStr.startsWith("closeFile") && openAndReadCheck.length == 1 && !statementStr.contains(";")) {
            return "CloseFileStatement";
        }

        if (aux.length == 2 && !statementStr.contains(";"))
            return "AssignmentStatement";

        aux = statementStr.split(";");
        if (aux.length >= 2 && !statementStr.contains("if")) {
            return "CompoundStatement";
        } else if (statementStr.contains("if") && statementStr.contains("then") && statementStr.contains("else")) {
            return "IfStatement";
        } else if (statementStr.contains("print(") && statementStr.endsWith(")")) {
            return "PrintStatement";
        }

        if (statementStr.contains("call") && statementStr.contains("(") && statementStr.contains(")")) {
            return "CallStatement";
        } else if (statementStr.contains("load") && statementStr.split(" ").length == 2) {
            return "LoadStatement";
        } else if (statementStr.contains("return")) {
            return "ReturnStatement";
        }

        if (statementStr.contains("new")) {
            return "newEntryStatement";
        }
        return null;
    }

    /**
     * Call syntax: call <functionName>(arg1, arg2..., argn)
     *
     * @param input: statement string
     * @return the CallStatement built from the string
     */
    public static CallStatement getCallStatementFromString(String input) throws SyntaxException {
        //remove the 'call' and extract the function name and params
        String functionName;
        String[] args;
        CallStatement statement = null;
        input = input.replace("call ", "");
        functionName = input.split("\\(")[0];
        args = input.split("\\(")[1].replace(")", "").replace(" ", "")
                .split(",");
        Vector<AbstractStatement> params = new Vector<>();
        for (String arg : args) {
            statement = (CallStatement) getStatementFromType(arg, getStatementType(arg));
        }
        return statement;
    }

    /**
     * @param input a compound statement given as a string, e.g. a=2+3;b=1
     * @return An instance of CompoundStatement created by parsing the given string
     * @throws SyntaxException if the input string doesn't have valid CompoundStatement syntax
     */
    public static CompoundStatement getCompoundStatementFromString(String input) throws SyntaxException {
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

        AbstractStatement firstStatement;
        AbstractStatement secondStatement;

        firstStatement = getStatementFromType(first, firstType);
        secondStatement = getStatementFromType(second, secondType);

        CompoundStatement s = new CompoundStatement(firstStatement, secondStatement);
        return s;
    }

    /**
     * @param statement     String representation of the statement, assumed to be syntactically valid
     * @param statementType The type of statement, Assignment, Compound, If, Print...
     * @return A AbstractStatement built from the given string
     * @throws SyntaxException if the input string is has syntactic errors
     */
    public static AbstractStatement getStatementFromType(String statement, String statementType) throws SyntaxException {
        switch (statementType) {
            case "AssignmentStatement":
                return getAssignmentStatementFromString(statement);
            case "CompoundStatement":
                return getCompoundStatementFromString(statement);
            case "PrintStatement":
                return getPrintStatementFromString(statement);
            case "IfStatement":
                return getIfStatementFromString(statement);
            case "OpenFileStatement":
                return getOpenFileStatementFromString(statement);
            case "CloseFileStatement":
                return getCloseFileStatementFromString(statement);
            case "ReadFileStatement":
                return getReadFileStatementFromString(statement);
            case "CallStatement":
                return getCallStatementFromString(statement);
            case "LoadStatement":
                return getLoadFunctionStatementFromString(statement);
            case "ReturnStatement":
                return getReturnStatementFromString(statement);
            case "newEntryStatement":
                return getNewEntryStatementFromString(statement);
        }
        return null;
    }

    public static NewEntryStatement getNewEntryStatementFromString(@NotNull String input) throws SyntaxException {
        String varName;
        AbstractExpression expr;
        String[] params = input.replace("new(", "")
                .replace(")", "")
                .replace(" ", "").split(",");
        varName = params[0];

        expr = getExpressionFromType(params[1], getExpressionType(params[1]));

        NewEntryStatement statement = new NewEntryStatement(varName, expr);

        return statement;
    }

    /**
     * Syntax: load <functionName>
     *
     * @param input
     * @return
     */
    public static LoadFunctionFromFileStatement getLoadFunctionStatementFromString(String input) {
        String functionName = input.split(" ")[1];
        LoadFunctionFromFileStatement statement = new LoadFunctionFromFileStatement(functionName);
        return statement;
    }

    /**
     * syntax: return varName or return
     *
     * @param input
     * @return
     */
    public static ReturnStatement getReturnStatementFromString(@NotNull String input) throws SyntaxException {
        ReturnStatement statement = null;
        if (input.split(" ").length == 1) {
            //no return varName
            statement = new ReturnStatement();
        } else if (input.split(" ").length == 2) {
            //has return varName
            statement = new ReturnStatement(AbstractExpression.getExpressionFromType(input.split(" ")[0],
                    AbstractExpression.getExpressionType(input.split(" ")[0])));
        }
        return statement;
    }

    /**
     * @param input string representation of a print statement
     * @return A PrintStatement build from the input string
     * @throws SyntaxException if the input string is not syntactically valid
     */
    public static PrintStatement getPrintStatementFromString(String input) throws SyntaxException {
        //depending on the parameter, treat each case
        //syntax: print(a) OR print(a+2*b) OR print(2)
        //          var         expr            const
        //extract the expression string
        AbstractExpression expr = null;

        String param = input.replace("print(", "").replace(")", "").replace(";", "");

        //figure out what kind of expression param is:
        expr = getExpressionFromType(param, getExpressionType(param));

        PrintStatement s = new PrintStatement(expr);
        return s;
    }

    /**
     * @param input: String repr. of an assignment, e.g. "a=2+3" or "a=2*b+8*c"
     * @return A AssignmentStatement built from the string
     * @throws SyntaxException
     */
    public static AssignmentStatement getAssignmentStatementFromString(String input) throws SyntaxException {
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
                Vector<String> postfix = AbstractExpression.infixToPostfix(rhsExpr);
                arithmeticExpression = AbstractExpression.buildExpressionFromPostfix(postfix);
                s = new AssignmentStatement(varName, arithmeticExpression);
                break;
        }
        return s;
    }

    /**
     * syntax expected: if expr/var/const then statement1 else statement2;
     * statement1 and statement2 can be compound statements, so don't remove the ; yet
     *
     * @param input: if statement string which is syntactically valid
     * @return an IfStatement parsed from the given string
     */
    public static IfStatement getIfStatementFromString(String input) throws SyntaxException {
        AbstractExpression condition;
        AbstractStatement thenStatement;
        AbstractStatement elseStatement;
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

    public static CloseFileStatement getCloseFileStatementFromString(String input) throws SyntaxException {
        AbstractExpression fileIdExpression;

        String fileId = input.replace("closeFile(", "").replace(")", "");
        String expressionType = getExpressionType(fileId);

        fileIdExpression = getExpressionFromType(fileId, expressionType);

        CloseFileStatement closeFileStatement = new CloseFileStatement(fileIdExpression);
        return closeFileStatement;
    }

    public static OpenFileStatement getOpenFileStatementFromString(String input) {
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

    public static ReadFileStatement getReadFileStatementFromString(String input) throws SyntaxException {
        AbstractExpression fileId;
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

    public abstract ProgramState execute(ProgramState programState) throws UndefinedOperationException, UndefinedVariableException, IOException, SyntaxException;

    public abstract String getFunction();

    public abstract void setFunction(String functionName);
}
