package controller;

import exceptions.ProgramException;
import exceptions.UndefinedOperationException;
import exceptions.UndefinedVariableException;
import model.expression.ArithmeticExpression;
import model.expression.ConstantExpression;
import model.programState.ProgramState;
import model.statement.AssignmentStatement;
import model.statement.Statement;
import repository.Repository;

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
    public String infixToPostfix(String input) {
        Stack<Character> stack = new Stack<>();
        StringBuilder output = new StringBuilder();
        int i = 0;

        while (i < input.length()) {
            if (Character.isDigit(input.charAt(i))) {
                output.append(input.charAt(i));
            } else {
                //pop while there are elems with higher priority than the current one and output them
                while (stack.contains('+') && (input.charAt(i) == '+' || input.charAt(i) == '-')) {
                    output.append(stack.pop());
                }
                stack.push(input.charAt(i));
            }
            i++;
        }
        while(! stack.empty()) {
            output.append(stack.pop());
        }
        System.out.println(stack.toString());
        return output.toString();
    }
    public int computeExpression(Vector<Integer> postfix) {
        int result;
        Stack<Integer> stack = new Stack<>();
        int i;

        while(postfix.isEmpty()) {

        }
        return 3;
    }
    public Vector<String> tokenize(String input) {
        /**
         * Split the input string in its components separated with space used as separator
         */
        Vector<String> tokens = new Vector<>(10);
        String[] auxTok = input.split("[-+*/|\\s*]");

        for(String t : auxTok) {
            if(!t.contains(" "))
                tokens.add(t);
        }
        return tokens;
    }
}
