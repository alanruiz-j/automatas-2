/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javaapplication4;

import java.util.List;

/**
 * Analisis_Sintactico - Syntactic analysis wrapper class
 * Provides a simple interface for performing syntactic analysis
 * @author Gerardo
 */
public class Analisis_Sintactico {
    private Parser parser;
    private ASTNode ast;
    private List<SyntaxError> errors;
    private boolean success;
    
    public Analisis_Sintactico() {
        this.parser = null;
        this.ast = null;
        this.errors = null;
        this.success = false;
    }
    
    /**
     * Perform syntactic analysis on a list of tokens
     * @param tokens List of tokens from lexical analysis
     * @return true if parsing succeeded with no errors, false otherwise
     */
    public boolean analizar(List<Token> tokens) {
        // Remove EOF token if present for cleaner parsing
        if (!tokens.isEmpty() && tokens.get(tokens.size() - 1).getType() == TokenType.EOF) {
            tokens = tokens.subList(0, tokens.size() - 1);
        }
        
        // Create parser and parse
        parser = new Parser(tokens);
        ast = parser.parse();
        errors = parser.getErrors();
        success = !parser.hasErrors();
        
        return success;
    }
    
    /**
     * Get the Abstract Syntax Tree
     * @return AST root node, or null if parsing failed
     */
    public ASTNode getAST() {
        return ast;
    }
    
    /**
     * Get list of syntax errors
     * @return List of errors found during parsing
     */
    public List<SyntaxError> getErrors() {
        return errors;
    }
    
    /**
     * Check if analysis succeeded
     * @return true if no errors, false otherwise
     */
    public boolean isSuccess() {
        return success;
    }
    
    /**
     * Check if there are any errors
     * @return true if errors exist, false otherwise
     */
    public boolean hasErrors() {
        return errors != null && !errors.isEmpty();
    }
    
    /**
     * Get error count
     * @return Number of syntax errors
     */
    public int getErrorCount() {
        return errors != null ? errors.size() : 0;
    }
    
    /**
     * Print AST for debugging (to console)
     */
    public void printAST() {
        if (ast != null) {
            System.out.println("=== Abstract Syntax Tree ===");
            ast.printTree("");
        } else {
            System.out.println("No AST available (parsing failed)");
        }
    }
    
    /**
     * Print errors for debugging (to console)
     */
    public void printErrors() {
        if (errors != null && !errors.isEmpty()) {
            System.out.println("=== Syntax Errors ===");
            for (SyntaxError error : errors) {
                System.out.println(error);
            }
        } else {
            System.out.println("No syntax errors found");
        }
    }
}
