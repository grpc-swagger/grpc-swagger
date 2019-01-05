package io.grpc.grpcswagger.controller;

import static com.google.common.collect.Sets.newConcurrentHashSet;
import static io.grpc.CallOptions.DEFAULT;
import static io.grpc.grpcswagger.discovery.ServiceDiscoveryCenter.addServiceConfig;
import static io.grpc.grpcswagger.model.Result.error;
import static io.grpc.grpcswagger.utils.ServiceRegisterUtils.getServiceNames;
import static io.grpc.grpcswagger.utils.ServiceRegisterUtils.registerByIpAndPort;
import static io.grpc.grpcswagger.utils.GrpcReflectionUtils.parseToMethodDefinition;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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

import io.grpc.Channel;
import io.grpc.grpcswagger.discovery.ServiceDiscoveryCenter;
import io.grpc.grpcswagger.model.RegisterParam;
import io.grpc.grpcswagger.model.Result;
import io.grpc.grpcswagger.model.ServiceConfig;
import io.grpc.grpcswagger.service.DocumentService;
import io.grpc.grpcswagger.utils.ChannelFactory;
import io.grpc.grpcswagger.model.CallResults;
import io.grpc.grpcswagger.model.GrpcMethodDefinition;
import io.grpc.grpcswagger.service.GrpcProxyService;

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
        return documentService.getDocumentation(service, apiHost);
    }

    @RequestMapping("/{rawFullMethodName}")
    public Result<Object> invokeMethod(@PathVariable String rawFullMethodName, @RequestBody String payload) {
        GrpcMethodDefinition methodDefinition = parseToMethodDefinition(rawFullMethodName);
        JSONObject jsonObject = JSON.parseObject(payload);
        HostAndPort hostAndPort;
        if (jsonObject.containsKey(ENDPOINT_PARAM)) {
            hostAndPort = HostAndPort.fromString(jsonObject.getString(ENDPOINT_PARAM));
            jsonObject.remove(ENDPOINT_PARAM);
            payload = JSON.toJSONString(jsonObject);
        } else {
            String fullServiceName = methodDefinition.getFullServiceName();
            hostAndPort = ServiceDiscoveryCenter.getTargetHostAdnPost(fullServiceName);
        }
        if (hostAndPort == null) {
            return Result.success("can't find target endpoint");
        }

        Channel channel = ChannelFactory.create(hostAndPort);
        CallResults results = grpcProxyService.invokeMethod(methodDefinition, channel, DEFAULT, singletonList(payload));
        return Result.success(results.asJSON());
    }

    @RequestMapping("/listServices")
    public Result<Object> listServices() {
        Map<String, ServiceConfig> successServicesMap = ServiceDiscoveryCenter.getServicesConfigMap().entrySet().stream()
                .filter(entry -> entry.getValue().isSuccess())
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
        return Result.success(successServicesMap);
    }

    @RequestMapping("/register")
    public Result<Object> registerServices(RegisterParam registerParam) {

        List<FileDescriptorSet> fileDescriptorSets = registerByIpAndPort(registerParam.getHost(), registerParam.getPort());
        if (CollectionUtils.isEmpty(fileDescriptorSets)) {
            return error("no services find");
        }
        if (StringUtils.isBlank(registerParam.getGroupName())) {
            registerParam.setGroupName(registerParam.getHostAndPortText());
        }
        ServiceConfig serviceConfig = new ServiceConfig();
        serviceConfig.setGroupName(registerParam.getGroupName());
        serviceConfig.setEndpoints(newConcurrentHashSet(singleton(registerParam.getHostAndPortText())));
        serviceConfig.setServices(getServiceNames(fileDescriptorSets));
        serviceConfig.setSuccess(true);
        return Result.success(addServiceConfig(serviceConfig));
    }
}
