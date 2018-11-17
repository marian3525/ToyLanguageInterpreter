package model.interfaces;

import java.util.Map;

public interface HeapInterface {
    Integer get(int key);

    // used with new
    int put(Integer value);

    // used with writeHeap
    void put(Integer value, Integer addr);

    Map<Integer, Integer> getAll();

    Map<Integer, Integer> setContent(Map<Integer, Integer> newContent);

    Map<Integer, Integer> getContent();
}
