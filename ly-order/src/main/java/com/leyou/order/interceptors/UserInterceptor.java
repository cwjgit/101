package com.leyou.order.interceptors;

import com.leyou.common.auth.entity.Payload;
import com.leyou.common.auth.entity.UserInfo;
import com.leyou.common.auth.utils.JwtUtils;
import com.leyou.common.threadlocals.UserHolder;
import com.leyou.common.utils.CookieUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @创建人 cwj
 * @创建时间 2019/9/16  21:57
 * @描述
 */
@Slf4j
public class UserInterceptor implements HandlerInterceptor {

    private static final String COOKIE_NAME = "LY_TOKEN";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try{
            String cookieValue = CookieUtils.getCookieValue(request, COOKIE_NAME);
            Payload<UserInfo> payload = JwtUtils.getInfoFromToken(cookieValue, UserInfo.class);
            UserHolder.setUser(payload.getUserInfo().getId());
            return true;
        }catch(Exception e){
            e.printStackTrace();
            // 解析失败，不继续向下
            log.error("【购物车服务】解析用户信息失败！", e);
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserHolder.removeUser();
    }
}
