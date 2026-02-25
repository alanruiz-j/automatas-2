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
    
    public boolean isPrimitive() {
        return this == INT || this == FLOAT || this == DOUBLE || 
               this == STRING || this == CHAR || this == BOOL || this == NUMBER;
    }

    public static class TypeInfo {
        private final DataType type;
        private final DataType arrayBaseType;
        private final int arraySize;

        private TypeInfo(DataType type, DataType arrayBaseType, int arraySize) {
            this.type = type;
            this.arrayBaseType = arrayBaseType;
            this.arraySize = arraySize;
        }

        public static TypeInfo createPrimitiveType(DataType primitiveType) {
            return new TypeInfo(primitiveType, null, 0);
        }

        public static TypeInfo createArrayType(DataType baseType, int size) {
            if (baseType == null || !baseType.isPrimitive()) {
                throw new IllegalArgumentException("Array base type must be a primitive type");
            }
            if (size <= 0) {
                throw new IllegalArgumentException("Array size must be positive");
            }
            return new TypeInfo(DataType.ARRAY, baseType, size);
        }

        public DataType getType() {
            return type;
        }

        public DataType getArrayBaseType() {
            return arrayBaseType;
        }

        public int getArraySize() {
            return arraySize;
        }

        public boolean isArray() {
            return type == DataType.ARRAY && arrayBaseType != null && arraySize > 0;
        }

        public boolean isPrimitiveType() {
            return type != null && type.isPrimitive();
        }

        @Override
        public String toString() {
            if (isArray()) {
                return arrayBaseType.getTypeName() + "[" + arraySize + "]";
            }
            return type != null ? type.getTypeName() : "unknown";
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof TypeInfo)) return false;
            TypeInfo other = (TypeInfo) obj;
            if (type != other.type) return false;
            if (arrayBaseType != other.arrayBaseType) return false;
            return arraySize == other.arraySize;
        }

        @Override
        public int hashCode() {
            int result = type != null ? type.hashCode() : 0;
            result = 31 * result + (arrayBaseType != null ? arrayBaseType.hashCode() : 0);
            result = 31 * result + arraySize;
            return result;
        }
    }
}
