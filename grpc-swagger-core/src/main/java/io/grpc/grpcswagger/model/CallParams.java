package io.grpc.grpcswagger.model;

import java.util.List;

import com.google.protobuf.Descriptors.MethodDescriptor;
import com.google.protobuf.DynamicMessage;

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.stub.StreamObserver;
import lombok.Builder;
import lombok.Getter;

/**
 * @author zhangjikai
 */
@Builder
@Getter
public class CallParams {
    private MethodDescriptor methodDescriptor;
    private Channel channel;
    private CallOptions callOptions;
    private List<DynamicMessage> requests;
    private StreamObserver<DynamicMessage> responseObserver;
}
