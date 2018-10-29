package model.adt;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class Vector<T> implements Iterable<T> {
    private java.util.Vector<T> elems;

    public Vector() {
        elems = new java.util.Vector<>();
    }
    public Vector(int init) {
        elems = new java.util.Vector<>(init);
    }
    public void add(T elem) {
        elems.add(elem);
    }

    public int size() {
        return elems.size();
    }

    public T elementAt(int i) {
        return elems.elementAt(i);
    }

    @SuppressWarnings("unchecked")
    public T[] toArray() {
        return (T[]) elems.toArray();
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return elems.iterator();
    }
}
