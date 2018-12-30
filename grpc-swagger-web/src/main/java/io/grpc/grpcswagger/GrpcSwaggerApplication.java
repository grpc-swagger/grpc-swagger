package io.grpc.grpcswagger;

import org.apache.log4j.BasicConfigurator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "io.grpc.grpcswagger")
public class GrpcSwaggerApplication {

    public static void main(String[] args) {
        BasicConfigurator.configure();
        SpringApplication.run(GrpcSwaggerApplication.class, args);
    }
}
