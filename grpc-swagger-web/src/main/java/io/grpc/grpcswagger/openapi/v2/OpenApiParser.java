package io.grpc.grpcswagger.openapi.v2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nullable;

import com.google.protobuf.Descriptors;

import io.grpc.grpcswagger.grpc.ServiceResolver;

/**
 * @author liuzhengyang
 */
public class OpenApiParser {

    private static final String DEFINITION_REF_PREFIX = "#/definitions/";

    private static final String HTTP_OK = "200";

    /**
     * key : field full name
     * value: definition type
     */
    private static final Map<String, DefinitionType> typeLookupTable = new ConcurrentHashMap<>();

    public static SwaggerV2Documentation parseDefinition(ServiceResolver serviceResolver) {
        List<Descriptors.FileDescriptor> fileDescriptors = serviceResolver.getFileDescriptors();

        parseModelTypes(fileDescriptors);

        processMessageFields();

        Map<String, PathItem> pathItemMap = parsePaths(serviceResolver);
        SwaggerV2Documentation swaggerV2Documentation = new SwaggerV2Documentation();
        swaggerV2Documentation.setInfoObject(new InfoObject.InfoObjectBuilder()
                .title("grpc-swagger").build());
        swaggerV2Documentation.setDefinitions(typeLookupTable);
        swaggerV2Documentation.setPaths(pathItemMap);

        registerService(serviceResolver, swaggerV2Documentation);
        return swaggerV2Documentation;
    }

    private static void parseModelTypes(List<Descriptors.FileDescriptor> fileDescriptors) {
        fileDescriptors.forEach(fileDescriptor -> {
            List<Descriptors.Descriptor> messageTypes = fileDescriptor.getMessageTypes();
            messageTypes.forEach(messageType -> {
                String fullName = messageType.getFullName();
                if (!typeLookupTable.containsKey(fullName)) {
                    DefinitionType definitionType = new DefinitionType();
                    definitionType.setTitle(messageType.getName());
                    definitionType.setType(FieldTypeEnum.OBJECT.getType());
                    definitionType.setProperties(new HashMap<>());
                    definitionType.setProtocolDescriptor(messageType);
                    typeLookupTable.put(fullName, definitionType);
                }
            });
        });
    }

    /**
     * 解析字段属性
     * 字段分为 primitive integer(int32, int64), float, double, string,
     * object类型 array类型
     * object类型指向 lookupTable里的字段, 并使用$ref表示引用
     * array类型，type是array,具体item是字段类型
     */
    public static void processMessageFields() {
        typeLookupTable.forEach((typeName, definitionType) -> {
            Descriptors.Descriptor protocolDescriptor = definitionType.getProtocolDescriptor();
            Map<String, FieldProperty> properties = definitionType.getProperties();
            List<Descriptors.FieldDescriptor> fields = protocolDescriptor.getFields();
            fields.forEach(fieldDescriptor -> {
                FieldProperty fieldProperty = parseFieldProperty(fieldDescriptor);
                properties.put(fieldDescriptor.getName(), fieldProperty);
            });
        });
    }

    private static Map<String, PathItem> parsePaths(ServiceResolver serviceResolver) {
        Map<String, PathItem> pathItemMap = new HashMap<>();

        serviceResolver.listServices().forEach(serviceDescriptor -> {
            List<Descriptors.MethodDescriptor> methods = serviceDescriptor.getMethods();
            methods.forEach(methodDescriptor -> {
                PathItem pathItem = new PathItem();
                Operation operation = parseOperation(methodDescriptor);
                pathItem.setPost(operation);
                pathItemMap.put('/' + methodDescriptor.getFullName(), pathItem);
            });
        });

        return pathItemMap;
    }

    private static Operation parseOperation(Descriptors.MethodDescriptor methodDescriptor) {
        Operation operation = new Operation();

        Descriptors.Descriptor inputType = methodDescriptor.getInputType();
        Descriptors.Descriptor outputType = methodDescriptor.getOutputType();

        operation.setDescription(methodDescriptor.getName());
        List<Parameter> parameters = parseParameters(inputType);

        Map<String, ResponseObject> response = parseResponse(outputType);

        operation.setParameters(parameters);
        operation.setResponses(response);
        return operation;
    }

    private static List<Parameter> parseParameters(Descriptors.Descriptor inputType) {
        List<Parameter> parameters = new ArrayList<>();
        Parameter parameter = new Parameter();
        parameter.setName(inputType.getName());
        ParameterSchema parameterSchema = new ParameterSchema();
        parameterSchema.setRef(findRefByType(inputType));
        parameter.setSchema(parameterSchema);
        parameters.add(parameter);
        return parameters;
    }

    private static Map<String, ResponseObject> parseResponse(Descriptors.Descriptor outputType) {
        ResponseObject responseObject = new ResponseObject();
        ParameterSchema responseSchema = new ParameterSchema();
        responseSchema.setRef(findRefByType(outputType));
        responseObject.setSchema(responseSchema);

        Map<String, ResponseObject> response = new HashMap<>();
        response.put(HTTP_OK, responseObject);
        return response;
    }

    private static void registerService(ServiceResolver serviceResolver, SwaggerV2Documentation swaggerV2Documentation) {
        serviceResolver.listServices().forEach(serviceDescriptor -> {
            String serviceName = serviceDescriptor.getFullName();
            DocumentRegistry.getInstance().put(serviceName, swaggerV2Documentation);
        });
    }

    private static FieldProperty parseFieldProperty(Descriptors.FieldDescriptor fieldDescriptor) {
        Descriptors.FieldDescriptor.Type type = fieldDescriptor.getType();

        FieldTypeEnum fieldTypeEnum = FieldTypeEnum.getByFieldType(type);

        FieldProperty fieldProperty = new FieldProperty();
        if (fieldDescriptor.isRepeated()) {
            // map
            if (type == Descriptors.FieldDescriptor.Type.MESSAGE
                    && fieldDescriptor.getMessageType().getOptions().getMapEntry()) {
                fieldProperty.setType(FieldTypeEnum.OBJECT.getType());
                Descriptors.Descriptor messageType = fieldDescriptor.getMessageType();
                Descriptors.FieldDescriptor mapValueType = messageType.getFields().get(1);
                fieldProperty.setAdditionalProperties(parseFieldProperty(mapValueType));
            }
            else { // array
                fieldProperty.setType(FieldTypeEnum.ARRAY.getType());
                Items items = new Items();
                items.setType(fieldTypeEnum.getType());
                items.setFormat(fieldTypeEnum.getFormat());
                if (fieldTypeEnum == FieldTypeEnum.OBJECT) {
                    items.setRef(findRefByType(fieldDescriptor.getMessageType()));
                }
                fieldProperty.setItems(items);
            }
        }
        // object reference
        else if (fieldTypeEnum == FieldTypeEnum.OBJECT) {
            fieldProperty.setRef(findRefByType(fieldDescriptor.getMessageType()));
        }
        // enum
        else if (fieldTypeEnum == FieldTypeEnum.ENUM) {
            fieldProperty.setType(FieldTypeEnum.ENUM.getType());
            List<String> enums = new ArrayList<>();
            Descriptors.EnumDescriptor enumDescriptor = fieldDescriptor.getEnumType();
            enumDescriptor.getValues().forEach(enumValueDescriptor -> enums.add(enumValueDescriptor.getName()));
            fieldProperty.setEnums(enums);
        }
        // other simple types
        else {
            fieldProperty.setType(fieldTypeEnum.getType());
            fieldProperty.setFormat(fieldTypeEnum.getFormat());
        }
        return fieldProperty;
    }

    @Nullable
    private static String findRefByType(Descriptors.Descriptor typeDescriptor) {
        String fullName = typeDescriptor.getFullName();
        DefinitionType definitionType = typeLookupTable.get(fullName);
        if (definitionType != null) {
            return DEFINITION_REF_PREFIX + fullName;
        }
        return null;
    }
}
