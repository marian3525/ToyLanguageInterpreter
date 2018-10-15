package model.adt;

import java.util.Stack;

public class SStack<E> extends Stack {
    public SStack() {
        super();
    }
    /*
    @SuppressWarnings("unchecked")
    @Override
    public synchronized  E pop() {
        return (E) super.pop();
    }
    @SuppressWarnings("unchecked")
    @Override
    public E push(E item) {
        return (E) super.push(item);
    }
    */
}
