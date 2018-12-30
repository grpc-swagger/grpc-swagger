package io.grpc.grpcswagger.openapi.v2;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * @author liuzhengyang
 */
@Data
public class FieldProperty {
    private String type;
    private String format;
    @JsonProperty("$ref")
    private String ref;
    private Items items;
    @JsonProperty("enum")
    private List<String> enums;
    private FieldProperty additionalProperties;
}
