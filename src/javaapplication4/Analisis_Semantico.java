/*
 * Analisis_Semantico - Semantic analysis for assignment statements
 * Step 1: Validates that identifiers exist in the symbol table
 * Step 2: Validates that identifiers are in accessible scope
 * Step 3: Validates that identifiers are modifiable (not constant)
 * Step 4: Validates type compatibility between LHS and RHS
 * Step 5: Validates binary expression type compatibility
 */
package javaapplication4;

import java.util.ArrayList;
import java.util.List;

public class Analisis_Semantico {
    
    private enum OperatorCategory {
        ARITHMETIC,  // +, -, *, /
        LOGICAL,     // &&, ||
        RELATIONAL,  // <, >, <=, >=
        EQUALITY     // ==, !=
    }
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
     * STEP 4: Validates type compatibility between the variable and the expression.
     * Strictly compares the declared type of the variable with the inferred type
     * of the expression on the right-hand side. No implicit conversions or promotions
     * are allowed.
     * 
     * @param identifierName The name of the identifier on the left-hand side
     * @param expressionNode The AST node representing the expression on the right-hand side
     * @param table Reference to the symbol table with type information
     * @return true if types match exactly, false if there's a type mismatch
     */
    public boolean validarTiposAsignacion(String identifierName, ASTNode expressionNode, SymbolTable table) {
        if (identifierName == null || identifierName.isEmpty() || expressionNode == null) {
            return false;
        }
        
        if (!table.contains(identifierName)) {
            return false;
        }
        
        DataType declaredType = table.getType(identifierName);
        DataType expressionType = inferExpressionType(expressionNode, table);
        
        if (declaredType == null || expressionType == null || 
            declaredType == DataType.UNKNOWN || expressionType == DataType.UNKNOWN) {
            return false;
        }
        
        if (declaredType != expressionType) {
            if (!isTypeCompatible(declaredType, expressionType)) {
                SemanticError error = new SemanticError(
                    identifierName,
                    "Tipo incompatible: se esperaba '" + declaredType.getTypeName() + "' pero se obtuvo '" + expressionType.getTypeName() + "'",
                    0,
                    0
                );
                errors.add(error);
                return false;
            }
            return true;
        }
        
        return true;
    }
    
    /**
     * Infers the type of an expression node.
     * Handles identifiers, literals, and binary expressions.
     * 
     * @param node The expression node to analyze
     * @param table Reference to the symbol table
     * @return The inferred DataType, or DataType.UNKNOWN if cannot be determined
     */
    private DataType inferExpressionType(ASTNode node, SymbolTable table) {
        if (node == null) {
            return DataType.UNKNOWN;
        }
        
        switch (node.getType()) {
            case IDENTIFIER:
                return table.getType(node.getValue());
                
            case NUMBER_LITERAL:
                return inferNumericLiteralType(node);
                
            case STRING_LITERAL:
                return DataType.STRING;
                
            case CHAR_LITERAL:
                return DataType.CHAR;
                
            case BOOL_LITERAL:
                return DataType.BOOL;
                
            case EXPRESSION:
            case TERM:
                return inferBinaryExpressionType(node, table);
                
            default:
                return DataType.UNKNOWN;
        }
    }
    
    /**
     * Infers the type of a numeric literal based on its value.
     * 
     * @param node The numeric literal node
     * @return The inferred DataType (INT, FLOAT, DOUBLE, or NUMBER)
     */
    private DataType inferNumericLiteralType(ASTNode node) {
        if (node == null || node.getValue() == null) {
            return DataType.NUMBER;
        }
        
        String value = node.getValue();
        
        if (value.contains(".") || value.contains("e") || value.contains("E")) {
            if (value.toLowerCase().endsWith("f")) {
                return DataType.FLOAT;
            }
            return DataType.DOUBLE;
        }
        
        try {
            Long.parseLong(value);
            return DataType.INT;
        } catch (NumberFormatException e) {
            return DataType.NUMBER;
        }
    }
    
    /**
     * Infers the type of a binary expression with strict operator-operand validation.
     * 
     * Rules:
     * - Arithmetic (+,-,*,/): both operands must be same numeric type, result is that type
     * - Logical (&&,||): both operands must be BOOL, result is BOOL
     * - Relational (<,>,<=,>=): both operands must be same numeric type, result is BOOL
     * - Equality (==,!=): both operands must be same type, result is BOOL
     * 
     * @param node The binary expression node
     * @param table Reference to the symbol table
     * @return The result DataType, or DataType.UNKNOWN if validation fails
     */
    private DataType inferBinaryExpressionType(ASTNode node, SymbolTable table) {
        if (node.getChildCount() < 2) {
            return DataType.UNKNOWN;
        }
        
        ASTNode left = node.getChild(0);
        ASTNode right = node.getChild(1);
        
        DataType leftType = inferExpressionType(left, table);
        DataType rightType = inferExpressionType(right, table);
        
        if (leftType == DataType.UNKNOWN || rightType == DataType.UNKNOWN) {
            return DataType.UNKNOWN;
        }
        
        String operator = node.getToken() != null ? node.getToken().getLexeme() : null;
        if (operator == null) {
            return DataType.UNKNOWN;
        }
        
        OperatorCategory category = getOperatorCategory(operator);
        
        switch (category) {
            case ARITHMETIC:
                return validateArithmeticOperation(operator, leftType, rightType, node);
            case LOGICAL:
                return validateLogicalOperation(operator, leftType, rightType, node);
            case RELATIONAL:
                return validateRelationalOperation(operator, leftType, rightType, node);
            case EQUALITY:
                return validateEqualityOperation(operator, leftType, rightType, node);
            default:
                return DataType.UNKNOWN;
        }
    }
    
    /**
     * Validates arithmetic operations (+, -, *, /).
     * Both operands must be the same numeric type.
     * 
     * @param operator The arithmetic operator
     * @param leftType Type of left operand
     * @param rightType Type of right operand
     * @param node The expression node for error reporting
     * @return Result type if valid, DataType.UNKNOWN otherwise
     */
    private DataType validateArithmeticOperation(String operator, DataType leftType, DataType rightType, ASTNode node) {
        if (!isNumericType(leftType) || !isNumericType(rightType)) {
            generateBinaryOpError(operator, leftType, rightType, node);
            return DataType.UNKNOWN;
        }
        
        if (leftType != rightType) {
            generateBinaryOpError(operator, leftType, rightType, node);
            return DataType.UNKNOWN;
        }
        
        return leftType;
    }
    
    /**
     * Validates logical operations (&&, ||).
     * Both operands must be BOOL.
     * 
     * @param operator The logical operator
     * @param leftType Type of left operand
     * @param rightType Type of right operand
     * @param node The expression node for error reporting
     * @return BOOL if valid, DataType.UNKNOWN otherwise
     */
    private DataType validateLogicalOperation(String operator, DataType leftType, DataType rightType, ASTNode node) {
        if (leftType != DataType.BOOL || rightType != DataType.BOOL) {
            generateBinaryOpError(operator, leftType, rightType, node);
            return DataType.UNKNOWN;
        }
        
        return DataType.BOOL;
    }
    
    /**
     * Validates relational operations (<, >, <=, >=).
     * Both operands must be the same numeric type.
     * Result is BOOL.
     * 
     * @param operator The relational operator
     * @param leftType Type of left operand
     * @param rightType Type of right operand
     * @param node The expression node for error reporting
     * @return BOOL if valid, DataType.UNKNOWN otherwise
     */
    private DataType validateRelationalOperation(String operator, DataType leftType, DataType rightType, ASTNode node) {
        if (!isNumericType(leftType) || !isNumericType(rightType)) {
            generateBinaryOpError(operator, leftType, rightType, node);
            return DataType.UNKNOWN;
        }
        
        if (leftType != rightType) {
            generateBinaryOpError(operator, leftType, rightType, node);
            return DataType.UNKNOWN;
        }
        
        return DataType.BOOL;
    }
    
    /**
     * Validates equality operations (==, !=).
     * Both operands must be exactly the same type.
     * Result is BOOL.
     * 
     * @param operator The equality operator
     * @param leftType Type of left operand
     * @param rightType Type of right operand
     * @param node The expression node for error reporting
     * @return BOOL if valid, DataType.UNKNOWN otherwise
     */
    private DataType validateEqualityOperation(String operator, DataType leftType, DataType rightType, ASTNode node) {
        if (leftType != rightType) {
            generateBinaryOpError(operator, leftType, rightType, node);
            return DataType.UNKNOWN;
        }
        
        return DataType.BOOL;
    }
    
    /**
     * Generates a semantic error for binary operator type mismatch.
     */
    private void generateBinaryOpError(String operator, DataType leftType, DataType rightType, ASTNode node) {
        String errorMsg = "Operador '" + operator + "' no aplicable a operandos de tipo '" + 
                         leftType.getTypeName() + "' y '" + rightType.getTypeName() + "'";
        
        int line = node.getToken() != null ? node.getToken().getLine() : 0;
        int column = node.getToken() != null ? node.getToken().getColumn() : 0;
        
        SemanticError error = new SemanticError("expresion", errorMsg, line, column);
        errors.add(error);
    }
    
    /**
     * Checks if a DataType is numeric (INT, FLOAT, DOUBLE, NUMBER).
     */
    private boolean isNumericType(DataType type) {
        return type == DataType.INT || type == DataType.FLOAT || 
               type == DataType.DOUBLE || type == DataType.NUMBER;
    }
    
    /**
     * Checks if two types are compatible for assignment.
     * Allows DOUBLE to be assigned to FLOAT (widening conversion).
     */
    private boolean isTypeCompatible(DataType declaredType, DataType expressionType) {
        if (declaredType == expressionType) {
            return true;
        }
        
        if (declaredType == DataType.FLOAT && expressionType == DataType.DOUBLE) {
            return true;
        }
        
        return false;
    }
    
    /**
     * Categorizes an operator by its type.
     */
    private OperatorCategory getOperatorCategory(String operator) {
        if (operator == null) {
            return null;
        }
        
        switch (operator) {
            case "+":
            case "-":
            case "*":
            case "/":
                return OperatorCategory.ARITHMETIC;
            case "&&":
            case "||":
                return OperatorCategory.LOGICAL;
            case "<":
            case ">":
            case "<=":
            case ">=":
                return OperatorCategory.RELATIONAL;
            case "==":
            case "!=":
                return OperatorCategory.EQUALITY;
            default:
                return null;
        }
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
     * Also tracks whether variables are constant and their type.
     */
    private void buildSymbolTable(ASTNode node) {
        if (node == null) return;
        
        if (node.getType() == ASTNode.NodeType.DECLARATION) {
            boolean isConst = node.isConstant();
            DataType varType = node.getDataType();
            for (int i = 0; i < node.getChildCount(); i++) {
                ASTNode child = node.getChild(i);
                if (child.getType() == ASTNode.NodeType.IDENTIFIER) {
                    String identifierName = child.getValue();
                    if (identifierName != null) {
                        symbolTable.addInCurrentScope(identifierName, isConst, varType);
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
     * - Step 4: Validate type compatibility between LHS and RHS
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
                            } else {
                                if (node.getChildCount() > 1) {
                                    ASTNode expressionNode = node.getChild(1);
                                    boolean step4Valid = validarTiposAsignacion(identifierName, expressionNode, symbolTable);
                                    
                                    if (!step4Valid) {
                                        SemanticError error = new SemanticError(
                                            identifierName,
                                            "Tipo incompatible en la asignacion",
                                            identifierNode.getLine(),
                                            identifierNode.getColumn()
                                        );
                                        errors.add(error);
                                    }
                                }
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
