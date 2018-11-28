package model.statement;

import exceptions.SyntaxException;
import model.programState.ProgramState;
import org.intellij.lang.annotations.RegExp;
import parsers.StatementParser;

public class ForkStatement extends AbstractStatement {
    @RegExp
    private static final String forkStatementRegex = "fork\\(.*\\)$";
    private AbstractStatement statement;

    public ForkStatement(AbstractStatement statement) {
        this.statement = statement;
    }

    /**
     * Syntax: fork(statement)
     * @param input: statement string
     * @return
     */
    public static ForkStatement getForkStatementFromString(String input) throws SyntaxException {
        String statementStr = input.replace(" ", "").
                replace("fork(", "").replaceAll("\\)$", "");
        AbstractStatement param = StatementParser.getStatementFromString(statementStr);
        return new ForkStatement(param);
    }

    public static boolean matchesString(String statementStr) {
        return statementStr.matches(forkStatementRegex);
    }

    @Override
    public String getFunction() {
        return null;
    }

    @Override
    public void setFunction(String functionName) {

    }

    @Override
    public String toString() {
        return "fork(" + statement.toString() + ")";
    }

    @Override
    public ProgramState execute(ProgramState programState) {
        //create new program state with the copy constructor which does the appropriate copying and referencing
        ProgramState newState = new ProgramState(programState);
        newState.getExecutionStack().push(statement);
        return newState;
    }
}
