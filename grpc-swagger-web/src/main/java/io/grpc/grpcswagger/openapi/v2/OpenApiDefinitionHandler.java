package io.grpc.grpcswagger.openapi.v2;

import static io.grpc.grpcswagger.openapi.v2.OpenApiParser.DEFINITION_REF_PREFIX;
import static io.grpc.grpcswagger.openapi.v2.OpenApiParser.HTTP_OK;
import static java.util.Optional.ofNullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.DescriptorProtos.MessageOptions;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Descriptors.Descriptor;

import io.grpc.grpcswagger.grpc.ServiceResolver;

/**
 * @author Jikai Zhang
 * @date 2019-08-24
 */
public class OpenApiDefinitionHandler {
    
    private final Map<String, DefinitionType> typeLookupTable;
    
    private static final Logger logger = LoggerFactory.getLogger(OpenApiDefinitionHandler.class);
    
    public OpenApiDefinitionHandler(Map<String, DefinitionType> typeLookupTable) {
        this.typeLookupTable = typeLookupTable;
    }
    
    public void parseModelTypes(List<Descriptors.FileDescriptor> fileDescriptors) {
        fileDescriptors.forEach(fileDescriptor -> {
            List<Descriptor> messageTypes = fileDescriptor.getMessageTypes();
            messageTypes.forEach(messageType -> {
                typeLookupTable.put(messageType.getFullName(), buildDefinitionType(messageType));
                parseNestModelType(messageType.getNestedTypes());
            });
        });
    }
    
    private void parseNestModelType(List<Descriptor> descriptors) {
        if (CollectionUtils.isEmpty(descriptors)) {
            return;
        }
        descriptors.forEach(d -> {
            boolean isMap = ofNullable(d.getOptions()).map(MessageOptions::getMapEntry).orElse(false);
            if (isMap) {
                return;
            }
            typeLookupTable.put(d.getFullName(), buildDefinitionType(d));
        });
    }
    
    private DefinitionType buildDefinitionType(Descriptor descriptor) {
        DefinitionType definitionType = new DefinitionType();
        definitionType.setTitle(descriptor.getName());
        definitionType.setType(FieldTypeEnum.OBJECT.getType());
        definitionType.setProperties(new HashMap<>());
        definitionType.setProtocolDescriptor(descriptor);
        return definitionType;
    }
    
    /**
     * 解析字段属性
     * 字段分为 primitive integer(int32, int64), float, double, string,
     * object类型 array类型
     * object类型指向 lookupTable里的字段, 并使用$ref表示引用
     * array类型，type是array,具体item是字段类型
     */
    public void processMessageFields() {
        typeLookupTable.forEach((typeName, definitionType) -> {
            Descriptor protocolDescriptor = definitionType.getProtocolDescriptor();
            Map<String, FieldProperty> properties = definitionType.getProperties();
            List<Descriptors.FieldDescriptor> fields = protocolDescriptor.getFields();
            fields.forEach(fieldDescriptor -> {
                FieldProperty fieldProperty = parseFieldProperty(fieldDescriptor);
                properties.put(fieldDescriptor.getName(), fieldProperty);
            });
        });
    }
    
    public Map<String, PathItem> parsePaths(ServiceResolver serviceResolver) {
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
    
    private Operation parseOperation(Descriptors.MethodDescriptor methodDescriptor) {
        Operation operation = new Operation();
        
        Descriptor inputType = methodDescriptor.getInputType();
        Descriptor outputType = methodDescriptor.getOutputType();
        
        operation.setDescription(methodDescriptor.getName());
        List<Parameter> parameters = parseParameters(inputType);
        parameters.add(buildHeaderParameter());
        Map<String, ResponseObject> response = parseResponse(outputType);
        operation.setParameters(parameters);
        operation.setResponses(response);
        return operation;
    }
    
    private List<Parameter> parseParameters(Descriptor inputType) {
        List<Parameter> parameters = new ArrayList<>();
        Parameter parameter = new Parameter();
        parameter.setName(inputType.getName());
        ParameterSchema parameterSchema = new ParameterSchema();
        parameterSchema.setRef(findRefByType(inputType));
        parameter.setSchema(parameterSchema);
        parameters.add(parameter);
        return parameters;
    }
    
    private Parameter buildHeaderParameter() {
        QueryParameter parameter = new QueryParameter();
        parameter.setName("headers");
        parameter.setDescription("Headers passed to gRPC server");
        parameter.setType("object");
        parameter.setRequired(false);
        return parameter;
    }
    
    private Map<String, ResponseObject> parseResponse(Descriptor outputType) {
        ResponseObject responseObject = new ResponseObject();
        ParameterSchema responseSchema = new ParameterSchema();
        responseSchema.setRef(findRefByType(outputType));
        responseObject.setSchema(responseSchema);
        Map<String, ResponseObject> response = new HashMap<>();
        response.put(HTTP_OK, responseObject);
        return response;
    }
    
    private FieldProperty parseFieldProperty(Descriptors.FieldDescriptor fieldDescriptor) {
        Descriptors.FieldDescriptor.Type type = fieldDescriptor.getType();
        
        FieldTypeEnum fieldTypeEnum = FieldTypeEnum.getByFieldType(type);
        
        FieldProperty fieldProperty = new FieldProperty();
        if (fieldDescriptor.isRepeated()) {
            // map
            if (type == Descriptors.FieldDescriptor.Type.MESSAGE
                    && fieldDescriptor.getMessageType().getOptions().getMapEntry()) {
                fieldProperty.setType(FieldTypeEnum.OBJECT.getType());
                Descriptor messageType = fieldDescriptor.getMessageType();
                Descriptors.FieldDescriptor mapValueType = messageType.getFields().get(1);
                fieldProperty.setAdditionalProperties(parseFieldProperty(mapValueType));
            } else { // array
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
    private String findRefByType(Descriptor typeDescriptor) {
        String fullName = typeDescriptor.getFullName();
        DefinitionType definitionType = typeLookupTable.get(fullName);
        if (definitionType != null) {
            return DEFINITION_REF_PREFIX + fullName;
        }
        return null;
    }
}
