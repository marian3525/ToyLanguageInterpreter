package model.adt;

import java.util.Map;

public class HashMap<K,V> {
    private Map<K, V> table;

    public HashMap() {
        table = new java.util.HashMap<>();
    }

    public V remove(K key) {
        return table.remove(key);
    }

    public V put(K key, V val) {
        return table.put(key, val);
    }

    public boolean containsKey(K key) {
        return table.containsKey(key);
    }

    public int size() {
        return table.size();
    }

}
