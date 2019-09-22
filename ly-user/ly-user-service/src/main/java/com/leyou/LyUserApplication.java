package com.leyou;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @创建人 cwj
 * @创建时间 2019/9/10  20:35
 * @描述
 */
@SpringBootApplication(scanBasePackages = "com.leyou")
@EnableDiscoveryClient
@MapperScan("com.leyou.user.mapper")
public class LyUserApplication {
    public static void main(String[] args) {
        SpringApplication.run(LyUserApplication.class,args);
    }
}
