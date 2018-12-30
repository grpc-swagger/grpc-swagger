package io.grpc.grpcswagger.discovery;

import static com.google.common.collect.Sets.newCopyOnWriteArraySet;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.collect.Sets;
import com.google.common.net.HostAndPort;

import io.grpc.grpcswagger.model.ServiceConfig;

/**
 * @author zhangjikai
 */
public class ServiceDiscoveryCenter {
    private static final Map<String, ServiceConfig> servicesConfigMap = new ConcurrentHashMap<>();
    private static final Map<String, Set<String>> serviceEndpointMap = new ConcurrentHashMap<>();

    public static HostAndPort getTargetHostAdnPost(String fullServiceName) {
        Set<String> urls = serviceEndpointMap.get(fullServiceName);
        if (isEmpty(urls)) {
            return null;
        }
        return HostAndPort.fromString(urls.toArray()[0].toString());
    }

    public static Map<String, ServiceConfig> getServicesConfigMap() {
        return servicesConfigMap;
    }

    public static ServiceConfig addServiceConfig(ServiceConfig serviceConfig) {
        if (servicesConfigMap.containsKey(serviceConfig.getConfigName())) {
            ServiceConfig existConfig = servicesConfigMap.get(serviceConfig.getConfigName());
            existConfig.getEndpoints().addAll(serviceConfig.getEndpoints());
            serviceConfig = existConfig;
        } else {
            servicesConfigMap.put(serviceConfig.getConfigName(), serviceConfig);
        }
        addServiceEndpoint(serviceConfig);
        return serviceConfig;
    }

    public static void addServiceEndpoint(ServiceConfig serviceConfig) {
        serviceConfig.getServices().forEach(serviceName -> {
            if (serviceEndpointMap.containsKey(serviceName)) {
                serviceEndpointMap.get(serviceName).addAll(serviceConfig.getEndpoints());
            } else {
                serviceEndpointMap.put(serviceName, newCopyOnWriteArraySet(serviceConfig.getEndpoints()));
            }
        });
    }
}
