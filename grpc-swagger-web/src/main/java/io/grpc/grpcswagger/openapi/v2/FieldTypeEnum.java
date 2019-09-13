package io.grpc.grpcswagger.openapi.v2;

import java.util.HashMap;
import java.util.Map;

import com.google.protobuf.Descriptors;
import com.google.protobuf.Descriptors.FieldDescriptor.Type;

/**
 * @author liuzhengyang
 */
enum FieldTypeEnum {
    UNKNOWN("unknown", "unknown"),
    INT32("integer", "int32"),
    INT64("integer", "int64"),
    FLOAT("number", "float"),
    DOUBLE("number", "double"),
    STRING("string"),
    BOOLEAN("boolean"),
    OBJECT("object"),
    ARRAY("array"),
    BYTE("byte"), // FIXME 这个验证下
    ENUM("enum");

    private final String type;
    private final String format;

    private static final Map<Type, FieldTypeEnum> typeMap =
            new HashMap<>();

    static {
        typeMap.put(Type.ENUM, ENUM);
        typeMap.put(Type.INT64, INT64);
        typeMap.put(Type.UINT64, INT64);
        typeMap.put(Type.FIXED64, INT64);
        typeMap.put(Type.SINT64, INT64);
        typeMap.put(Type.INT32, INT32);
        typeMap.put(Type.UINT32, INT32);
        typeMap.put(Type.FIXED32, INT32);
        typeMap.put(Type.SINT32, INT32);
        typeMap.put(Type.FLOAT, FLOAT);
        typeMap.put(Type.DOUBLE, DOUBLE);
        typeMap.put(Type.BOOL, BOOLEAN);
        typeMap.put(Type.STRING, STRING);
        typeMap.put(Type.MESSAGE, OBJECT);
        typeMap.put(Type.BYTES, BYTE);
    }

    FieldTypeEnum(String type) {
        this(type, null);
    }

    FieldTypeEnum(String type, String format) {
        this.type = type;
        this.format = format;
    }

    public String getType() {
        return type;
    }

    public String getFormat() {
        return format;
    }

    public static FieldTypeEnum getByFieldType(Type type) {
        return typeMap.getOrDefault(type, UNKNOWN);
    }
}
