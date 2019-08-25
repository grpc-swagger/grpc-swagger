package io.grpc.grpcswagger.openapi.v2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.protobuf.Descriptors;

import io.grpc.grpcswagger.grpc.ServiceResolver;

/**
 * @author liuzhengyang
 */
public class OpenApiParser {
    
    public static final String DEFINITION_REF_PREFIX = "#/definitions/";
    
    public static final String HTTP_OK = "200";
    
    /**
     * key : field full name
     * value: definition type
     */
    public static SwaggerV2Documentation parseDefinition(ServiceResolver serviceResolver) {
        List<Descriptors.FileDescriptor> fileDescriptors = serviceResolver.getFileDescriptors();
    
        Map<String, DefinitionType> typeLookupTable = new HashMap<>();
        OpenApiDefinitionHandler definitionHandler = new OpenApiDefinitionHandler(typeLookupTable);
        definitionHandler.parseModelTypes(fileDescriptors);
        definitionHandler.processMessageFields();
        Map<String, PathItem> pathItemMap = definitionHandler.parsePaths(serviceResolver);
        SwaggerV2Documentation swaggerV2Documentation = new SwaggerV2Documentation();
        swaggerV2Documentation.setInfo(new InfoObject.InfoObjectBuilder()
                .title("grpc-swagger").build());

        swaggerV2Documentation.setDefinitions(typeLookupTable);
        swaggerV2Documentation.setPaths(pathItemMap);
        
        registerService(serviceResolver, swaggerV2Documentation);
        return swaggerV2Documentation;
    }
    
    
    private static void registerService(ServiceResolver serviceResolver, SwaggerV2Documentation swaggerV2Documentation) {
        serviceResolver.listServices().forEach(serviceDescriptor -> {
            String serviceName = serviceDescriptor.getFullName();
            DocumentRegistry.getInstance().put(serviceName, swaggerV2Documentation);
        });
    }
}
