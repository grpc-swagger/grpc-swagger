package io.grpc.grpcswagger.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.grpc.grpcswagger.openapi.v2.DocumentRegistry;
import io.grpc.grpcswagger.openapi.v2.SwaggerV2Documentation;

/**
 * swagger doc api
 * @author liuzhengyang
 */
@Service
public class DocumentService {

    public SwaggerV2Documentation getDocumentation(String service, String apiHost) {
        SwaggerV2Documentation swaggerV2Documentation = DocumentRegistry.getInstance().get(service);
        if (swaggerV2Documentation != null) {
            swaggerV2Documentation.setHost(apiHost);
        }
        return swaggerV2Documentation;
    }
}
