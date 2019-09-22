package com.leyou.user.DTO;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author HM
 * @since 2019-08-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class UserDTO {

    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 注册手机号
     */
    private String phone;

}
