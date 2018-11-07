package model.adt;

import model.interfaces.HeapInterface;

import java.util.HashMap;
import java.util.Map;

public class Heap implements HeapInterface {
    HashMap<Integer, Integer> data;

    public Heap() {
        data = new HashMap<>();
    }

    @Override
    public Integer get(int key) {
        return data.get(key);
    }

    @Override
    public void put(Integer key, Integer value) {
        data.put(key, value);
    }

    public Map<Integer, Integer> getAll() {
        return data;
    }

    @Override
    public Map<Integer, Integer> setContent(Map<Integer, Integer> newContent) {
        return null;
    }

    @Override
    public Map<Integer, Integer> getContent() {
        return null;
    }
}
