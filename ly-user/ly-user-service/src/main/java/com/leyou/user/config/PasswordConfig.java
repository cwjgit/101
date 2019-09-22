package com.leyou.user.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.security.SecureRandom;

/**
 * @创建人 cwj
 * @创建时间 2019/9/11  9:32
 * @描述
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "ly.encoder.crypt")
public class PasswordConfig {

    //加密次数
    private int strength;
    //指定随机的盐
    private String secret;

    @Bean
    public BCryptPasswordEncoder passewordEncoder(){
        SecureRandom secureRandom = new SecureRandom(secret.getBytes());
        return new BCryptPasswordEncoder(strength,secureRandom);
    }
}
