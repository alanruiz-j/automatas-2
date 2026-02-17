/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javaapplication4;

import java.util.List;
import java.util.ArrayList;

/**
 * Analisis_Lexico - Main lexical analysis class using the new Scanner
 * This class provides backward compatibility with the existing GUI while
 * using the new robust scanning engine.
 * @author Gerardo
 */
public class Analisis_Lexico
{
    // For backward compatibility with existing code
    public String lexema;
    public String nombre;
    public int numero;
    
    // Legacy fields - kept for compatibility (English keywords)
    String[][] diccionario = {
        {"structure", "PALABRA_RESERVADA", "1"},
        {"public", "PALABRA_RESERVADA", "1"},
        {"const", "PALABRA_RESERVADA", "1"},
        {"void", "PALABRA_RESERVADA", "1"},
        {"main", "PALABRA_RESERVADA", "1"},
        {"print", "FUNCION_SALIDA", "2"},
        {"input", "FUNCION_ENTRADA", "3"},
        {"number", "TIPO_DATO", "4"},
        {"10", "LITERAL_NUMERICO", "5"},
        {"\"..\"", "LITERAL_CADENA", "6"},
        {"=", "OPERADOR_ASIGNACION", "7"},
        {"+", "OPERADOR_ARITMETICO", "8"},
        {"-", "OPERADOR_ARITMETICO", "8"},
        {"*", "OPERADOR_ARITMETICO", "8"},
        {"/", "OPERADOR_ARITMETICO", "8"},
        {"(", "APERTURA", "9"},
        {")", "CIERRE", "9"},
        {"[", "AGRUPADOR", "9"},
        {"]", "AGRUPADOR", "9"},
        {";", "PUNTO_Y_COMA", "10"},
        {",", "SEPARADOR", "22"},
        {"==", "OPERADOR_COMPARACION", "20"},
        {"!=", "OPERADOR_COMPARACION", "20"},
        {"<", "OPERADOR_COMPARACION", "20"},
        {">", "OPERADOR_COMPARACION", "20"},
        {"<=", "OPERADOR_COMPARACION", "20"},
        {">=", "OPERADOR_COMPARACION", "20"},
        {"&&", "OPERADOR_LOGICO", "21"},
        {"||", "OPERADOR_LOGICO", "21"},
        {"!", "OPERADOR_LOGICO", "21"},
        // Control flow - Conditionals
        {"if", "CONDICIONAL", "30"},
        {"else", "CONDICIONAL", "30"},
        {"elseif", "CONDICIONAL", "30"},
        // Control flow - Loops
        {"while", "BUCLE", "31"},
        {"for", "BUCLE", "31"},
        // Control flow - Jump statements
        {"break", "SALTO", "32"},
        {"continue", "SALTO", "32"},
        {"return", "SALTO", "32"},
        // Boolean literals
        {"true", "BOOLEANO", "33"},
        {"false", "BOOLEANO", "33"},
        // Specific Data Types
        {"int", "TIPO_ENTERO", "40"},
        {"float", "TIPO_FLOTANTE", "41"},
        {"double", "TIPO_FLOTANTE", "41"},
        {"string", "TIPO_CADENA_TIPO", "42"},
        {"char", "TIPO_CARACTER", "43"},
        {"bool", "TIPO_BOOLEANO", "44"},
        {"array", "TIPO_ARREGLO", "45"},
        // Exception Handling
        {"try", "EXCEPCION", "60"},
        {"catch", "EXCEPCION", "60"},
        {"throw", "EXCEPCION", "60"},
        {"finally", "EXCEPCION", "60"},
    };
    
    /**
     * Legacy method for backward compatibility - analyzes a single token
     * This method is deprecated, use scan() for full text analysis instead
     */
    @Deprecated
    public Analisis_Lexico Analiza(String palabra) {
        Analisis_Lexico objLexico = new Analisis_Lexico();
        objLexico.lexema = palabra;
        
        // Try dictionary lookup first
        int index = 0;
        boolean bandera = false;
        
        while (index < diccionario.length) {
            if (palabra.equals(diccionario[index][0])) {
                bandera = true;
                objLexico.nombre = diccionario[index][1];
                objLexico.numero = Integer.parseInt(diccionario[index][2]);
                break;
            }
            index++;
        }
        
        if (bandera) {
            return objLexico;
        }
        
        // Use FSA for unknown tokens
        return analyzeWithFSA(objLexico, palabra);
    }
    
    /**
     * NEW: Scan entire text and return all tokens
     * This is the recommended method for full lexical analysis
     */
    public List<Token> scan(String source) {
        Scanner scanner = new Scanner(source);
        return scanner.scanTokens();
    }
    
    /**
     * NEW: Convert Token list to legacy format for GUI compatibility
     */
    public List<Analisis_Lexico> scanToLegacyFormat(String source) {
        List<Token> tokens = scan(source);
        List<Analisis_Lexico> legacyList = new ArrayList<>();
        
        for (Token token : tokens) {
            if (token.getType() == TokenType.EOF) continue;
            
            Analisis_Lexico legacy = new Analisis_Lexico();
            legacy.lexema = token.getLexeme();
            legacy.nombre = token.getTypeName();
            legacy.numero = token.getCode();
            legacyList.add(legacy);
        }
        
        return legacyList;
    }
    
    /**
     * Improved FSA with proper state machine
     */
    private Analisis_Lexico analyzeWithFSA(Analisis_Lexico objLexico, String palabra) {
        // State definitions
        final int START = 0;
        final int IN_INTEGER = 1;
        final int IN_DECIMAL = 2;
        final int IN_STRING = 3;
        final int IN_IDENTIFIER = 4;
        final int ERROR = 5;
        
        int state = START;
        StringBuilder recognized = new StringBuilder();
        
        for (int i = 0; i < palabra.length(); i++) {
            char c = palabra.charAt(i);
            
            switch (state) {
                case START:
                    if (isDigit(c)) {
                        state = IN_INTEGER;
                        recognized.append(c);
                    } else if (c == '"') {
                        state = IN_STRING;
                        recognized.append(c);
                    } else if (isAlpha(c)) {
                        state = IN_IDENTIFIER;
                        recognized.append(c);
                    } else {
                        state = ERROR;
                    }
                    break;
                    
                case IN_INTEGER:
                    if (isDigit(c)) {
                        recognized.append(c);
                    } else if (c == '.' && i + 1 < palabra.length() && isDigit(palabra.charAt(i + 1))) {
                        state = IN_DECIMAL;
                        recognized.append(c);
                    } else {
                        state = ERROR;
                    }
                    break;
                    
                case IN_DECIMAL:
                    if (isDigit(c)) {
                        recognized.append(c);
                    } else if (c == 'e' || c == 'E') {
                        // Scientific notation
                        if (i + 1 < palabra.length() && 
                            (palabra.charAt(i + 1) == '+' || palabra.charAt(i + 1) == '-' || isDigit(palabra.charAt(i + 1)))) {
                            recognized.append(c);
                        } else {
                            state = ERROR;
                        }
                    } else if ((c == '+' || c == '-') && i > 0 && 
                              (palabra.charAt(i - 1) == 'e' || palabra.charAt(i - 1) == 'E')) {
                        recognized.append(c);
                    } else {
                        state = ERROR;
                    }
                    break;
                    
                case IN_STRING:
                    recognized.append(c);
                    if (c == '"' && i > 0) {
                        // End of string (and not the opening quote)
                        state = START; // Will be caught as valid at end
                    }
                    break;
                    
                case IN_IDENTIFIER:
                    if (isAlphaNumeric(c)) {
                        recognized.append(c);
                    } else {
                        state = ERROR;
                    }
                    break;
                    
                case ERROR:
                    // Stay in error
                    break;
            }
        }
        
        // Determine final token type based on state
        switch (state) {
            case IN_INTEGER:
                objLexico.nombre = "ENTERO";
                objLexico.numero = 50;
                break;
            case IN_DECIMAL:
                objLexico.nombre = "DECIMAL";
                objLexico.numero = 51;
                break;
            case IN_STRING:
                if (recognized.length() > 1 && recognized.charAt(recognized.length() - 1) == '"') {
                    objLexico.nombre = "LITERAL_CADENA";
                    objLexico.numero = 6;
                } else {
                    objLexico.nombre = "ERROR_CADENA";
                    objLexico.numero = 102;
                }
                break;
            case IN_IDENTIFIER:
                objLexico.nombre = "IDENTIFICADOR";
                objLexico.numero = 11;
                break;
            default:
                objLexico.nombre = "ERROR_DESCONOCIDO";
                objLexico.numero = 100;
        }
        
        return objLexico;
    }
    
    // Helper methods
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
    
    public boolean CaracterEspecial(char caracter) {
        char[] PI = {'%', '[', ']', '_', '!'};
        for (char c : PI) {
            if (caracter == c) return true;
        }
        return false;
    }
}
