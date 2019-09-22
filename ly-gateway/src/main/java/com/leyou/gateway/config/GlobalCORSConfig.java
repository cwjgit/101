package com.leyou.gateway.config;

import org.springframework.boot.actuate.autoconfigure.endpoint.web.CorsEndpointProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * @创建人 cwj
 * @创建时间 2019/8/28  20:55
 * @描述 跨域过滤器 接受请求头中关于跨域的请求并响应相关的头 实现跨域
 */
@Configuration
public class GlobalCORSConfig {

    @Bean
    public CorsFilter corsFilter(){
//        1.添加cors的配置信息
        CorsConfiguration corsConfiguration = new CorsConfiguration();
//          允许访问的域
        corsConfiguration.addAllowedOrigin("http://manage.leyou.com");
        corsConfiguration.addAllowedOrigin("http://www.leyou.com");
//          是否允许发送cookie
        corsConfiguration.setAllowCredentials(true);
//          允许的请求方式
        corsConfiguration.addAllowedMethod(HttpMethod.GET);
        corsConfiguration.addAllowedMethod(HttpMethod.POST);
        corsConfiguration.addAllowedMethod(HttpMethod.PUT);
        corsConfiguration.addAllowedMethod(HttpMethod.DELETE);
        corsConfiguration.addAllowedMethod(HttpMethod.OPTIONS);
        corsConfiguration.addAllowedMethod(HttpMethod.HEAD);
//          允许的头信息
        corsConfiguration.addAllowedHeader("*");
//          访问有效期
        corsConfiguration.setMaxAge(360000L);
//       2.添加映射路径，我们拦截一切请求
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**",corsConfiguration);
//       3.返回新的CORSFilter
        return new CorsFilter(source);
    }
}
