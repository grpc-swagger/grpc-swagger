package io.grpc.grpcswagger.model;

import lombok.Data;

/**
 * @author liuzhengyang
 */
@Data
public class RegisterParam {
    private String host;
    private int port;
    private String groupName;

    public String getHostAndPortText() {
        return host + ":" + port;
    }
}
