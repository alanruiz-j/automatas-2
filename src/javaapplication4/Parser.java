/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javaapplication4;

import java.util.ArrayList;
import java.util.List;

/**
 * Recursive descent parser for syntactic analysis
 * Implements the grammar of the programming language
 * @author Gerardo
 */
public class Parser {
    private List<Token> tokens;
    private int current;
    private List<SyntaxError> errors;
    private ASTNode ast;
    
    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.current = 0;
        this.errors = new ArrayList<>();
        this.ast = null;
    }
    
    /**
     * Main entry point - parses the entire program
     */
    public ASTNode parse() {
        try {
            ast = parseProgram();
            if (!isAtEnd()) {
                error("Tokens inesperados después del fin del programa", peek());
            }
            return ast;
        } catch (ParseException e) {
            return null;
        }
    }
    
    /**
     * PROGRAM ::= STRUCTURE_DEFINITION
     */
    private ASTNode parseProgram() {
        ASTNode program = new ASTNode(ASTNode.NodeType.PROGRAM);
        ASTNode structure = parseStructure();
        if (structure != null) {
            program.addChild(structure);
        }
        return program;
    }
    
    /**
     * STRUCTURE_DEFINITION ::= "structure" IDENTIFIER "(" MEMBER_LIST ")"
     */
    private ASTNode parseStructure() {
        ASTNode structure = new ASTNode(ASTNode.NodeType.STRUCTURE);
        
        // Expect 'structure'
        if (!match(TokenType.PALABRA_RESERVADA, "structure")) {
            error("Se esperaba la palabra clave 'structure' al inicio del programa", peek());
            return null;
        }
        // 'structure' already consumed by match()
        
        // Expect structure name (identifier)
        Token nameToken = consume(TokenType.IDENTIFICADOR, "Se esperaba nombre de estructura");
        if (nameToken != null) {
            structure.addChild(new ASTNode(ASTNode.NodeType.IDENTIFIER, nameToken));
        }
        
        // Expect '('
        consume(TokenType.APERTURA, "Se esperaba '(' después del nombre de estructura");
        
        // Parse member list
        ASTNode members = parseMemberList();
        if (members != null) {
            structure.addChild(members);
        }
        
        // Expect ')'
        consume(TokenType.CIERRE, "Se esperaba ')' para cerrar la estructura");
        
        return structure;
    }
    
    /**
     * MEMBER_LIST ::= (DECLARATION | FUNCTION_DEFINITION)*
     */
    private ASTNode parseMemberList() {
        ASTNode members = new ASTNode(ASTNode.NodeType.STATEMENT_LIST);
        
        while (!check(TokenType.CIERRE) && !isAtEnd()) {
            ASTNode member = null;
            
            if (isType(peek())) {
                // Could be declaration or function return type
                if (isFunctionDefinition()) {
                    member = parseFunction();
                } else {
                    member = parseDeclaration();
                }
            } else if (check(TokenType.PALABRA_RESERVADA, "public") || 
                       check(TokenType.PALABRA_RESERVADA, "private")) {
                // Function with access modifier - parseFunction will consume the modifier
                member = parseFunction();
            }
            
            if (member != null) {
                members.addChild(member);
            } else {
                // Skip unknown token and continue
                error("Token inesperado en el cuerpo de la estructura", peek());
                advance();
            }
        }
        
        return members;
    }
    
    /**
     * Check if current position starts a function definition
     */
    private boolean isFunctionDefinition() {
        // Look ahead: type IDENTIFIER '[' ']' or access modifier
        int save = current;
        boolean result = false;
        
        // Check for access modifier
        if (match(TokenType.PALABRA_RESERVADA, "public") || 
            match(TokenType.PALABRA_RESERVADA, "private")) {
            // Continue checking
        }
        
        // Check for 'const'
        if (match(TokenType.PALABRA_RESERVADA, "const")) {
            // Continue
        }
        
        // Check for type
        if (isType(peek())) {
            advance();
            // Check for 'main' or identifier
            if (check(TokenType.IDENTIFICADOR) || check(TokenType.PALABRA_RESERVADA)) {
                Token next = peek();
                if (next.getLexeme().equals("main")) {
                    result = true;
                }
            }
        }
        
        current = save;
        return result;
    }
    
    /**
     * FUNCTION_DEFINITION ::= ACCESS_MODIFIER "const" TYPE "main" "[" "]" "(" STATEMENT_LIST ")"
     */
    private ASTNode parseFunction() {
        ASTNode function = new ASTNode(ASTNode.NodeType.FUNCTION);
        
        // Optional access modifier
        if (match(TokenType.PALABRA_RESERVADA, "public") || 
            match(TokenType.PALABRA_RESERVADA, "private")) {
            Token access = previous();
            function.addChild(new ASTNode(ASTNode.NodeType.ACCESS_MODIFIER, access));
        }
        
        // Expect 'const'
        consume(TokenType.PALABRA_RESERVADA, "const", "Se esperaba 'const' en la definición de función");
        
        // Expect return type
        Token returnType = parseType();
        if (returnType != null) {
            function.addChild(new ASTNode(ASTNode.NodeType.TYPE, returnType));
        }
        
        // Expect 'main' or function name
        Token funcName = null;
        // First try 'main' as reserved word
        if (match(TokenType.PALABRA_RESERVADA, "main")) {
            funcName = previous();
        } else if (check(TokenType.IDENTIFICADOR)) {
            // Other function names as identifiers
            funcName = consume(TokenType.IDENTIFICADOR);
        } else {
            error("Se esperaba nombre de función (main o identificador)", peek());
        }
        
        if (funcName != null) {
            function.addChild(new ASTNode(ASTNode.NodeType.IDENTIFIER, funcName));
        }
        
        // Expect '[' ']'
        consume(TokenType.AGRUPADOR, "[", "Se esperaba '[' después del nombre de función");
        consume(TokenType.AGRUPADOR, "]", "Se esperaba ']' después de '['");
        
        // Expect '('
        consume(TokenType.APERTURA, "Se esperaba '(' para iniciar el cuerpo de la función");
        
        // Parse statement list
        ASTNode body = parseStatementList();
        if (body != null) {
            function.addChild(body);
        }
        
        // Expect ')'
        consume(TokenType.CIERRE, "Se esperaba ')' para cerrar el cuerpo de la función");
        
        return function;
    }
    
    /**
     * STATEMENT_LIST ::= STATEMENT*
     */
    private ASTNode parseStatementList() {
        ASTNode statements = new ASTNode(ASTNode.NodeType.STATEMENT_LIST);
        
        while (!check(TokenType.CIERRE) && !isAtEnd()) {
            ASTNode statement = parseStatement();
            if (statement != null) {
                statements.addChild(statement);
            }
        }
        
        return statements;
    }
    
    /**
     * STATEMENT ::= DECLARATION | ASSIGNMENT | IF_STATEMENT | WHILE_STATEMENT | 
     *               FOR_STATEMENT | TRY_CATCH | THROW_STATEMENT | BREAK_STATEMENT | 
     *               CONTINUE_STATEMENT | RETURN_STATEMENT | FUNCTION_CALL
     */
    private ASTNode parseStatement() {
        if (isAtEnd()) return null;
        
        Token token = peek();
        
        // Try-catch-finally
        if (match(TokenType.EXCEPCION, "try")) {
            return parseTryCatch();
        }
        
        // Throw statement
        if (match(TokenType.SALTO, "throw")) {
            return parseThrowStatement();
        }
        
        // Break statement
        if (match(TokenType.SALTO, "break")) {
            return parseBreakStatement();
        }
        
        // Continue statement
        if (match(TokenType.SALTO, "continue")) {
            return parseContinueStatement();
        }
        
        // Return statement
        if (match(TokenType.SALTO, "return")) {
            return parseReturnStatement();
        }
        
        // If statement
        if (match(TokenType.CONDICIONAL, "if")) {
            return parseIfStatement();
        }
        
        // While statement
        if (match(TokenType.BUCLE, "while")) {
            return parseWhileStatement();
        }
        
        // For statement
        if (match(TokenType.BUCLE, "for")) {
            return parseForStatement();
        }
        
        // Check if it's a type declaration
        if (isType(token)) {
            // Look ahead to see if it's declaration or assignment
            int save = current;
            advance(); // consume type
            if (check(TokenType.IDENTIFICADOR)) {
                advance(); // consume identifier
                if (check(TokenType.OPERADOR_ASIGNACION)) {
                    current = save;
                    return parseDeclaration();
                } else {
                    current = save;
                    return parseDeclaration();
                }
            }
            current = save;
            return parseDeclaration();
        }
        
        // Assignment or function call (starts with identifier)
        if (check(TokenType.IDENTIFICADOR)) {
            int save = current;
            advance(); // consume identifier
            if (check(TokenType.OPERADOR_ASIGNACION)) {
                current = save;
                return parseAssignment();
            }
            current = save;
            // Try as expression statement or function call
            return parseExpressionStatement();
        }
        
        // Function calls: print or input
        if (match(TokenType.FUNCION_SALIDA, "print")) {
            return parsePrintStatement();
        }
        
        if (match(TokenType.FUNCION_ENTRADA, "input")) {
            return parseInputStatement();
        }
        
        // Unknown statement - skip and report error
        error("Token inesperado en la instrucción", token);
        advance();
        return null;
    }
    
    /**
     * DECLARATION ::= TYPE IDENTIFIER ("=" EXPRESSION)? ";"
     */
    private ASTNode parseDeclaration() {
        ASTNode declaration = new ASTNode(ASTNode.NodeType.DECLARATION);
        
        // Parse type
        Token typeToken = parseType();
        if (typeToken != null) {
            declaration.addChild(new ASTNode(ASTNode.NodeType.TYPE, typeToken));
        }
        
        // Parse variable name
        Token name = consume(TokenType.IDENTIFICADOR, "Se esperaba nombre de variable después del tipo");
        if (name != null) {
            declaration.addChild(new ASTNode(ASTNode.NodeType.IDENTIFIER, name));
        }
        
        // Optional initialization
        if (match(TokenType.OPERADOR_ASIGNACION, "=")) {
            ASTNode init = parseExpression();
            if (init != null) {
                declaration.addChild(init);
            }
        }
        
        // Expect ';'
        consume(TokenType.PUNTO_Y_COMA, "Se esperaba ';' después de la declaración");
        
        return declaration;
    }
    
    /**
     * ASSIGNMENT ::= IDENTIFIER "=" EXPRESSION ";"
     */
    private ASTNode parseAssignment() {
        ASTNode assignment = new ASTNode(ASTNode.NodeType.ASSIGNMENT);
        
        Token name = consume(TokenType.IDENTIFICADOR, "Se esperaba nombre de variable");
        if (name != null) {
            assignment.addChild(new ASTNode(ASTNode.NodeType.IDENTIFIER, name));
        }
        
        consume(TokenType.OPERADOR_ASIGNACION, "Se esperaba '=' en la asignación");
        
        ASTNode expr = parseExpression();
        if (expr != null) {
            assignment.addChild(expr);
        }
        
        consume(TokenType.PUNTO_Y_COMA, "Se esperaba ';' después de la asignación");
        
        return assignment;
    }
    
    /**
     * IF_STATEMENT ::= "if" "(" EXPRESSION ")" "(" STATEMENT_LIST ")" (ELSE_PART)?
     */
    private ASTNode parseIfStatement() {
        ASTNode ifStmt = new ASTNode(ASTNode.NodeType.IF_STATEMENT);
        
        // 'if' already consumed
        consume(TokenType.APERTURA, "Se esperaba '(' después de 'if'");
        
        ASTNode condition = parseExpression();
        if (condition != null) {
            ifStmt.addChild(condition);
        }
        
        consume(TokenType.CIERRE, "Se esperaba ')' después de la condición if");
        consume(TokenType.APERTURA, "Se esperaba '(' para iniciar el cuerpo del if");
        
        ASTNode thenBranch = parseStatementList();
        if (thenBranch != null) {
            ifStmt.addChild(thenBranch);
        }
        
        consume(TokenType.CIERRE, "Se esperaba ')' para cerrar el cuerpo del if");
        
        // Optional else or elseif
        if (match(TokenType.CONDICIONAL, "else")) {
            ASTNode elseBranch = parseElsePart();
            if (elseBranch != null) {
                ifStmt.addChild(elseBranch);
            }
        } else if (match(TokenType.CONDICIONAL, "elseif")) {
            ASTNode elseIfBranch = parseIfStatement();
            if (elseIfBranch != null) {
                // Mark as elseif
                elseIfBranch.setType(ASTNode.NodeType.IF_STATEMENT);
                ifStmt.addChild(elseIfBranch);
            }
        }
        
        return ifStmt;
    }
    
    /**
     * ELSE_PART ::= "else" "(" STATEMENT_LIST ")"
     */
    private ASTNode parseElsePart() {
        // 'else' already consumed
        consume(TokenType.APERTURA, "Se esperaba '(' después de 'else'");
        
        ASTNode elseBody = parseStatementList();
        
        consume(TokenType.CIERRE, "Se esperaba ')' para cerrar el cuerpo del else");
        
        return elseBody;
    }
    
    /**
     * WHILE_STATEMENT ::= "while" "(" EXPRESSION ")" "(" STATEMENT_LIST ")"
     */
    private ASTNode parseWhileStatement() {
        ASTNode whileStmt = new ASTNode(ASTNode.NodeType.WHILE_STATEMENT);
        
        // 'while' already consumed
        consume(TokenType.APERTURA, "Se esperaba '(' después de 'while'");
        
        ASTNode condition = parseExpression();
        if (condition != null) {
            whileStmt.addChild(condition);
        }
        
        consume(TokenType.CIERRE, "Se esperaba ')' después de la condición while");
        consume(TokenType.APERTURA, "Se esperaba '(' para iniciar el cuerpo del while");
        
        ASTNode body = parseStatementList();
        if (body != null) {
            whileStmt.addChild(body);
        }
        
        consume(TokenType.CIERRE, "Se esperaba ')' para cerrar el cuerpo del while");
        
        return whileStmt;
    }
    
    /**
     * FOR_STATEMENT ::= "for" "(" ASSIGNMENT ";" EXPRESSION ";" ASSIGNMENT ")" "(" STATEMENT_LIST ")"
     */
    private ASTNode parseForStatement() {
        ASTNode forStmt = new ASTNode(ASTNode.NodeType.FOR_STATEMENT);
        
        // 'for' already consumed
        consume(TokenType.APERTURA, "Se esperaba '(' después de 'for'");
        
        // Initialization
        ASTNode init = parseAssignment();
        if (init != null) {
            forStmt.addChild(init);
        }
        
        // Condition
        ASTNode condition = parseExpression();
        if (condition != null) {
            forStmt.addChild(condition);
        }
        
        consume(TokenType.PUNTO_Y_COMA, "Se esperaba ';' después de la condición del for");
        
        // Increment
        ASTNode increment = parseAssignment();
        if (increment != null) {
            forStmt.addChild(increment);
        }
        
        consume(TokenType.CIERRE, "Se esperaba ')' después de las cláusulas del for");
        consume(TokenType.APERTURA, "Se esperaba '(' para iniciar el cuerpo del for");
        
        ASTNode body = parseStatementList();
        if (body != null) {
            forStmt.addChild(body);
        }
        
        consume(TokenType.CIERRE, "Se esperaba ')' para cerrar el cuerpo del for");
        
        return forStmt;
    }
    
    /**
     * TRY_CATCH ::= "try" "(" STATEMENT_LIST ")" "catch" "(" STATEMENT_LIST ")" (FINALLY)?
     */
    private ASTNode parseTryCatch() {
        ASTNode tryCatch = new ASTNode(ASTNode.NodeType.TRY_CATCH);
        
        // 'try' already consumed
        consume(TokenType.APERTURA, "Se esperaba '(' después de 'try'");
        
        ASTNode tryBody = parseStatementList();
        if (tryBody != null) {
            tryCatch.addChild(tryBody);
        }
        
        consume(TokenType.CIERRE, "Se esperaba ')' para cerrar el cuerpo del try");
        consume(TokenType.EXCEPCION, "catch", "Se esperaba 'catch' después del bloque try");
        consume(TokenType.APERTURA, "Se esperaba '(' después de 'catch'");
        
        ASTNode catchBody = parseStatementList();
        if (catchBody != null) {
            tryCatch.addChild(catchBody);
        }
        
        consume(TokenType.CIERRE, "Se esperaba ')' para cerrar el cuerpo del catch");
        
        // Optional finally
        if (match(TokenType.EXCEPCION, "finally")) {
            consume(TokenType.APERTURA, "Se esperaba '(' después de 'finally'");
            
            ASTNode finallyBody = parseStatementList();
            if (finallyBody != null) {
                tryCatch.addChild(finallyBody);
            }
            
            consume(TokenType.CIERRE, "Se esperaba ')' para cerrar el cuerpo del finally");
        }
        
        return tryCatch;
    }
    
    /**
     * THROW_STATEMENT ::= "throw" EXPRESSION ";"
     */
    private ASTNode parseThrowStatement() {
        ASTNode throwStmt = new ASTNode(ASTNode.NodeType.THROW_STATEMENT);
        
        // 'throw' already consumed
        ASTNode expr = parseExpression();
        if (expr != null) {
            throwStmt.addChild(expr);
        }
        
        consume(TokenType.PUNTO_Y_COMA, "Se esperaba ';' después de la instrucción throw");
        
        return throwStmt;
    }
    
    /**
     * BREAK_STATEMENT ::= "break" ";"
     */
    private ASTNode parseBreakStatement() {
        ASTNode breakStmt = new ASTNode(ASTNode.NodeType.BREAK_STATEMENT, previous());
        consume(TokenType.PUNTO_Y_COMA, "Se esperaba ';' después de break");
        return breakStmt;
    }
    
    /**
     * CONTINUE_STATEMENT ::= "continue" ";"
     */
    private ASTNode parseContinueStatement() {
        ASTNode continueStmt = new ASTNode(ASTNode.NodeType.CONTINUE_STATEMENT, previous());
        consume(TokenType.PUNTO_Y_COMA, "Se esperaba ';' después de continue");
        return continueStmt;
    }
    
    /**
     * RETURN_STATEMENT ::= "return" (EXPRESSION)? ";"
     */
    private ASTNode parseReturnStatement() {
        ASTNode returnStmt = new ASTNode(ASTNode.NodeType.RETURN_STATEMENT, previous());
        
        // Optional return value
        if (!check(TokenType.PUNTO_Y_COMA)) {
            ASTNode expr = parseExpression();
            if (expr != null) {
                returnStmt.addChild(expr);
            }
        }
        
        consume(TokenType.PUNTO_Y_COMA, "Se esperaba ';' después de return");
        return returnStmt;
    }
    
    /**
     * PRINT_STATEMENT ::= "print" "[" EXPRESSION_LIST "]"
     */
    private ASTNode parsePrintStatement() {
        ASTNode printStmt = new ASTNode(ASTNode.NodeType.FUNCTION_CALL, previous());
        
        consume(TokenType.AGRUPADOR, "[", "Se esperaba '[' después de 'print'");
        
        ASTNode args = parseExpressionList();
        if (args != null) {
            printStmt.addChild(args);
        }
        
        consume(TokenType.AGRUPADOR, "]", "Se esperaba ']' después de los argumentos de print");
        consume(TokenType.PUNTO_Y_COMA, "Se esperaba ';' después de la instrucción print");
        
        return printStmt;
    }
    
    /**
     * INPUT_STATEMENT ::= "input" IDENTIFIER
     */
    private ASTNode parseInputStatement() {
        ASTNode inputStmt = new ASTNode(ASTNode.NodeType.FUNCTION_CALL, previous());
        
        Token varName = consume(TokenType.IDENTIFICADOR, "Se esperaba nombre de variable después de 'input'");
        if (varName != null) {
            inputStmt.addChild(new ASTNode(ASTNode.NodeType.IDENTIFIER, varName));
        }
        
        consume(TokenType.PUNTO_Y_COMA, "Se esperaba ';' después de la instrucción input");
        
        return inputStmt;
    }
    
    /**
     * EXPRESSION_STATEMENT ::= EXPRESSION ";"
     */
    private ASTNode parseExpressionStatement() {
        ASTNode expr = parseExpression();
        consume(TokenType.PUNTO_Y_COMA, "Se esperaba ';' después de la expresión");
        return expr;
    }
    
    /**
     * EXPRESSION_LIST ::= EXPRESSION ("," EXPRESSION)*
     */
    private ASTNode parseExpressionList() {
        ASTNode list = new ASTNode(ASTNode.NodeType.ARGUMENT_LIST);
        
        if (check(TokenType.AGRUPADOR) && peek().getLexeme().equals("]")) {
            return list; // Empty list
        }
        
        ASTNode first = parseExpression();
        if (first != null) {
            list.addChild(first);
        }
        
        while (match(TokenType.COMA, ",")) {
            ASTNode next = parseExpression();
            if (next != null) {
                list.addChild(next);
            }
        }
        
        return list;
    }
    
    /**
     * EXPRESSION ::= TERM (("+" | "-" | "==" | "!=" | "<" | ">" | "<=" | ">=") TERM)*
     */
    private ASTNode parseExpression() {
        ASTNode left = parseTerm();
        
        while (match(TokenType.OPERADOR_ARITMETICO, "+") || 
               match(TokenType.OPERADOR_ARITMETICO, "-") ||
               match(TokenType.OPERADOR_COMPARACION, "==") ||
               match(TokenType.OPERADOR_COMPARACION, "!=") ||
               match(TokenType.OPERADOR_COMPARACION, "<") ||
               match(TokenType.OPERADOR_COMPARACION, ">") ||
               match(TokenType.OPERADOR_COMPARACION, "<=") ||
               match(TokenType.OPERADOR_COMPARACION, ">=") ||
               match(TokenType.OPERADOR_LOGICO, "&&") ||
               match(TokenType.OPERADOR_LOGICO, "||")) {
            Token operator = previous();
            ASTNode right = parseTerm();
            
            ASTNode binaryOp = new ASTNode(ASTNode.NodeType.EXPRESSION, operator);
            if (left != null) binaryOp.addChild(left);
            if (right != null) binaryOp.addChild(right);
            left = binaryOp;
        }
        
        return left;
    }
    
    /**
     * TERM ::= FACTOR (("*" | "/") FACTOR)*
     */
    private ASTNode parseTerm() {
        ASTNode left = parseFactor();
        
        while (match(TokenType.OPERADOR_ARITMETICO, "*") || 
               match(TokenType.OPERADOR_ARITMETICO, "/")) {
            Token operator = previous();
            ASTNode right = parseFactor();
            
            ASTNode binaryOp = new ASTNode(ASTNode.NodeType.TERM, operator);
            if (left != null) binaryOp.addChild(left);
            if (right != null) binaryOp.addChild(right);
            left = binaryOp;
        }
        
        return left;
    }
    
    /**
     * FACTOR ::= IDENTIFIER | NUMBER | STRING | CHAR | BOOL | "(" EXPRESSION ")" | "!" FACTOR
     */
    private ASTNode parseFactor() {
        // Boolean literals
        if (match(TokenType.BOOLEANO, "true") || match(TokenType.BOOLEANO, "false")) {
            return new ASTNode(ASTNode.NodeType.BOOL_LITERAL, previous());
        }
        
        // String literal
        if (check(TokenType.LITERAL_CADENA)) {
            return new ASTNode(ASTNode.NodeType.STRING_LITERAL, advance());
        }
        
        // Number literal
        if (check(TokenType.LITERAL_NUMERICO) || check(TokenType.ENTERO) || check(TokenType.DECIMAL)) {
            return new ASTNode(ASTNode.NodeType.NUMBER_LITERAL, advance());
        }
        
        // Identifier
        if (check(TokenType.IDENTIFICADOR)) {
            return new ASTNode(ASTNode.NodeType.IDENTIFIER, advance());
        }
        
        // Parenthesized expression
        if (match(TokenType.APERTURA, "(")) {
            ASTNode expr = parseExpression();
            consume(TokenType.CIERRE, "Se esperaba ')' después de la expresión");
            return expr;
        }
        
        // Negation
        if (match(TokenType.OPERADOR_LOGICO, "!")) {
            ASTNode factor = parseFactor();
            ASTNode negation = new ASTNode(ASTNode.NodeType.EXPRESSION, previous());
            if (factor != null) negation.addChild(factor);
            return negation;
        }
        
        error("Se esperaba una expresión", peek());
        return null;
    }
    
    /**
     * Parse type token
     */
    private Token parseType() {
        if (isType(peek())) {
            return advance();
        }
        error("Se esperaba declaración de tipo", peek());
        return null;
    }
    
    /**
     * Check if token is a type keyword
     */
    private boolean isType(Token token) {
        if (token == null) return false;
        // Check for standard data types
        if (token.getType() == TokenType.TIPO_DATO ||      // number
            token.getType() == TokenType.TIPO_ENTERO ||    // int
            token.getType() == TokenType.TIPO_FLOTANTE ||  // float, double
            token.getType() == TokenType.TIPO_CADENA_TIPO || // string
            token.getType() == TokenType.TIPO_CARACTER ||  // char
            token.getType() == TokenType.TIPO_BOOLEANO ||  // bool
            token.getType() == TokenType.TIPO_ARREGLO) {   // array
            return true;
        }
        // Check for void as PALABRA_RESERVADA
        if (token.getType() == TokenType.PALABRA_RESERVADA && 
            token.getLexeme().equals("void")) {
            return true;
        }
        return false;
    }
    
    // ==================== UTILITY METHODS ====================
    
    private boolean match(TokenType type, String lexeme) {
        if (check(type, lexeme)) {
            advance();
            return true;
        }
        return false;
    }
    
    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return peek().getType() == type;
    }
    
    private boolean check(TokenType type, String lexeme) {
        if (isAtEnd()) return false;
        Token token = peek();
        return token.getType() == type && token.getLexeme().equals(lexeme);
    }
    
    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }
    
    private boolean isAtEnd() {
        return current >= tokens.size();
    }
    
    private Token peek() {
        if (current >= tokens.size()) return tokens.get(tokens.size() - 1);
        return tokens.get(current);
    }
    
    private Token previous() {
        if (current == 0) return tokens.get(0);
        return tokens.get(current - 1);
    }
    
    private Token consume(TokenType type) {
        return consume(type, "Se esperaba tipo de token: " + type.name());
    }
    
    private Token consume(TokenType type, String errorMessage) {
        if (check(type)) return advance();
        error(errorMessage, peek());
        return null;
    }
    
    private Token consume(TokenType type, String lexeme, String errorMessage) {
        if (check(type, lexeme)) return advance();
        error(errorMessage, peek());
        return null;
    }
    
    private void error(String message, Token token) {
        String found = token != null ? token.getLexeme() : "EOF";
        int line = token != null ? token.getLine() : 0;
        int column = token != null ? token.getColumn() : 0;
        
        SyntaxError error = new SyntaxError(message, line, column, "ver mensaje de error", found);
        errors.add(error);
    }
    
    public boolean hasErrors() {
        return !errors.isEmpty();
    }
    
    public List<SyntaxError> getErrors() {
        return errors;
    }
    
    public ASTNode getAST() {
        return ast;
    }
}

/**
 * Exception for parse errors
 */
class ParseException extends RuntimeException {
    public ParseException(String message) {
        super(message);
    }
}
