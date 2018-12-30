package io.grpc.grpcswagger.utils;

import org.junit.Test;

/**
 * @author zhangjikai
 * Created on 2018-12-16
 */
public class GrpcReflectionUtilsTest {

    @Test
    public void testParseToMethodDefinition() {
        System.out.println(GrpcReflectionUtils.parseToMethodDefinition("io.grpc.reflection.Test.print"));
    }
}
