package com.trinity.trinity.client.syntax;

public class SyntaxList {

    private SyntaxElement element;
    private String[] keys;

    public SyntaxList(SyntaxElement element, String[] keys) {
        this.element = element;
        this.keys = keys;
    }

    public SyntaxElement getElement() {
         return element;
    }

    public String[] getKeys() {
        return keys;
    }

    public boolean hasKey(String key) {
        for (String s : keys) {
            if (s.equalsIgnoreCase(key)) return true;
        }
        return false;
    }
}
