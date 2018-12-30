package io.grpc.grpcswagger.model;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;

/**
 * @author zhangjikai
 */
public class CallResults {
    private List<String> results;

    public CallResults() {
        this.results = new ArrayList<>();
    }

    public void add(String jsonText) {
        results.add(jsonText);
    }

    public List<String> asList() {
        return results;
    }

    public Object asJSON() {
        if (results.size() == 1) {
            return JSON.parseObject(results.get(0));
        }
        return results.stream().map(JSON::parseObject).collect(toList());
    }
}
