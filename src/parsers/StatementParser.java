package parsers;

import exceptions.SyntaxException;
import model.statement.*;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class StatementParser {

    public enum StatementType {
        AssignmentStatement, CallStatement, CloseFileStatement, CompoundStatement, DecrementStatement,
        ForkStatement, IfStatement, IncrementStatement, LoadFunctionFromFileStatement, NewHeapEntryStatement, OpenFileStatement,
        PrintStatement, ReadFileStatement, ReturnStatement, WhileStatement, WriteHeapStatement, UndefinedStatement
    }
    /**
     * Used to decide on the type of a statement based on its semantics
     *
     * @param statementStr: statement as string
     * @return Assignment/Compound/If/Print depending on the statement type
     */
    private static StatementType getStatementType(@NotNull String statementStr) throws SyntaxException {

        if (OpenFileStatement.matchesString(statementStr)) {
            return StatementType.OpenFileStatement;
        }
        else if (ReadFileStatement.matchesString(statementStr)) {
            return StatementType.ReadFileStatement;
        }
        else if (CloseFileStatement.matchesString(statementStr)) {
            return StatementType.CloseFileStatement;
        }
        else if (AssignmentStatement.matchesString(statementStr))
            return StatementType.AssignmentStatement;
        else if (CompoundStatement.matchesString(statementStr)) {
            return StatementType.CompoundStatement;
        }
        else if (IfStatement.matchesString(statementStr)) {
            return StatementType.IfStatement;
        }
        else if (PrintStatement.matchesString(statementStr)) {
            return StatementType.PrintStatement;
        }
        else if (CallStatement.matchesString(statementStr)) {
            return StatementType.CallStatement;
        }
        else if (false) {
            return StatementType.LoadFunctionFromFileStatement;
        }
        else if (ReturnStatement.matchesString(statementStr)) {
            return StatementType.ReadFileStatement;
        }
        else if (NewHeapEntryStatement.matchesString(statementStr)) {
            return StatementType.NewHeapEntryStatement;
        }
        else if (WriteHeapStatement.matchesString(statementStr)) {
            return StatementType.WriteHeapStatement;
        }
        else if (WhileStatement.matchesString(statementStr)) {
            return StatementType.WhileStatement;
        } else if (ForkStatement.matchesString(statementStr)) {
            return StatementType.ForkStatement;
        } else
            throw new SyntaxException("Invalid syntax in: " + statementStr);
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
                return ForkStatement.getForkStatementFromString(statement);
            case IncrementStatement:
                return IncrementStatement.getIncrementStatementFromString(statement);
            case DecrementStatement:
                return DecrementStatement.getDecrementStatementFromString(statement);
            case UndefinedStatement:
                throw new SyntaxException("Syntax of:" + statement + "couldn't be matched to a statement class");
        }
        return null;
    }
}
