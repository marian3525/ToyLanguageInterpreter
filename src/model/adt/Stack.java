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
    public boolean empty() {
        return stack.empty();
    }
    public T peek() {
        return stack.peek();
    }
}
