package com.leyou.user.controller;

import com.leyou.common.exception.LyException;
import com.leyou.user.DTO.UserDTO;
import com.leyou.user.entiry.TbUser;
import com.leyou.user.service.TbUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.stream.Collectors;

/**
 * @创建人 cwj
 * @创建时间 2019/9/10  20:52
 * @描述
 */
@Controller
public class UserController {

    @Autowired
    private TbUserService tbUserService;

    /**
     * 校验数据 所输入的数据在数据库中是否唯一 type =1 data为用户名 =2 data为电话号
     * @param data
     * @param type
     * @return
     */
    @GetMapping("/check/{data}/{type}")
    public ResponseEntity<Boolean> checkDataType(@PathVariable(name = "data") String data, @PathVariable(name = "type") Integer type) {
        return ResponseEntity.ok(tbUserService.checkDataType(data,type));
    }

    /**
     * 发送短信验证码
     * @return
     */
    @PostMapping("/code")
    public ResponseEntity<Void> sendMessage(@RequestParam(name = "phone") String phone){
        tbUserService.sendMessage(phone);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 注册用户信息
     * @param user 账户 密码 电话号
     * @param code 验证码 用来判断验证码是否正确
     * @return
     */
    @PostMapping("/register")
    public ResponseEntity<Void> registerUser(@Valid TbUser user, BindingResult bindingResult, @RequestParam(name = "code") String code){
        //判断返回是否包含error
        if(bindingResult.hasErrors()){
            String msg = bindingResult.getFieldErrors().stream().map(FieldError::getDefaultMessage).collect(Collectors.joining("|"));
            throw new LyException(400, msg);
        }
        tbUserService.registerUser(user,code);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 查询功能，根据参数中的用户名和密码查询指定用户并且返回用户
     * @param username
     * @param password
     * @return
     */
    @GetMapping("/query")
    public ResponseEntity<UserDTO> queryUser(@RequestParam(name = "username") String username,
                                             @RequestParam(name = "password") String password){
        return ResponseEntity.ok(tbUserService.queryUser(username,password));
    }
}
