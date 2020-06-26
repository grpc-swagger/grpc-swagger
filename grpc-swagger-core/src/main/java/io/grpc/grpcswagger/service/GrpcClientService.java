package io.grpc.grpcswagger.service;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static io.grpc.grpcswagger.utils.GrpcReflectionUtils.fetchFullMethodName;
import static io.grpc.grpcswagger.utils.GrpcReflectionUtils.fetchMethodType;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

import java.util.List;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.protobuf.Descriptors.MethodDescriptor;
import com.google.protobuf.DynamicMessage;

import io.grpc.ClientCall;
import io.grpc.MethodDescriptor.MethodType;
import io.grpc.grpcswagger.grpc.CompositeStreamObserver;
import io.grpc.grpcswagger.grpc.DoneObserver;
import io.grpc.grpcswagger.grpc.DynamicMessageMarshaller;
import io.grpc.grpcswagger.model.CallParams;
import io.grpc.stub.StreamObserver;

/**
 * @author zhangjikai
 */
public class GrpcClientService {

    private static final Logger logger = LoggerFactory.getLogger(GrpcClientService.class);

    @Nullable
    public ListenableFuture<Void> call(CallParams callParams) {
        checkParams(callParams);
        MethodType methodType = fetchMethodType(callParams.getMethodDescriptor());
        List<DynamicMessage> requests = callParams.getRequests();
        StreamObserver<DynamicMessage> responseObserver = callParams.getResponseObserver();
        DoneObserver<DynamicMessage> doneObserver = new DoneObserver<>();
        StreamObserver<DynamicMessage> compositeObserver = CompositeStreamObserver.of(responseObserver, doneObserver);
        StreamObserver<DynamicMessage> requestObserver;
        switch (methodType) {
            case UNARY:
                asyncUnaryCall(createCall(callParams), requests.get(0), compositeObserver);
                return doneObserver.getCompletionFuture();
            case SERVER_STREAMING:
                asyncServerStreamingCall(createCall(callParams), requests.get(0), compositeObserver);
                return doneObserver.getCompletionFuture();
            case CLIENT_STREAMING:
                requestObserver = asyncClientStreamingCall(createCall(callParams), compositeObserver);
                requests.forEach(responseObserver::onNext);
                requestObserver.onCompleted();
                return doneObserver.getCompletionFuture();
            case BIDI_STREAMING:
                requestObserver = asyncBidiStreamingCall(createCall(callParams), compositeObserver);
                requests.forEach(responseObserver::onNext);
                requestObserver.onCompleted();
                return doneObserver.getCompletionFuture();
            default:
                logger.info("Unknown methodType:{}", methodType);
                return null;
        }
    }

    private void checkParams(CallParams callParams) {
        checkNotNull(callParams);
        checkNotNull(callParams.getMethodDescriptor());
        checkNotNull(callParams.getChannel());
        checkNotNull(callParams.getCallOptions());
        checkArgument(isNotEmpty(callParams.getRequests()));
        checkNotNull(callParams.getResponseObserver());
    }

    private ClientCall<DynamicMessage, DynamicMessage> createCall(CallParams callParams) {
        return callParams.getChannel().newCall(createGrpcMethodDescriptor(callParams.getMethodDescriptor()),
                callParams.getCallOptions());
    }

    private io.grpc.MethodDescriptor<DynamicMessage, DynamicMessage> createGrpcMethodDescriptor(MethodDescriptor descriptor) {
        return io.grpc.MethodDescriptor.<DynamicMessage, DynamicMessage>newBuilder()
                .setType(fetchMethodType(descriptor))
                .setFullMethodName(fetchFullMethodName(descriptor))
                .setRequestMarshaller(new DynamicMessageMarshaller(descriptor.getInputType()))
                .setResponseMarshaller(new DynamicMessageMarshaller(descriptor.getOutputType()))
                .build();
    }
}
