package com.leyou.gateway.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @创建人 cwj
 * @创建时间 2019/9/15  10:03
 * @描述
 */
@Data
@Slf4j
@Configuration
@ConfigurationProperties(prefix = "ly.filter")
public class FilterProperties {
    private List<String> allowPaths;
}
