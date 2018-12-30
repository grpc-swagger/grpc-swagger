package io.grpc.grpcswagger.service;

import java.util.List;
import java.util.concurrent.ExecutionException;

import com.google.protobuf.DescriptorProtos.FileDescriptorSet;
import com.google.protobuf.Descriptors.MethodDescriptor;
import com.google.protobuf.DynamicMessage;
import com.google.protobuf.util.JsonFormat.TypeRegistry;

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.grpcswagger.model.CallParams;
import io.grpc.grpcswagger.model.CallResults;
import io.grpc.grpcswagger.model.GrpcMethodDefinition;
import io.grpc.grpcswagger.grpc.ServiceResolver;
import io.grpc.grpcswagger.utils.GrpcReflectionUtils;
import io.grpc.grpcswagger.utils.MessageWriter;
import io.grpc.stub.StreamObserver;

/**
 * @author zhangjikai
 * Created on 2018-12-01
 */
public class GrpcProxyService {
    private GrpcClientService grpcClientService = new GrpcClientService();

    public CallResults invokeMethod(GrpcMethodDefinition definition, Channel channel, CallOptions callOptions,
            List<String> requestJsonTexts) {
        FileDescriptorSet fileDescriptorSet = GrpcReflectionUtils.resolveService(channel, definition.getFullServiceName());
        if (fileDescriptorSet == null) {
            return null;
        }
        ServiceResolver serviceResolver = ServiceResolver.fromFileDescriptorSet(fileDescriptorSet);
        MethodDescriptor methodDescriptor = serviceResolver.resolveServiceMethod(definition);
        TypeRegistry registry = TypeRegistry.newBuilder().add(serviceResolver.listMessageTypes()).build();
        List<DynamicMessage> requestMessages = GrpcReflectionUtils.parseToMessages(registry, methodDescriptor.getInputType(),
                requestJsonTexts);
        CallResults results = new CallResults();
        StreamObserver<DynamicMessage> streamObserver = MessageWriter.newInstance(registry, results);
        CallParams callParams = CallParams.builder()
                .methodDescriptor(methodDescriptor)
                .channel(channel)
                .callOptions(callOptions)
                .requests(requestMessages)
                .responseObserver(streamObserver)
                .build();
        try {
            grpcClientService.call(callParams).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Caught exception while waiting for rpc", e);
        }
        return results;
    }
}
