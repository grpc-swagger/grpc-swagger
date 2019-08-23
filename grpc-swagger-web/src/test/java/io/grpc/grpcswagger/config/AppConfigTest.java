package io.grpc.grpcswagger.config;

import io.grpc.grpcswagger.GrpcSwaggerApplication;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static io.grpc.grpcswagger.config.AppConfig.enableListService;
import static io.grpc.grpcswagger.config.AppConfig.serviceExpiredSeconds;

/**
 * @author Jikai Zhang
 * @date 2019-08-23
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = GrpcSwaggerApplication.class)
public class AppConfigTest {
    
    @Test
    public void testAppConfig() {
        Assert.assertFalse(enableListService());
        Assert.assertEquals(serviceExpiredSeconds(), 60 * 60 * 3);
    }
}
