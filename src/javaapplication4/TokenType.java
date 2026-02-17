/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javaapplication4;

/**
 * Represents the type of a token in the lexical analysis.
 * This is the ENGLISH VERSION of the programming language.
 * 
 * Language Keywords:
 * - structure: Structure/class definition
 * - public: Public access modifier
 * - const: Constant declaration
 * - void: No return value
 * - main: Entry point function
 * - print: Output function
 * - input: Input function
 * - number: Numeric data type
 * 
 * Data Types:
 * - int: Integer type (32-bit)
 * - float: Floating point (32-bit)
 * - double: Double precision (64-bit)
 * - string: String/text type
 * - char: Character type
 * - bool: Boolean type
 * - array: Array/collection type
 * 
 * Control Flow:
 * - if, else, elseif: Conditional statements
 * - while, for: Loop statements
 * - break, continue: Loop control
 * - return: Function return
 * 
 * Exception Handling:
 * - try: Try block
 * - catch: Exception handler
 * - throw: Throw exception
 * - finally: Finally block
 * 
 * Boolean Literals:
 * - true, false
 * 
 * @author Gerardo
 */
public enum TokenType {
    // Reserved words (structure, public, const, void, main)
    PALABRA_RESERVADA(1),
    
    // Output function (print)
    FUNCION_SALIDA(2),
    
    // Input function (input)
    FUNCION_ENTRADA(3),
    
    // Data types (number)
    TIPO_DATO(4),
    
    // Control flow - Conditionals (if, else, elseif)
    CONDICIONAL(30),
    
    // Control flow - Loops (while, for)
    BUCLE(31),
    
    // Control flow - Jump statements (break, continue, return)
    SALTO(32),
    
    // Boolean literals (true, false)
    BOOLEANO(33),
    
    // Specific Data Types
    TIPO_ENTERO(40),           // int
    TIPO_FLOTANTE(41),         // float, double
    TIPO_CADENA_TIPO(42),      // string
    TIPO_CARACTER(43),         // char
    TIPO_BOOLEANO(44),         // bool
    TIPO_ARREGLO(45),          // array
    
    // Exception Handling
    EXCEPCION(60),             // try, catch, throw, finally
    
    // Literals
    LITERAL_NUMERICO(5),
    LITERAL_CADENA(6),
    
    // Operators
    OPERADOR_ASIGNACION(7),    // =
    OPERADOR_ARITMETICO(8),    // + - * /
    OPERADOR_COMPARACION(20),  // == != < > <= >=
    OPERADOR_LOGICO(21),       // && || !
    
    // Delimiters
    APERTURA(9),               // (
    CIERRE(9),                 // )
    AGRUPADOR(9),              // [ ]
    PUNTO_Y_COMA(10),          // ;
    COMA(22),                  // ,
    
    // Identifiers
    IDENTIFICADOR(11),
    
    // Valid tokens recognized by FSA
    ENTERO(50),                // Integer literals
    DECIMAL(51),               // Decimal/float literals
    CADENA(52),                // String literals
    VARIABLE(53),              // Variable names
    
    // Special
    ERROR(100),
    EOF(999);
    
    private final int code;
    
    TokenType(int code) {
        this.code = code;
    }
    
    public int getCode() {
        return code;
    }
}
