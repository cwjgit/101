package com.leyou.gateway.filters;

import com.leyou.common.auth.entity.Payload;
import com.leyou.common.auth.entity.UserInfo;
import com.leyou.common.auth.utils.JwtUtils;
import com.leyou.common.utils.CookieUtils;
import com.leyou.gateway.config.FilterProperties;
import com.leyou.gateway.config.ZuulJwtProperties;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @创建人 cwj
 * @创建时间 2019/9/15  9:49
 * @描述
 */
@Slf4j
@Component
@EnableConfigurationProperties(FilterProperties.class)
public class AuthFilter extends ZuulFilter {

    @Autowired
    private ZuulJwtProperties Prop;
    @Autowired
    private FilterProperties filterProperties;

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return FilterConstants.FORM_BODY_WRAPPER_FILTER_ORDER - 1;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        //获得所有需要跳过检验的地址路径
        List<String> allowPaths = filterProperties.getAllowPaths();
        Boolean flag = true;
        for (String allowPath : allowPaths) {
            String path = request.getRequestURI();
            //判断在路径中是否是以不需要过滤的路径开头的 是就不需要过滤 return false
            if(path.startsWith(allowPath)){
                flag = false;
                break;
            }
        }
        return flag;
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        try{
            String token = CookieUtils.getCookieValue(request, Prop.getUser().getCookieName());
            Payload<UserInfo> payload = JwtUtils.getInfoFromToken(token, Prop.getPublicKey(), UserInfo.class);
            UserInfo userInfo = payload.getUserInfo();
            // 获取用户角色，查询权限
            String role = userInfo.getRole();
            // 获取当前资源路径
            String path = request.getRequestURI();
            String method = request.getMethod();
            //TODO 校验权限
            log.info("【网关】用户{},角色{}。访问服务{} : {}，", userInfo.getUsername(), role, method, path);
        }catch(Exception e){
            // 校验出现异常，返回403
            ctx.setSendZuulResponse(false);
            ctx.setResponseStatusCode(403);
            log.error("非法访问，未登录，地址：{}", request.getRemoteHost(), e );
        }
        return null;
    }
}
