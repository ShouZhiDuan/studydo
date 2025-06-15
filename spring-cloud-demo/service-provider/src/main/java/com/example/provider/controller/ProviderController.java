package com.example.provider.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 服务生产者控制器
 */
@RestController
@RequestMapping("/provider")
public class ProviderController {

    @Value("${server.port}")
    private String port;

    @Value("${spring.application.name}")
    private String serviceName;

    /**
     * 获取服务信息
     */
    @GetMapping("/info")
    public String getInfo() {
        return String.format("服务名称: %s, 端口: %s, 时间: %s", 
                serviceName, port, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }

    /**
     * 根据ID获取用户信息
     */
    @GetMapping("/user/{id}")
    public String getUserById(@PathVariable String id) {
        return String.format("用户ID: %s, 来自服务: %s, 端口: %s", id, serviceName, port);
    }

    /**
     * 健康检查接口
     */
    @GetMapping("/health")
    public String health() {
        return "服务运行正常";
    }

}