package model.expression;

import model.interfaces.HeapInterface;
import org.intellij.lang.annotations.RegExp;

import java.util.Map;

public class ReadHeapExpression extends AbstractExpression {
    @RegExp
    public static final String readHeapExpressionRegex = "readHeap\\([a-z]+\\)";
    private String key;

    public ReadHeapExpression(String varName) {
        this.key = varName;
    }

    @Override
    public String toString() {
        return "readHeap(" + key + ")";
    }

    @Override
    public int evaluate(Map<String, Integer> symbols, HeapInterface heap) {
        return heap.get(symbols.get(key));
    }
}
