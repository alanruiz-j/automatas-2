/*
 * SemanticError - Represents a semantic error found during semantic analysis
 */
package javaapplication4;

public class SemanticError {
    private String identifier;
    private String message;
    private int line;
    private int column;
    
    public SemanticError(String identifier, String message, int line, int column) {
        this.identifier = identifier;
        this.message = message;
        this.line = line;
        this.column = column;
    }
    
    public String getIdentifier() {
        return identifier;
    }
    
    public String getMessage() {
        return message;
    }
    
    public int getLine() {
        return line;
    }
    
    public int getColumn() {
        return column;
    }
    
    @Override
    public String toString() {
        return String.format("Error en linea %d, columna %d: %s (identificador: '%s')",
            line, column, message, identifier);
    }
}
