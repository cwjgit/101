package com.leyou.gateway.config;

import com.leyou.common.auth.utils.RsaUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.security.PublicKey;

/**
 * @创建人 cwj
 * @创建时间 2019/9/15  9:42
 * @描述
 */
@Slf4j
@Data
@Configuration
@ConfigurationProperties(prefix = "ly.jwt")
public class ZuulJwtProperties implements InitializingBean {
    // E:/ssh/id_rsa.pub # 公钥地址
    private String pubKeyPath;

    /**
     * 用户token相关属性
     */
    private UserProperties user = new UserProperties();

    private PublicKey publicKey;

    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            // 获取公钥和私钥
            this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
        } catch (Exception e) {
            log.error("初始化公钥失败！", e);
            throw new RuntimeException(e);
        }
    }

    @Data
    public class UserProperties{
        //LY_TOKEN # cookie名称
        private String cookieName;
    }

}
