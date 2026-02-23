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
    private Map<String, Boolean> constantIdentifiers;
    private int currentScope;
    private Set<Integer> globalScopes;
    
    public SymbolTable() {
        this.identifiers = new HashSet<>();
        this.identifierScopes = new HashMap<>();
        this.constantIdentifiers = new HashMap<>();
        this.currentScope = 0;
        this.globalScopes = new HashSet<>();
    }
    
    public void add(String identifier) {
        addInCurrentScope(identifier);
    }
    
    public void addInCurrentScope(String identifier) {
        addInCurrentScope(identifier, false);
    }
    
    public void addInCurrentScope(String identifier, boolean isConstant) {
        if (identifier != null && !identifier.isEmpty()) {
            identifiers.add(identifier);
            identifierScopes.put(identifier, currentScope);
            constantIdentifiers.put(identifier, isConstant);
        }
    }
    
    public void addAsGlobal(String identifier) {
        addAsGlobal(identifier, false);
    }
    
    public void addAsGlobal(String identifier, boolean isConstant) {
        if (identifier != null && !identifier.isEmpty()) {
            identifiers.add(identifier);
            identifierScopes.put(identifier, 0);
            constantIdentifiers.put(identifier, isConstant);
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
    
    public boolean isConstant(String identifier) {
        return constantIdentifiers.getOrDefault(identifier, false);
    }
    
    public boolean isMutable(String identifier) {
        return !isConstant(identifier);
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
        constantIdentifiers.clear();
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
