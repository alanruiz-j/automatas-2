/*
 * Analisis_Semantico - Semantic analysis for assignment statements
 * Step 1: Validates that identifiers exist in the symbol table
 * Step 2: Validates that identifiers are in accessible scope
 * Step 3: Validates that identifiers are modifiable (not constant)
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
     * STEP 1: Validates that an identifier exists in the symbol table.
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
     * STEP 2: Validates that a declared identifier is within the correct scope.
     * Checks that the variable is visible and accessible from the point where
     * the assignment is made.
     * 
     * The variable must belong to:
     * - The current block (same scope level)
     * - A parent block (lower scope level)
     * 
     * The variable CANNOT be from an inner block that has already ended.
     * 
     * @param identifierName The name of the identifier on the left-hand side
     * @param currentScope The current scope level where the assignment occurs
     * @param table Reference to the symbol table with scope information
     * @return true if the identifier is in accessible scope, false if out of scope
     */
    public boolean validarAlcanceAsignacion(String identifierName, int currentScope, SymbolTable table) {
        if (identifierName == null || identifierName.isEmpty()) {
            return false;
        }
        
        if (!table.contains(identifierName)) {
            return false;
        }
        
        int declScope = table.getScopeLevel(identifierName);
        
        if (declScope > currentScope) {
            SemanticError error = new SemanticError(
                identifierName,
                "La variable '" + identifierName + "' esta fuera de alcance (declarada en un bloque interno)",
                0,
                0
            );
            errors.add(error);
            return false;
        }
        
        return true;
    }
    
    /**
     * STEP 3: Validates that a declared identifier is modifiable.
     * Checks that the variable is not declared as constant (const).
     * 
     * Constants cannot be reassigned after initialization.
     * 
     * @param identifierName The name of the identifier on the left-hand side
     * @param table Reference to the symbol table with constant information
     * @return true if the identifier is mutable, false if it's constant
     */
    public boolean validarModificabilidadAsignacion(String identifierName, SymbolTable table) {
        if (identifierName == null || identifierName.isEmpty()) {
            return false;
        }
        
        if (!table.contains(identifierName)) {
            return false;
        }
        
        if (table.isConstant(identifierName)) {
            SemanticError error = new SemanticError(
                identifierName,
                "La variable '" + identifierName + "' es una constante y no puede ser modificada",
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
     * First pass: builds symbol table from DECLARATION nodes with scope tracking.
     * Second pass: validates ASSIGNMENTS using both step 1 and step 2.
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
     * First pass: Traverse AST and collect all declared identifiers with scope tracking.
     * Tracks scope levels:
     * - Level 0: Function body (global to function)
     * - Level 1+: Nested blocks (if, while, for, try, etc.)
     * Also tracks whether variables are constant.
     */
    private void buildSymbolTable(ASTNode node) {
        if (node == null) return;
        
        if (node.getType() == ASTNode.NodeType.DECLARATION) {
            boolean isConst = node.isConstant();
            for (int i = 0; i < node.getChildCount(); i++) {
                ASTNode child = node.getChild(i);
                if (child.getType() == ASTNode.NodeType.IDENTIFIER) {
                    String identifierName = child.getValue();
                    if (identifierName != null) {
                        symbolTable.addInCurrentScope(identifierName, isConst);
                    }
                }
            }
        }
        
        if (isBlockNode(node.getType())) {
            symbolTable.enterScope();
        }
        
        for (ASTNode child : node.getChildren()) {
            buildSymbolTable(child);
        }
        
        if (isBlockNode(node.getType())) {
            symbolTable.exitScope();
        }
    }
    
    /**
     * Check if a node type represents a block that creates a new scope
     */
    private boolean isBlockNode(ASTNode.NodeType type) {
        return type == ASTNode.NodeType.IF_STATEMENT ||
               type == ASTNode.NodeType.WHILE_STATEMENT ||
               type == ASTNode.NodeType.FOR_STATEMENT ||
               type == ASTNode.NodeType.TRY_CATCH ||
               type == ASTNode.NodeType.FUNCTION;
    }
    
    /**
     * Second pass: Validate all assignment statements.
     * Performs:
     * - Step 1: Validate identifier exists in symbol table
     * - Step 2: Validate identifier is in accessible scope
     * - Step 3: Validate identifier is modifiable (not constant)
     */
    private void validateAssignments(ASTNode node) {
        if (node == null) return;
        
        if (node.getType() == ASTNode.NodeType.ASSIGNMENT) {
            if (node.getChildCount() > 0) {
                ASTNode identifierNode = node.getChild(0);
                if (identifierNode.getType() == ASTNode.NodeType.IDENTIFIER) {
                    String identifierName = identifierNode.getValue();
                    
                    boolean step1Valid = validarIdentificadorAsignacion(identifierName, symbolTable);
                    
                    if (step1Valid) {
                        int currentScope = symbolTable.getCurrentScope();
                        boolean step2Valid = validarAlcanceAsignacion(identifierName, currentScope, symbolTable);
                        
                        if (!step2Valid) {
                            SemanticError error = new SemanticError(
                                identifierName,
                                "La variable '" + identifierName + "' esta fuera de alcance",
                                identifierNode.getLine(),
                                identifierNode.getColumn()
                            );
                            errors.add(error);
                        } else {
                            boolean step3Valid = validarModificabilidadAsignacion(identifierName, symbolTable);
                            
                            if (!step3Valid) {
                                SemanticError error = new SemanticError(
                                    identifierName,
                                    "La variable '" + identifierName + "' es una constante y no puede ser modificada",
                                    identifierNode.getLine(),
                                    identifierNode.getColumn()
                                );
                                errors.add(error);
                            }
                        }
                    } else {
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
        
        if (isBlockNode(node.getType())) {
            symbolTable.enterScope();
        }
        
        for (ASTNode child : node.getChildren()) {
            validateAssignments(child);
        }
        
        if (isBlockNode(node.getType())) {
            symbolTable.exitScope();
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
