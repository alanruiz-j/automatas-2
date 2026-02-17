/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javaapplication4;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * A proper lexical scanner that performs character-by-character scanning
 * with a state machine for accurate token recognition.
 * @author Gerardo
 */
public class Scanner {
    
    private final String source;
    private final List<Token> tokens;
    private int start;
    private int current;
    private int line;
    private int column;
    private int startColumn;
    
    // Reserved words mapping
    private static final Map<String, TokenType> reservedWords;
    private static final Map<String, TokenType> operators;
    
    static {
        reservedWords = new HashMap<>();
        // Reserved words (English)
        reservedWords.put("structure", TokenType.PALABRA_RESERVADA);
        reservedWords.put("public", TokenType.PALABRA_RESERVADA);
        reservedWords.put("const", TokenType.PALABRA_RESERVADA);
        reservedWords.put("void", TokenType.PALABRA_RESERVADA);
        reservedWords.put("main", TokenType.PALABRA_RESERVADA);

        // Functions (English)
        reservedWords.put("print", TokenType.FUNCION_SALIDA);
        reservedWords.put("input", TokenType.FUNCION_ENTRADA);

        // Data types (English)
        reservedWords.put("number", TokenType.TIPO_DATO);
        
        // Control flow - Conditionals
        reservedWords.put("if", TokenType.CONDICIONAL);
        reservedWords.put("else", TokenType.CONDICIONAL);
        reservedWords.put("elseif", TokenType.CONDICIONAL);
        
        // Control flow - Loops
        reservedWords.put("while", TokenType.BUCLE);
        reservedWords.put("for", TokenType.BUCLE);
        
        // Control flow - Jump statements
        reservedWords.put("break", TokenType.SALTO);
        reservedWords.put("continue", TokenType.SALTO);
        reservedWords.put("return", TokenType.SALTO);
        
        // Boolean literals
        reservedWords.put("true", TokenType.BOOLEANO);
        reservedWords.put("false", TokenType.BOOLEANO);
        
        // Specific Data Types
        reservedWords.put("int", TokenType.TIPO_ENTERO);
        reservedWords.put("float", TokenType.TIPO_FLOTANTE);
        reservedWords.put("double", TokenType.TIPO_FLOTANTE);
        reservedWords.put("string", TokenType.TIPO_CADENA_TIPO);
        reservedWords.put("char", TokenType.TIPO_CARACTER);
        reservedWords.put("bool", TokenType.TIPO_BOOLEANO);
        reservedWords.put("array", TokenType.TIPO_ARREGLO);
        
        // Exception Handling
        reservedWords.put("try", TokenType.EXCEPCION);
        reservedWords.put("catch", TokenType.EXCEPCION);
        reservedWords.put("throw", TokenType.EXCEPCION);
        reservedWords.put("finally", TokenType.EXCEPCION);
        
        // Operators lookup
        operators = new HashMap<>();
        operators.put("=", TokenType.OPERADOR_ASIGNACION);
        operators.put("+", TokenType.OPERADOR_ARITMETICO);
        operators.put("-", TokenType.OPERADOR_ARITMETICO);
        operators.put("*", TokenType.OPERADOR_ARITMETICO);
        operators.put("/", TokenType.OPERADOR_ARITMETICO);
        operators.put("==", TokenType.OPERADOR_COMPARACION);
        operators.put("!=", TokenType.OPERADOR_COMPARACION);
        operators.put("<", TokenType.OPERADOR_COMPARACION);
        operators.put(">", TokenType.OPERADOR_COMPARACION);
        operators.put("<=", TokenType.OPERADOR_COMPARACION);
        operators.put(">=", TokenType.OPERADOR_COMPARACION);
        operators.put("&&", TokenType.OPERADOR_LOGICO);
        operators.put("||", TokenType.OPERADOR_LOGICO);
        operators.put("!", TokenType.OPERADOR_LOGICO);
    }
    
    public Scanner(String source) {
        this.source = source;
        this.tokens = new ArrayList<>();
        this.start = 0;
        this.current = 0;
        this.line = 1;
        this.column = 1;
        this.startColumn = 1;
    }
    
    public List<Token> scanTokens() {
        while (!isAtEnd()) {
            start = current;
            startColumn = column;
            scanToken();
        }
        
        tokens.add(new Token("", TokenType.EOF, line, column));
        return tokens;
    }
    
    private void scanToken() {
        char c = advance();
        
        switch (c) {
            // Single-character operators
            case '+':
            case '-':
                addToken(operators.get(String.valueOf(c)));
                break;
            case '*':
                addToken(TokenType.OPERADOR_ARITMETICO);
                break;
            case '/':
                if (match('/')) {
                    // Single-line comment, consume until end of line
                    while (peek() != '\n' && !isAtEnd()) advance();
                } else if (match('*')) {
                    // Multi-line comment
                    skipMultiLineComment();
                } else {
                    addToken(TokenType.OPERADOR_ARITMETICO);
                }
                break;
                
            // Multi-character operators starting with =
            case '=':
                if (match('=')) {
                    addToken(TokenType.OPERADOR_COMPARACION);
                } else {
                    addToken(TokenType.OPERADOR_ASIGNACION);
                }
                break;
                
            // Multi-character operators starting with !
            case '!':
                if (match('=')) {
                    addToken(TokenType.OPERADOR_COMPARACION);
                } else {
                    addToken(TokenType.OPERADOR_LOGICO);
                }
                break;
                
            // Comparison operators
            case '<':
                if (match('=')) {
                    addToken(TokenType.OPERADOR_COMPARACION);
                } else {
                    addToken(TokenType.OPERADOR_COMPARACION);
                }
                break;
            case '>':
                if (match('=')) {
                    addToken(TokenType.OPERADOR_COMPARACION);
                } else {
                    addToken(TokenType.OPERADOR_COMPARACION);
                }
                break;
                
            // Logical operators
            case '&':
                if (match('&')) {
                    addToken(TokenType.OPERADOR_LOGICO);
                } else {
                    addError("Unexpected character '&'");
                }
                break;
            case '|':
                if (match('|')) {
                    addToken(TokenType.OPERADOR_LOGICO);
                } else {
                    addError("Unexpected character '|'");
                }
                break;
                
            // Delimiters
            case '(':
                addToken(TokenType.APERTURA);
                break;
            case ')':
                addToken(TokenType.CIERRE);
                break;
            case '[':
            case ']':
                addToken(TokenType.AGRUPADOR);
                break;
            case ';':
                addToken(TokenType.PUNTO_Y_COMA);
                break;
            case ',':
                addToken(TokenType.COMA);
                break;
                
            // String literals
            case '"':
                string();
                break;
            case '\'':
                singleQuoteString();
                break;
                
            // Whitespace
            case ' ':
            case '\r':
            case '\t':
                // Ignore whitespace
                break;
            case '\n':
                line++;
                column = 1;
                break;
                
            default:
                if (isDigit(c)) {
                    number();
                } else if (isAlpha(c)) {
                    identifier();
                } else {
                    addError("Unexpected character: '" + c + "'");
                }
                break;
        }
    }
    
    private void string() {
        StringBuilder value = new StringBuilder();
        
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') {
                line++;
                column = 1;
            }
            if (peek() == '\\') {
                advance(); // consume backslash
                char escaped = advance();
                switch (escaped) {
                    case 'n': value.append('\n'); break;
                    case 't': value.append('\t'); break;
                    case 'r': value.append('\r'); break;
                    case '"': value.append('"'); break;
                    case '\\': value.append('\\'); break;
                    default: 
                        addError("Invalid escape sequence: \\'" + escaped + "'");
                        return;
                }
            } else {
                value.append(advance());
            }
        }
        
        if (isAtEnd()) {
            addError("Unterminated string");
            return;
        }
        
        advance(); // consume closing "
        
        // Create token with the processed string value
        tokens.add(new Token("\"" + value.toString() + "\"", TokenType.LITERAL_CADENA, line, startColumn));
    }
    
    private void singleQuoteString() {
        StringBuilder value = new StringBuilder();
        
        while (peek() != '\'' && !isAtEnd()) {
            if (peek() == '\n') {
                line++;
                column = 1;
            }
            if (peek() == '\\') {
                advance();
                char escaped = advance();
                switch (escaped) {
                    case 'n': value.append('\n'); break;
                    case 't': value.append('\t'); break;
                    case 'r': value.append('\r'); break;
                    case '\'': value.append('\''); break;
                    case '\\': value.append('\\'); break;
                    default:
                        addError("Invalid escape sequence: \\'" + escaped + "'");
                        return;
                }
            } else {
                value.append(advance());
            }
        }
        
        if (isAtEnd()) {
            addError("Unterminated string");
            return;
        }
        
        advance(); // consume closing '
        
        tokens.add(new Token("'" + value.toString() + "'", TokenType.LITERAL_CADENA, line, startColumn));
    }
    
    private void number() {
        while (isDigit(peek())) advance();
        
        boolean isDecimal = false;
        if (peek() == '.' && isDigit(peekNext())) {
            isDecimal = true;
            advance(); // consume '.'
            while (isDigit(peek())) advance();
        }
        
        // Scientific notation
        if (peek() == 'e' || peek() == 'E') {
            int savePos = current;
            advance();
            if (peek() == '+' || peek() == '-') advance();
            if (isDigit(peek())) {
                isDecimal = true;
                while (isDigit(peek())) advance();
            } else {
                // Not a valid exponent, backtrack
                current = savePos;
                column = startColumn + (current - start);
            }
        }
        
        String numberStr = source.substring(start, current);
        TokenType type = isDecimal ? TokenType.DECIMAL : TokenType.ENTERO;
        tokens.add(new Token(numberStr, type, line, startColumn));
    }
    
    private void identifier() {
        while (isAlphaNumeric(peek())) advance();
        
        String text = source.substring(start, current);
        TokenType type = reservedWords.getOrDefault(text, TokenType.IDENTIFICADOR);
        addToken(type);
    }
    
    private void skipMultiLineComment() {
        int nesting = 1;
        while (nesting > 0 && !isAtEnd()) {
            if (peek() == '/' && peekNext() == '*') {
                advance();
                advance();
                nesting++;
            } else if (peek() == '*' && peekNext() == '/') {
                advance();
                advance();
                nesting--;
            } else {
                if (peek() == '\n') {
                    line++;
                    column = 1;
                }
                advance();
            }
        }
        
        if (nesting > 0) {
            addError("Unterminated multi-line comment");
        }
    }
    
    private boolean match(char expected) {
        if (isAtEnd()) return false;
        if (source.charAt(current) != expected) return false;
        current++;
        column++;
        return true;
    }
    
    private char peek() {
        if (isAtEnd()) return '\0';
        return source.charAt(current);
    }
    
    private char peekNext() {
        if (current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1);
    }
    
    private char advance() {
        char c = source.charAt(current);
        current++;
        column++;
        return c;
    }
    
    private boolean isAtEnd() {
        return current >= source.length();
    }
    
    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }
    
    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') || 
               (c >= 'A' && c <= 'Z') || 
               c == '_' ||
               (c >= 'á' && c <= 'ú') ||
               (c >= 'Á' && c <= 'Ú') ||
               c == 'ñ' || c == 'Ñ';
    }
    
    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }
    
    private void addToken(TokenType type) {
        String text = source.substring(start, current);
        tokens.add(new Token(text, type, line, startColumn));
    }
    
    private void addError(String message) {
        String text = source.substring(start, current);
        tokens.add(new Token(text, message, line, startColumn));
    }
}
