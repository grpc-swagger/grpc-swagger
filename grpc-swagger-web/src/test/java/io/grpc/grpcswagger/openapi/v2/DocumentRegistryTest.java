package io.grpc.grpcswagger.openapi.v2;

import static org.junit.Assert.*;

import org.junit.Test;
import org.mockito.Mockito;

public class DocumentRegistryTest {

    @Test
    public void getInstance() {
        assertEquals(DocumentRegistry.getInstance(), DocumentRegistry.getInstance());
    }

    @Test
    public void get() {
        SwaggerV2Documentation swaggerV2Documentation = Mockito.mock(SwaggerV2Documentation.class);
        DocumentRegistry.getInstance().put("foo", swaggerV2Documentation);
        assertEquals(swaggerV2Documentation, DocumentRegistry.getInstance().get("foo"));
    }
}