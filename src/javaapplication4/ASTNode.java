/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javaapplication4;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract Syntax Tree Node for syntactic analysis
 * Represents nodes in the parse tree
 * @author Gerardo
 */
public class ASTNode {
    
    public enum NodeType {
        PROGRAM,           // Root node
        STRUCTURE,         // structure definition
        FUNCTION,          // function definition
        PARAMETER_LIST,    // function parameters
        STATEMENT_LIST,    // list of statements
        DECLARATION,       // variable declaration
        ASSIGNMENT,        // variable assignment
        IF_STATEMENT,      // if-else
        WHILE_STATEMENT,   // while loop
        FOR_STATEMENT,     // for loop
        TRY_CATCH,         // try-catch-finally
        THROW_STATEMENT,   // throw
        BREAK_STATEMENT,   // break
        CONTINUE_STATEMENT,// continue
        RETURN_STATEMENT,  // return
        EXPRESSION,        // expression
        TERM,              // term in expression
        FACTOR,            // factor in expression
        IDENTIFIER,        // variable name
        NUMBER_LITERAL,    // numeric literal
        STRING_LITERAL,    // string literal
        CHAR_LITERAL,      // char literal
        BOOL_LITERAL,      // boolean literal
        FUNCTION_CALL,     // function invocation
        ARGUMENT_LIST,     // function arguments
        TYPE,              // data type
        ACCESS_MODIFIER,   // public/private
        ERROR              // error node
    }
    
    private NodeType type;
    private Token token;
    private List<ASTNode> children;
    private int line;
    private int column;
    private String value;
    
    public ASTNode(NodeType type) {
        this.type = type;
        this.children = new ArrayList<>();
        this.line = 0;
        this.column = 0;
        this.value = null;
    }
    
    public ASTNode(NodeType type, Token token) {
        this.type = type;
        this.token = token;
        this.children = new ArrayList<>();
        this.line = token != null ? token.getLine() : 0;
        this.column = token != null ? token.getColumn() : 0;
        this.value = token != null ? token.getLexeme() : null;
    }
    
    public ASTNode(NodeType type, String value, int line, int column) {
        this.type = type;
        this.value = value;
        this.line = line;
        this.column = column;
        this.children = new ArrayList<>();
    }
    
    public NodeType getType() {
        return type;
    }
    
    public void setType(NodeType type) {
        this.type = type;
    }
    
    public Token getToken() {
        return token;
    }
    
    public void setToken(Token token) {
        this.token = token;
        if (token != null) {
            this.line = token.getLine();
            this.column = token.getColumn();
            this.value = token.getLexeme();
        }
    }
    
    public List<ASTNode> getChildren() {
        return children;
    }
    
    public void addChild(ASTNode child) {
        if (child != null) {
            children.add(child);
        }
    }
    
    public ASTNode getChild(int index) {
        if (index >= 0 && index < children.size()) {
            return children.get(index);
        }
        return null;
    }
    
    public int getChildCount() {
        return children.size();
    }
    
    public int getLine() {
        return line;
    }
    
    public void setLine(int line) {
        this.line = line;
    }
    
    public int getColumn() {
        return column;
    }
    
    public void setColumn(int column) {
        this.column = column;
    }
    
    public String getValue() {
        return value;
    }
    
    public void setValue(String value) {
        this.value = value;
    }
    
    public boolean isError() {
        return type == NodeType.ERROR;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(type.name());
        if (value != null && !value.isEmpty()) {
            sb.append(" [").append(value).append("]");
        }
        sb.append(" (line:").append(line).append(")");
        return sb.toString();
    }
    
    /**
     * Print tree structure for debugging
     */
    public void printTree(String indent) {
        System.out.println(indent + this.toString());
        for (ASTNode child : children) {
            child.printTree(indent + "  ");
        }
    }
}
