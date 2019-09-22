package com.leyou.auth.service;

import com.leyou.common.auth.entity.UserInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @创建人 cwj
 * @创建时间 2019/9/14  19:57
 * @描述
 */
public interface AuthService {
    void login(String username, String password, HttpServletResponse response);

    UserInfo verify(HttpServletRequest request, HttpServletResponse response);

    void logout(HttpServletRequest request, HttpServletResponse response);
}
