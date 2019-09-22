package com.leyou.upload.config;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @创建人 cwj
 * @创建时间 2019/8/31  8:54
 * @描述
 */
@Configuration
public class OSSConfig {

    @Bean
    public OSS ossClient(OSSProperties prop){
        return new OSSClientBuilder().build(prop.getEndpoint(),prop.getAccessKeyId(),prop.getAccessKeySecret());
    }
}
