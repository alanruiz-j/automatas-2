/*
 * Analisis_Semantico - Semantic analysis for assignment statements
 * Validates that identifiers used in assignments have been previously declared
 */
package javaapplication4;

import java.util.ArrayList;
import java.util.List;

public class Analisis_Semantico {
    private SymbolTable symbolTable;
    private List<SemanticError> errors;
    private boolean success;
    
    public Analisis_Semantico() {
        this.symbolTable = new SymbolTable();
        this.errors = new ArrayList<>();
        this.success = false;
    }
    
    /**
     * Validates that an identifier exists in the symbol table.
     * This is the first step of semantic analysis for assignments.
     * 
     * @param identifierName The name of the identifier on the left-hand side of the assignment
     * @param table Reference to the symbol table
     * @return true if the identifier exists, false if it doesn't (generates semantic error)
     */
    public boolean validarIdentificadorAsignacion(String identifierName, SymbolTable table) {
        if (identifierName == null || identifierName.isEmpty()) {
            return false;
        }
        
        if (!table.contains(identifierName)) {
            SemanticError error = new SemanticError(
                identifierName,
                "La variable no ha sido declarada",
                0,
                0
            );
            errors.add(error);
            return false;
        }
        
        return true;
    }
    
    /**
     * Performs semantic analysis on the AST.
     * First pass: builds symbol table from DECLARATION nodes.
     * Second pass: validates ASSIGNMENTS using the symbol table.
     * 
     * @param ast The root of the Abstract Syntax Tree
     * @return true if semantic analysis passed, false if errors were found
     */
    public boolean analizar(ASTNode ast) {
        errors.clear();
        symbolTable.clear();
        success = false;
        
        if (ast == null) {
            return false;
        }
        
        buildSymbolTable(ast);
        validateAssignments(ast);
        
        success = errors.isEmpty();
        return success;
    }
    
    /**
     * First pass: Traverse AST and collect all declared identifiers
     */
    private void buildSymbolTable(ASTNode node) {
        if (node == null) return;
        
        if (node.getType() == ASTNode.NodeType.DECLARATION) {
            for (int i = 0; i < node.getChildCount(); i++) {
                ASTNode child = node.getChild(i);
                if (child.getType() == ASTNode.NodeType.IDENTIFIER) {
                    String identifierName = child.getValue();
                    if (identifierName != null) {
                        symbolTable.add(identifierName);
                    }
                }
            }
        }
        
        for (ASTNode child : node.getChildren()) {
            buildSymbolTable(child);
        }
    }
    
    /**
     * Second pass: Validate all assignment statements
     */
    private void validateAssignments(ASTNode node) {
        if (node == null) return;
        
        if (node.getType() == ASTNode.NodeType.ASSIGNMENT) {
            if (node.getChildCount() > 0) {
                ASTNode identifierNode = node.getChild(0);
                if (identifierNode.getType() == ASTNode.NodeType.IDENTIFIER) {
                    String identifierName = identifierNode.getValue();
                    
                    boolean valid = validarIdentificadorAsignacion(identifierName, symbolTable);
                    
                    if (!valid) {
                        SemanticError error = new SemanticError(
                            identifierName,
                            "La variable '" + identifierName + "' no ha sido declarada",
                            identifierNode.getLine(),
                            identifierNode.getColumn()
                        );
                        errors.add(error);
                    }
                }
            }
        }
        
        for (ASTNode child : node.getChildren()) {
            validateAssignments(child);
        }
    }
    
    public List<SemanticError> getErrors() {
        return errors;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public SymbolTable getSymbolTable() {
        return symbolTable;
    }
}
