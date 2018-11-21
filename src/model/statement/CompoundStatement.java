package model.statement;

import exceptions.SyntaxException;
import exceptions.UndefinedOperationException;
import model.programState.ProgramState;
import parsers.StatementParser;

public class CompoundStatement extends AbstractStatement {
    private AbstractStatement first;
    private AbstractStatement second;
    private String functionName;

    public CompoundStatement(AbstractStatement first, AbstractStatement second) {
        this.first = first;
        this.second = second;
        this.functionName = "main";
    }

    public CompoundStatement(AbstractStatement first, AbstractStatement second, String functionName) {
        this.first = first;
        this.second = second;
        this.functionName = functionName;
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

        AbstractStatement firstStatement;
        AbstractStatement secondStatement;

        firstStatement = StatementParser.getStatementFromString(first);
        secondStatement = StatementParser.getStatementFromString(second);

        CompoundStatement s = new CompoundStatement(firstStatement, secondStatement);
        return s;
    }

    @Override
    public ProgramState execute(ProgramState programState) throws UndefinedOperationException {
        programState.getExecutionStack().push(second);
        programState.getExecutionStack().push(first);
        return programState;
    }

    @Override
    public String getFunction() {
        return functionName;
    }

    @Override
    public void setFunction(String functionName) {
        this.functionName = functionName;
    }

    @Override
    public String toString() {
        return "(" + first.toString() + ";" + second.toString() + ")";
    }
}
