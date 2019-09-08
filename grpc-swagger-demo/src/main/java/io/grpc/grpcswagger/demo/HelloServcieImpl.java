package io.grpc.grpcswagger.demo;

import io.grpc.stub.StreamObserver;

/**
 * @author liuzhengyang
 */
public class HelloServcieImpl extends HelloServiceGrpc.HelloServiceImplBase {
    @Override
    public void helloWorld(HelloProto.HelloRequest request, StreamObserver<HelloProto.HelloResponse> responseObserver) {
        responseObserver.onNext(HelloProto.HelloResponse.newBuilder().setResult("Hello " + request.getName()).build());
        responseObserver.onCompleted();
    }
}
