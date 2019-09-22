package com.leyou.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.leyou.user.DTO.UserDTO;
import com.leyou.user.entiry.TbUser;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author HM
 * @since 2019-08-27
 */
public interface TbUserService extends IService<TbUser> {

    Boolean checkDataType(String data, Integer type);

    void sendMessage(String phone);

    void registerUser(TbUser user, String code);

    UserDTO queryUser(String username, String password);
}
