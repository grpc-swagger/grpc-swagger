package io.grpc.grpcswagger.controller;

import static io.grpc.CallOptions.DEFAULT;
import static io.grpc.grpcswagger.manager.ServiceConfigManager.getServiceConfigs;
import static io.grpc.grpcswagger.model.Result.error;
import static io.grpc.grpcswagger.utils.GrpcReflectionUtils.parseToMethodDefinition;
import static io.grpc.grpcswagger.utils.ServiceRegisterUtils.getServiceNames;
import static io.grpc.grpcswagger.utils.ServiceRegisterUtils.registerByIpAndPort;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.ParserConfig;
import com.google.common.net.HostAndPort;
import com.google.protobuf.DescriptorProtos.FileDescriptorSet;

import io.grpc.ManagedChannel;
import io.grpc.grpcswagger.config.AppConfig;
import io.grpc.grpcswagger.manager.ServiceConfigManager;
import io.grpc.grpcswagger.model.CallResults;
import io.grpc.grpcswagger.model.GrpcMethodDefinition;
import io.grpc.grpcswagger.model.RegisterParam;
import io.grpc.grpcswagger.model.Result;
import io.grpc.grpcswagger.model.ServiceConfig;
import io.grpc.grpcswagger.openapi.v2.SwaggerV2DocumentView;
import io.grpc.grpcswagger.openapi.v2.SwaggerV2Documentation;
import io.grpc.grpcswagger.service.DocumentService;
import io.grpc.grpcswagger.service.GrpcProxyService;
import io.grpc.grpcswagger.utils.ChannelFactory;
import lombok.SneakyThrows;

/**
 * @author liuzhengyang
 */
@RestController
public class GrpcController {

    private static final Logger logger = LoggerFactory.getLogger(GrpcController.class);

    private static final String ENDPOINT_PARAM = "endpoint";

    @Autowired
    private GrpcProxyService grpcProxyService;

    @Autowired
    private DocumentService documentService;

    @PostConstruct
    public void init() {
        ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
    }

    @RequestMapping("/v2/api-docs")
    public Object groupResponse(@RequestParam("service") String service, HttpServletRequest httpServletRequest) {
        String apiHost = httpServletRequest.getHeader("Host");
        SwaggerV2Documentation documentation = documentService.getDocumentation(service, apiHost);
        return new SwaggerV2DocumentView(service, documentation);
    }

    @SneakyThrows
    @RequestMapping("/{rawFullMethodName}")
    public Result<Object> invokeMethod(@PathVariable String rawFullMethodName,
                                       @RequestBody String payload,
                                       @RequestParam(defaultValue = "{}") String headers) {
        GrpcMethodDefinition methodDefinition = parseToMethodDefinition(rawFullMethodName);
        JSONObject jsonObject = JSON.parseObject(payload);
        HostAndPort endPoint;
        if (jsonObject.containsKey(ENDPOINT_PARAM)) {
            endPoint = HostAndPort.fromString(jsonObject.getString(ENDPOINT_PARAM));
            jsonObject.remove(ENDPOINT_PARAM);
            payload = JSON.toJSONString(jsonObject);
        } else {
            String fullServiceName = methodDefinition.getFullServiceName();
            endPoint = ServiceConfigManager.getEndPoint(fullServiceName);
        }
        if (endPoint == null) {
            return Result.success("can't find target endpoint");
        }
        Map<String, Object> metaHeaderMap = JSON.parseObject(headers);
        ManagedChannel channel = null;
        try {
            channel = ChannelFactory.create(endPoint, metaHeaderMap);
            CallResults results = grpcProxyService.invokeMethod(methodDefinition, channel, DEFAULT, singletonList(payload));
            return Result.success(results.asJSON()).setEndpoint(endPoint.toString());
        } finally {
            if (channel != null) {
                channel.shutdown();
            }
        }
    }

    @RequestMapping("/listServices")
    public Result<Object> listServices() {
        if (!AppConfig.enableListService()) {
            return Result.error("Not support this action.");
        }
       return Result.success(getServiceConfigs());
    }

    @RequestMapping("/register")
    public Result<Object> registerServices(RegisterParam registerParam) {

        List<FileDescriptorSet> fileDescriptorSets = registerByIpAndPort(registerParam.getHost(), registerParam.getPort());
        if (CollectionUtils.isEmpty(fileDescriptorSets)) {
            return error("no services find");
        }
        List<String> serviceNames = getServiceNames(fileDescriptorSets);
        List<ServiceConfig> serviceConfigs = serviceNames.stream()
                .map(name -> new ServiceConfig(name, registerParam.getHostAndPortText()))
                .peek(ServiceConfigManager::addServiceConfig)
                .collect(toList());
        return Result.success(serviceConfigs);
    }
}
