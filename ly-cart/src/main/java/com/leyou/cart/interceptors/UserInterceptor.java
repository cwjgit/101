package com.leyou.cart.interceptors;

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
 * @创建时间 2019/9/16  9:15
 * @描述 web拦截器 用来拦截每一个请求 在请求中获得token解析得到当前用户信息
 */
@Slf4j
public class UserInterceptor implements HandlerInterceptor {

    /**
     * 前置方法 用来获取每一个请求的token 保存在线程容器中
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try{
            String token = CookieUtils.getCookieValue(request, "LY_TOKEN");
            Payload<UserInfo> payload = JwtUtils.getInfoFromToken(token, UserInfo.class);
            UserInfo userInfo = payload.getUserInfo();
            UserHolder.setUser(userInfo.getId());
            return true;
        }catch(Exception e){
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
