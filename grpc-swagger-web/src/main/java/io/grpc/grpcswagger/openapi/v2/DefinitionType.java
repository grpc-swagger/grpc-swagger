package io.grpc.grpcswagger.openapi.v2;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.protobuf.Descriptors;

import lombok.Data;

/**
 * @author liuzhengyang
 */
@Data
public class DefinitionType {
    private String type;
    private String title;
    @JsonIgnore
    private Descriptors.Descriptor protocolDescriptor;
    private Map<String, FieldProperty> properties;
}
