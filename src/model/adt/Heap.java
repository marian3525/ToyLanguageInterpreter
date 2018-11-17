package model.adt;

import model.interfaces.HeapInterface;

import java.util.HashMap;
import java.util.Map;

public class Heap implements HeapInterface {
    HashMap<Integer, Integer> data;
    private int currentIdx;

    public Heap() {
        data = new HashMap<>();
        currentIdx = 0;
    }

    @Override
    public Integer get(int key) {
        return data.get(key);
    }

    /**
     * Put the value into the value and return the address
     *
     * @param value
     * @return
     */
    @Override
    public int put(Integer value) {
        currentIdx++;
        data.put(currentIdx, value);
        return currentIdx;
    }

    @Override
    public void put(Integer value, Integer addr) {
        data.put(addr, value);
    }

    public Map<Integer, Integer> getAll() {
        return data;
    }

    @Override
    public Map<Integer, Integer> setContent(Map<Integer, Integer> newContent) {
        data = (HashMap<Integer, Integer>) newContent;
        return data;
    }

    @Override
    public Map<Integer, Integer> getContent() {
        return data;
    }
}
