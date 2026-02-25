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
    private Map<String, DataType> identifierTypes;
    private Map<String, Integer> identifierArraySizes;
    private int currentScope;
    private Set<Integer> globalScopes;
    
    public SymbolTable() {
        this.identifiers = new HashSet<>();
        this.identifierScopes = new HashMap<>();
        this.constantIdentifiers = new HashMap<>();
        this.identifierTypes = new HashMap<>();
        this.identifierArraySizes = new HashMap<>();
        this.currentScope = 0;
        this.globalScopes = new HashSet<>();
    }
    
    public void add(String identifier) {
        addInCurrentScope(identifier);
    }
    
    public void addInCurrentScope(String identifier) {
        addInCurrentScope(identifier, false, DataType.UNKNOWN);
    }
    
    public void addInCurrentScope(String identifier, boolean isConstant) {
        addInCurrentScope(identifier, isConstant, DataType.UNKNOWN);
    }
    
    public void addInCurrentScope(String identifier, boolean isConstant, String type) {
        DataType dt = (type != null) ? DataType.fromString(type) : DataType.UNKNOWN;
        addInCurrentScope(identifier, isConstant, dt);
    }
    
    public void addInCurrentScope(String identifier, boolean isConstant, DataType type) {
        addInCurrentScope(identifier, isConstant, type, 0);
    }
    
    public void addInCurrentScope(String identifier, boolean isConstant, DataType type, int arraySize) {
        if (identifier != null && !identifier.isEmpty()) {
            if (isInCurrentScope(identifier)) {
                throw new IllegalArgumentException("Duplicate declaration: '" + identifier + "' already declared in current scope");
            }
            identifiers.add(identifier);
            identifierScopes.put(identifier, currentScope);
            constantIdentifiers.put(identifier, isConstant);
            identifierTypes.put(identifier, type);
            identifierArraySizes.put(identifier, arraySize);
        }
    }
    
    public void addAsGlobal(String identifier) {
        addAsGlobal(identifier, false, DataType.UNKNOWN);
    }
    
    public void addAsGlobal(String identifier, boolean isConstant) {
        addAsGlobal(identifier, isConstant, DataType.UNKNOWN);
    }
    
    public void addAsGlobal(String identifier, boolean isConstant, String type) {
        DataType dt = (type != null) ? DataType.fromString(type) : DataType.UNKNOWN;
        addAsGlobal(identifier, isConstant, dt);
    }
    
    private void addAsGlobal(String identifier, boolean isConstant, DataType type) {
        addAsGlobal(identifier, isConstant, type, 0);
    }
    
    private void addAsGlobal(String identifier, boolean isConstant, DataType type, int arraySize) {
        if (identifier != null && !identifier.isEmpty()) {
            if (globalScopes.contains(0) && contains(identifier) && getScopeLevel(identifier) == 0) {
                throw new IllegalArgumentException("Duplicate declaration: '" + identifier + "' already declared in global scope");
            }
            identifiers.add(identifier);
            identifierScopes.put(identifier, 0);
            constantIdentifiers.put(identifier, isConstant);
            identifierTypes.put(identifier, type);
            identifierArraySizes.put(identifier, arraySize);
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
    
    public DataType getType(String identifier) {
        return identifierTypes.getOrDefault(identifier, DataType.UNKNOWN);
    }
    
    public int getArraySize(String identifier) {
        return identifierArraySizes.getOrDefault(identifier, 0);
    }
    
    public boolean isArray(String identifier) {
        return getArraySize(identifier) > 0;
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
        identifierTypes.clear();
        identifierArraySizes.clear();
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
