package com.leyou.auth.controller;

import com.leyou.auth.service.AuthService;
import com.leyou.common.auth.entity.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @创建人 cwj
 * @创建时间 2019/9/14  19:50
 * @描述
 */
@RestController
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * 用户从前端登录页面，输入用户名、密码，进行登录
     *
     * @param username
     * @param password
     * @return
     */
    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestParam(name = "username") String username,
                                      @RequestParam(name = "password") String password,
                                      HttpServletResponse response) {
        authService.login(username, password, response);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 用户从前端登录成功后，前端携带cookie包含token，到服务端验证token有效性，并返回用户信息
     *
     * @param request
     * @param response
     * @return
     */
    @GetMapping("/verify")
    public ResponseEntity<UserInfo> verify(HttpServletRequest request, HttpServletResponse response) {
        return ResponseEntity.ok(authService.verify(request, response));
    }

    /**
     * 用户退出操作
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        authService.logout(request, response);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
