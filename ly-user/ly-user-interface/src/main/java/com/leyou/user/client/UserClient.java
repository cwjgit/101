package com.leyou.user.client;

import com.leyou.user.DTO.AddressDTO;
import com.leyou.user.DTO.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @创建人 cwj
 * @创建时间 2019/9/13  16:27
 * @描述
 */
@FeignClient(name = "user-service")
public interface UserClient {

    @GetMapping("/query")
    UserDTO queryUser(@RequestParam(name = "username") String username, @RequestParam(name = "password") String password);

    AddressDTO findAddress();

}
