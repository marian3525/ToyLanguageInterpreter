package model.adt;

public class Pairs<K, V> {
    private K key;
    private V value;

    public Pairs(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }
}
