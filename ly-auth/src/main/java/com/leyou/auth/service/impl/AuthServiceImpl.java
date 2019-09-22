package com.leyou.auth.service.impl;

import com.leyou.auth.config.JwtProperties;
import com.leyou.auth.service.AuthService;
import com.leyou.common.auth.entity.Payload;
import com.leyou.common.auth.entity.UserInfo;
import com.leyou.common.auth.utils.JwtUtils;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.CookieUtils;
import com.leyou.user.DTO.UserDTO;
import com.leyou.user.client.UserClient;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @创建人 cwj
 * @创建时间 2019/9/14  19:58
 * @描述
 */
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserClient userClient;
    @Autowired
    private JwtProperties prop;
    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 用户从前端登录页面，输入用户名、密码，进行登录
     *
     * @param username
     * @param password
     * @param response
     * @return
     */
    @Override
    public void login(String username, String password, HttpServletResponse response) {
        try {
            //查询数据库
            UserDTO userDTO = userClient.queryUser(username, password);
            //组装token
            UserInfo userInfo = new UserInfo(userDTO.getId(), userDTO.getUsername(), "admin");
            String token = JwtUtils.generateTokenExpireInMinutes(userInfo, prop.getPrivateKey(), prop.getUser().getExpire());
            CookieUtils.newCookieBuilder()
                    .name(prop.getUser().getCookieName()) //cookie中token名字
                    .value(token) //cookie中的值
                    .domain(prop.getUser().getCookieDomain())
                    .httpOnly(true) //不允许js操作，只能由http来携带
                    .response(response)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            throw new LyException(ExceptionEnum.INVALID_USERNAME_PASSWORD);
        }
    }

    /**
     * 用户从前端登录成功后，前端携带cookie包含token，到服务端验证token有效性，并返回用户信息
     *
     * @param request
     * @param response
     * @return
     */
    @Override
    public UserInfo verify(HttpServletRequest request, HttpServletResponse response) {
        try {
            //获取token
            String token = CookieUtils.getCookieValue(request, prop.getUser().getCookieName());
            //解密
            Payload<UserInfo> payload = JwtUtils.getInfoFromToken(token, prop.getPublicKey(), UserInfo.class);
            //查询黑名单 此token是否可用
            Boolean aBoolean = redisTemplate.hasKey(payload.getId());
            if (aBoolean != null && aBoolean) {
                throw new LyException(ExceptionEnum.UNAUTHORIZED);
            }
            //获取userInfor
            UserInfo userInfo = payload.getUserInfo();
            //获取过期时间
            Date expiration = payload.getExpiration();
            //获取最早过期时间
            DateTime dateTime = new DateTime(expiration).minusMillis(prop.getUser().getMinRefreshInterval());
            if (dateTime.isBefore(System.currentTimeMillis())) {
                token = JwtUtils.generateTokenExpireInMinutes(userInfo, prop.getPrivateKey(), prop.getUser().getExpire());
                CookieUtils.newCookieBuilder()
                        .name(prop.getUser().getCookieName())
                        .value(token)
                        .domain(prop.getUser().getCookieDomain())
                        .response(response)
                        .httpOnly(true)
                        .build();
            }
            return userInfo;
        } catch (Exception e) {
            throw new LyException(ExceptionEnum.INVALID_USERNAME_PASSWORD);
        }
    }

    /**
     * 用户退出操作
     *
     * @param request
     * @return
     */
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        try {
            //获取token
            String token = CookieUtils.getCookieValue(request, prop.getUser().getCookieName());
            //验证
            Payload<UserInfo> payload = JwtUtils.getInfoFromToken(token, prop.getPublicKey(), UserInfo.class);
            String id = payload.getId();
            //设置过期时间 （jwt的过期时间转换为毫秒） - （当前时间）
            Date expiration = payload.getExpiration();
            long time = expiration.getTime() - System.currentTimeMillis();
            //在redis中创建黑名单 使没有过期的cookie无法访问服务
            if (time > 5000) {
                redisTemplate.opsForValue().set(id, "", time, TimeUnit.MILLISECONDS);
            }
            //删除cookie信息
                    CookieUtils.deleteCookie(prop.getUser().getCookieName(), prop.getUser().getCookieDomain(), response);
        } catch (Exception e) {
            throw new LyException(ExceptionEnum.UNAUTHORIZED);
        }
    }
}
