package io.grpc.grpcswagger.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhangjikai
 * Created on 2018-12-07
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceConfig {
    private String service;
    private String endPoint;
}
