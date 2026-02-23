/*
 * SymbolTable - Simple symbol table for storing declared identifiers
 */
package javaapplication4;

import java.util.HashSet;
import java.util.Set;

public class SymbolTable {
    private Set<String> identifiers;
    
    public SymbolTable() {
        this.identifiers = new HashSet<>();
    }
    
    public void add(String identifier) {
        if (identifier != null && !identifier.isEmpty()) {
            identifiers.add(identifier);
        }
    }
    
    public boolean contains(String identifier) {
        return identifier != null && identifiers.contains(identifier);
    }
    
    public void clear() {
        identifiers.clear();
    }
    
    public int size() {
        return identifiers.size();
    }
    
    public boolean isEmpty() {
        return identifiers.isEmpty();
    }
}
