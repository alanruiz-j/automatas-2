/*
 * DataType - Enum representing supported data types in the language
 * Used for strict type checking in semantic analysis
 */
package javaapplication4;

public enum DataType {
    INT("int"),
    FLOAT("float"),
    DOUBLE("double"),
    STRING("string"),
    CHAR("char"),
    BOOL("bool"),
    NUMBER("number"),
    ARRAY("array"),
    UNKNOWN(null);
    
    private final String typeName;
    
    DataType(String typeName) {
        this.typeName = typeName;
    }
    
    public String getTypeName() {
        return typeName;
    }
    
    public static DataType fromString(String typeName) {
        if (typeName == null) {
            return UNKNOWN;
        }
        
        switch (typeName.toLowerCase()) {
            case "int":
                return INT;
            case "float":
                return FLOAT;
            case "double":
                return DOUBLE;
            case "string":
                return STRING;
            case "char":
                return CHAR;
            case "bool":
                return BOOL;
            case "number":
                return NUMBER;
            case "array":
                return ARRAY;
            default:
                return UNKNOWN;
        }
    }
}
