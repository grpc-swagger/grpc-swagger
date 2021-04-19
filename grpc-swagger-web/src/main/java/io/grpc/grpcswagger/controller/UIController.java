package io.grpc.grpcswagger.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author liuzhengyang
 */
@Controller
public class UIController {

    @Value("${grpc.server.address:}")
    private String grpcServerAddress;

    @Value("${server.port}")
    private int serverPort;

    @RequestMapping("/")
    public String index(Model model) {
        model.addAttribute("endpoint", grpcServerAddress);
        model.addAttribute("serverPort", serverPort);
        return "index";
    }
}
