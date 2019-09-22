package com.leyou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @创建人 cwj
 * @创建时间 2019/9/16  8:57
 * @描述
 */
@SpringBootApplication
@EnableDiscoveryClient
public class LyCartApplication {
    public static void main(String[] args) {
        SpringApplication.run(LyCartApplication.class,args);
    }
}
