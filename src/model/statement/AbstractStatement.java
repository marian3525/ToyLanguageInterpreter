package model.statement;

import exceptions.SyntaxException;
import exceptions.UndefinedOperationException;
import exceptions.UndefinedVariableException;
import model.programState.ProgramState;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

public abstract class AbstractStatement {
    public abstract String toString();

    /**
     * Used to decide on the type of a statement based on its semantics
     *
     * @param statementStr: statement as string
     * @return Assignment/Compound/If/Print depending on the statement type
     */
    public static String getStatementType(@NotNull String statementStr) {
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
            return "NewEntryStatement";
        } else if (statementStr.contains("readHeap")) {
            return "ReadHeapStatement";
        } else if (statementStr.contains("writeHeap")) {
            return "WriteHeapStatement";
        }
        return null;
    }

    /**
     * @param statement     String representation of the statement, assumed to be syntactically valid
     * @return A AbstractStatement built from the given string
     * @throws SyntaxException if the input string is has syntactic errors
     */
    static AbstractStatement getStatementFromString(String statement) throws SyntaxException {
        String statementType = getStatementType(statement);
        switch (Objects.requireNonNull(statementType)) {
            case "AssignmentStatement":
                return AssignmentStatement.getAssignmentStatementFromString(statement);
            case "CompoundStatement":
                return CompoundStatement.getCompoundStatementFromString(statement);
            case "PrintStatement":
                return PrintStatement.getPrintStatementFromString(statement);
            case "IfStatement":
                return IfStatement.getIfStatementFromString(statement);
            case "OpenFileStatement":
                return OpenFileStatement.getOpenFileStatementFromString(statement);
            case "CloseFileStatement":
                return CloseFileStatement.getCloseFileStatementFromString(statement);
            case "ReadFileStatement":
                return ReadFileStatement.getReadFileStatementFromString(statement);
            case "CallStatement":
                return CallStatement.getCallStatementFromString(statement);
            case "LoadStatement":
                return LoadFunctionFromFileStatement.getLoadFunctionStatementFromString(statement);
            case "ReturnStatement":
                return ReturnStatement.getReturnStatementFromString(statement);
            case "NewEntryStatement":
                return NewHeapEntryStatement.getNewHeapEntryStatementFromString(statement);
            case "WriteHeapStatement":
                return WriteHeapStatement.getWriteHeapStatementFromString(statement);
        }
        return null;
    }

    public abstract ProgramState execute(ProgramState programState) throws UndefinedOperationException, UndefinedVariableException, IOException, SyntaxException;

    public abstract String getFunction();

    public abstract void setFunction(String functionName);

    private void f() {
    }
}
