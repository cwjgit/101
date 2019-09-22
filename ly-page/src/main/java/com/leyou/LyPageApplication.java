package com.leyou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @创建人 cwj
 * @创建时间 2019/9/7  9:37
 * @描述
 */
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.leyou")
@SpringBootApplication
public class LyPageApplication {
    public static void main(String[] args) {
        SpringApplication.run(LyPageApplication.class,args);
    }
}
