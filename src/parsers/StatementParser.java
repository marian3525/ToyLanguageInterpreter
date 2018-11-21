package parsers;

import exceptions.SyntaxException;
import model.statement.*;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class StatementParser {
    /**
     * Used to decide on the type of a statement based on its semantics
     *
     * @param statementStr: statement as string
     * @return Assignment/Compound/If/Print depending on the statement type
     */
    public static StatementType getStatementType(@NotNull String statementStr) {
        //split by the first =
        String[] aux = statementStr.split("=");

        String[] openAndReadCheck = statementStr.split(",");
        //also check if it contains ';'; it might be a compound statement
        if (statementStr.startsWith("openFile") && openAndReadCheck.length == 2 && !statementStr.contains(";")) {
            return StatementType.OpenFileStatement;
        }
        if (statementStr.startsWith("readFile") && openAndReadCheck.length == 2 && !statementStr.contains(";")) {
            return StatementType.ReadFileStatement;
        }
        if (statementStr.startsWith("closeFile") && openAndReadCheck.length == 1 && !statementStr.contains(";")) {
            return StatementType.CloseFileStatement;
        }
        //TODO does not enter if on a=1==2
        if (statementStr.matches(AssignmentStatement.assignmentRegex) && !statementStr.contains(";") && !statementStr.contains("while")
                && !statementStr.contains("if"))
            return StatementType.AssignmentStatement;

        aux = statementStr.split(";");
        if (aux.length >= 2 && !statementStr.contains("if(") && !statementStr.contains("while(")) {
            return StatementType.CompoundStatement;
        }
        else if (statementStr.contains("if") && statementStr.contains("then") && statementStr.contains("else")) {
            return StatementType.IfStatement;
        }
        else if (statementStr.contains("print(") && statementStr.endsWith(")")) {
            return StatementType.PrintStatement;
        }

        if (statementStr.contains("call") && statementStr.contains("(") && statementStr.contains(")")) {
            return StatementType.CallStatement;
        }
        else if (statementStr.contains("load") && statementStr.split(" ").length == 2) {
            return StatementType.LoadFunctionFromFileStatement;
        }
        else if (statementStr.contains("return")) {
            return StatementType.ReadFileStatement;
        }

        if (statementStr.contains("new")) {
            return StatementType.NewHeapEntryStatement;
        }
        else if (statementStr.contains("writeHeap")) {
            return StatementType.WriteHeapStatement;
        }

        if (statementStr.contains("while(")) {
            return StatementType.WhileStatement;
        }
        return null;
    }

    /**
     * @param statement String representation of the statement, assumed to be syntactically valid
     * @return A AbstractStatement built from the given string
     * @throws SyntaxException if the input string is has syntactic errors
     */
    public static AbstractStatement getStatementFromString(String statement) throws SyntaxException {
        StatementType statementType = getStatementType(statement);
        switch (Objects.requireNonNull(statementType)) {
            case AssignmentStatement:
                return AssignmentStatement.getAssignmentStatementFromString(statement);
            case CompoundStatement:
                return CompoundStatement.getCompoundStatementFromString(statement);
            case PrintStatement:
                return PrintStatement.getPrintStatementFromString(statement);
            case IfStatement:
                return IfStatement.getIfStatementFromString(statement);
            case OpenFileStatement:
                return OpenFileStatement.getOpenFileStatementFromString(statement);
            case CloseFileStatement:
                return CloseFileStatement.getCloseFileStatementFromString(statement);
            case ReadFileStatement:
                return ReadFileStatement.getReadFileStatementFromString(statement);
            case CallStatement:
                return CallStatement.getCallStatementFromString(statement);
            case LoadFunctionFromFileStatement:
                return LoadFunctionFromFileStatement.getLoadFunctionStatementFromString(statement);
            case ReturnStatement:
                return ReturnStatement.getReturnStatementFromString(statement);
            case NewHeapEntryStatement:
                return NewHeapEntryStatement.getNewHeapEntryStatementFromString(statement);
            case WriteHeapStatement:
                return WriteHeapStatement.getWriteHeapStatementFromString(statement);
            case WhileStatement:
                return WhileStatement.getWhileExpressionFromString(statement);
            case ForkStatement:
                return null;//todo
            case IncrementStatement:
                return null;//todo
            case DecrementStatement:
                return null;//todo
            case UndefinedStatement:
                return null;
        }
        return null;
    }

    public enum StatementType {
        AssignmentStatement, CallStatement, CloseFileStatement, CompoundStatement, DecrementStatement,
        ForkStatement, IfStatement, IncrementStatement, LoadFunctionFromFileStatement, NewHeapEntryStatement, OpenFileStatement,
        PrintStatement, ReadFileStatement, ReturnStatement, WhileStatement, WriteHeapStatement, UndefinedStatement
    }
}
