package com.leyou.auth.config;

import com.leyou.common.auth.utils.RsaUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Hashtable;

/**
 * @创建人 cwj
 * @创建时间 2019/9/14  20:03
 * @描述
 */
@Slf4j
@Data
@Configuration
@ConfigurationProperties(prefix = "ly.jwt")
public class JwtProperties implements InitializingBean {
    //E:/ssh/id_rsa.pub # 公钥地址
    private String pubKeyPath;
    // E:/ssh/id_rsa # 私钥地址
    private String priKeyPath;
    private UserProperties user = new UserProperties();

    private PublicKey publicKey;
    private PrivateKey privateKey;

    @Override
    public void afterPropertiesSet() throws Exception {
        try{
            publicKey = RsaUtils.getPublicKey(pubKeyPath);
            privateKey = RsaUtils.getPrivateKey(priKeyPath);
        }catch(Exception e){
            log.error("初始化公钥和私钥失败！", e);
            new RuntimeException(e);
        }
    }

    @Data
    public class UserProperties{
        // 30 # 过期时间,单位分钟
        private Integer expire;
        // LY_TOKEN # cookie名称
        private String cookieName;
        // leyou.com # cookie的域
        private String cookieDomain;
        //设定新token的创建间隔时间
        private Integer minRefreshInterval;
    }
    /**
     * 此注释可以在对象初始化后再执行
     */
//    @PostConstruct
//    public void getKey(){
//        publicKey = RsaUtils.getPublicKey(pubKeyPath);
//        privateKey = RsaUtils.getPrivateKey(priKeyPath);
//    }
}
