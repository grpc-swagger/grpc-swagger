package io.grpc.grpcswagger.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.grpc.stub.StreamObserver;

/**
 * @author liuzhengyang
 */
public class HelloServiceImpl extends HelloServiceGrpc.HelloServiceImplBase {

    private static final Logger logger = LoggerFactory.getLogger(HelloServiceImpl.class);

    @Override
    public void helloWorld(HelloProto.HelloRequest request, StreamObserver<HelloProto.HelloResponse> responseObserver) {
        logger.info("Request {}", request);
        responseObserver.onNext(HelloProto.HelloResponse.newBuilder().setResult("Hello " + request.getName()).build());
        responseObserver.onCompleted();
    }
}
