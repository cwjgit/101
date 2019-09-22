package com.leyou.upload.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;



/**
 * @创建人 cwj
 * @创建时间 2019/8/31  8:48
 * @描述
 */
@Data
@Component
@ConfigurationProperties(prefix = "leyou.oss")
public class OSSProperties {
    private String accessKeyId;
    private String accessKeySecret;
    private String bucket;
    private String host;
    private String endpoint;
    private String dir;
    private long expireTime;
    private long maxFileSize;
}
