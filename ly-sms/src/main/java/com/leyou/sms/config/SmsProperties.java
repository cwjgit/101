package com.leyou.sms.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @创建人 cwj
 * @创建时间 2019/9/10  19:47
 * @描述 用来和yml文件来匹配
 */
@Data
@ConfigurationProperties("ly.sms")
public class SmsProperties {
    /**
     * 账号
     */
    private String accessKeyID;
    /**
     * 密钥
     */
    private String accessKeySecret;
    /**
     * 短信签名
     */
    private String signName;
    /**
     * 短信模板
     */
    private String verifyCodeTemplate;
    /**
     * 发送短信请求的域名
     */
    private String domain;
    /**
     * API版本
     */
    private String version;
    /**
     * API类型
     */
    private String action;
    /**
     * 区域
     */
    private String regionID;

}
