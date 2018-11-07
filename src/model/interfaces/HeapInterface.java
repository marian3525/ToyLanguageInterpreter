package model.interfaces;

import java.util.Map;

public interface HeapInterface {
    Integer get(int key);

    void put(Integer key, Integer value);

    Map<Integer, Integer> getAll();

    Map<Integer, Integer> setContent(Map<Integer, Integer> newContent);

    Map<Integer, Integer> getContent();
}
