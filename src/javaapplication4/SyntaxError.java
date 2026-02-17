/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javaapplication4;

/**
 * Represents a syntax error found during syntactic analysis
 * @author Gerardo
 */
public class SyntaxError {
    private String message;
    private int line;
    private int column;
    private String expected;
    private String found;
    
    public SyntaxError(String message, int line, int column, String expected, String found) {
        this.message = message;
        this.line = line;
        this.column = column;
        this.expected = expected;
        this.found = found;
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
    
    public String getExpected() {
        return expected;
    }
    
    public String getFound() {
        return found;
    }
    
    @Override
    public String toString() {
        return String.format("Error la linea %d, columna %d: %s (Esperaba: %s, Encontró: %s)",
            line, column, message, expected, found);
    }
}
