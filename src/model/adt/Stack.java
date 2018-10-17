package model.adt;

public class Stack<T> {
    private java.util.Stack<T> stack;
    public Stack() {
         stack = new java.util.Stack<>();
    }
    public T pop() {
        return stack.pop();
    }
    public T push(T elem) {
        return stack.push(elem);
    }

    public boolean isEmpty() {
        return stack.empty();
    }
    public T peek() {
        return stack.peek();
    }

    public Stack<T> clone() {
        /*
        1 2 3 4   4 3 2 1
         */
        Stack<T> out = new Stack<>();
        for (T e : stack) {
            out.push(e);
        }
        return out;
    }
}
