package io.grpc.grpcswagger.model;

import java.util.List;
import java.util.Set;

import lombok.Data;

/**
 * @author zhangjikai
 * Created on 2018-12-07
 */
@Data
public class ServiceConfig {
    private String groupName;
    private List<String> services;
    private Set<String> endpoints;
    private boolean success;
}
