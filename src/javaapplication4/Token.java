/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javaapplication4;

/**
 * Represents a token in the lexical analysis
 * @author Gerardo
 */
public class Token {
    private String lexeme;
    private TokenType type;
    private int line;
    private int column;
    private String errorMessage;
    
    public Token(String lexeme, TokenType type, int line, int column) {
        this.lexeme = lexeme;
        this.type = type;
        this.line = line;
        this.column = column;
        this.errorMessage = null;
    }
    
    public Token(String lexeme, String errorMessage, int line, int column) {
        this.lexeme = lexeme;
        this.type = TokenType.ERROR;
        this.line = line;
        this.column = column;
        this.errorMessage = errorMessage;
    }
    
    public String getLexeme() {
        return lexeme;
    }
    
    public String getLexema() {
        return lexeme;
    }
    
    public TokenType getType() {
        return type;
    }
    
    public int getCode() {
        return type.getCode();
    }
    
    public String getTypeName() {
        return type.name();
    }
    
    public int getLine() {
        return line;
    }
    
    public int getColumn() {
        return column;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public boolean isError() {
        return type == TokenType.ERROR;
    }
    
    @Override
    public String toString() {
        if (isError()) {
            return String.format("Error en linea %d, col %d: %s - %s", 
                line, column, lexeme, errorMessage);
        }
        return String.format("Token[%s, '%s', code=%d, line=%d, col=%d]", 
            type.name(), lexeme, type.getCode(), line, column);
    }
}
