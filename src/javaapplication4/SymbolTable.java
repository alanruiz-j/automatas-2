/*
 * SymbolTable - Symbol table for storing declared identifiers with scope tracking
 */
package javaapplication4;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SymbolTable {
    private Set<String> identifiers;
    private Map<String, Integer> identifierScopes;
    private int currentScope;
    private Set<Integer> globalScopes;
    
    public SymbolTable() {
        this.identifiers = new HashSet<>();
        this.identifierScopes = new HashMap<>();
        this.currentScope = 0;
        this.globalScopes = new HashSet<>();
    }
    
    public void add(String identifier) {
        addInCurrentScope(identifier);
    }
    
    public void addInCurrentScope(String identifier) {
        if (identifier != null && !identifier.isEmpty()) {
            identifiers.add(identifier);
            identifierScopes.put(identifier, currentScope);
        }
    }
    
    public void addAsGlobal(String identifier) {
        if (identifier != null && !identifier.isEmpty()) {
            identifiers.add(identifier);
            identifierScopes.put(identifier, 0);
            globalScopes.add(0);
        }
    }
    
    public boolean contains(String identifier) {
        return identifier != null && identifiers.contains(identifier);
    }
    
    public int getScopeLevel(String identifier) {
        return identifierScopes.getOrDefault(identifier, -1);
    }
    
    public boolean isAccessible(String identifier, int fromScope) {
        if (!contains(identifier)) {
            return false;
        }
        int declScope = getScopeLevel(identifier);
        return declScope <= fromScope;
    }
    
    public boolean isInCurrentScope(String identifier) {
        return contains(identifier) && getScopeLevel(identifier) == currentScope;
    }
    
    public boolean isInInnerScope(String identifier, int fromScope) {
        if (!contains(identifier)) {
            return false;
        }
        int declScope = getScopeLevel(identifier);
        return declScope > fromScope;
    }
    
    public void enterScope() {
        currentScope++;
    }
    
    public void enterScope(int scopeLevel) {
        if (scopeLevel > currentScope) {
            currentScope = scopeLevel;
        }
    }
    
    public void exitScope() {
        if (currentScope > 0) {
            currentScope--;
        }
    }
    
    public int getCurrentScope() {
        return currentScope;
    }
    
    public void setCurrentScope(int scope) {
        this.currentScope = scope;
    }
    
    public void clear() {
        identifiers.clear();
        identifierScopes.clear();
        currentScope = 0;
        globalScopes.clear();
    }
    
    public int size() {
        return identifiers.size();
    }
    
    public boolean isEmpty() {
        return identifiers.isEmpty();
    }
    
    public Map<String, Integer> getAllIdentifierScopes() {
        return new HashMap<>(identifierScopes);
    }
}
