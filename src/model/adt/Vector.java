package model.adt;

public class Vector<T> {
    private Vector<T> elems;
    public Vector(int init) {
        elems = new Vector<>(init);
    }
    public void add(T elem) {
        elems.add(elem);
    }
}
