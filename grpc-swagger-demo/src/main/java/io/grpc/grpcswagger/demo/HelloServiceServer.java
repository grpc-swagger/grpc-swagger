package io.grpc.grpcswagger.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.protobuf.services.ProtoReflectionService;

/**
 * @author liuzhengyang
 */
public class HelloServiceServer {
    private static final Logger logger = LoggerFactory.getLogger(HelloServiceServer.class);

    private static final int DEMO_SERVER_PORT = 1234;

    public static void main(String[] args) throws Exception {
        logger.info("Starting server on port " + DEMO_SERVER_PORT);
        Server server = ServerBuilder.forPort(DEMO_SERVER_PORT)
                .addService(ProtoReflectionService.newInstance())
                .addService(new HelloServiceImpl())
                .build()
                .start();
        server.awaitTermination();
    }
}
