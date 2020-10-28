package io.grpc.grpcswagger;

import org.apache.log4j.BasicConfigurator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@SpringBootApplication(scanBasePackages = "io.grpc.grpcswagger")
public class GrpcSwaggerApplication {

    public static void main(String[] args) {
        BasicConfigurator.configure();
        SpringApplication.run(GrpcSwaggerApplication.class, args);
    }


    //支持JavaScript跨域请求<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    /**
     * 跨域过滤器
     * @return
     */
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", buildConfig());
        return new CorsFilter(source);
    }
    /**CORS跨域配置
     * @return
     */
    private CorsConfiguration buildConfig() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin("*"); //允许的域名或IP地址
        corsConfiguration.addAllowedHeader("*"); //允许的请求头
        corsConfiguration.addAllowedMethod("*"); //允许的HTTP请求方法
        corsConfiguration.setAllowCredentials(true); //允许发送跨域凭据，前端Axios存取JSESSIONID必须要
        return corsConfiguration;
    }
    //支持JavaScript跨域请求 >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

}
