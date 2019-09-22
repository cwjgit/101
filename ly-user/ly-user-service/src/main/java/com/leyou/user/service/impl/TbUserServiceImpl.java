package com.leyou.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leyou.common.constants.MqConstants;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.BeanHelper;
import com.leyou.common.utils.RegexUtils;
import com.leyou.user.DTO.UserDTO;
import com.leyou.user.config.PasswordConfig;
import com.leyou.user.entiry.TbUser;
import com.leyou.user.mapper.TbUserMapper;
import com.leyou.user.service.TbUserService;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author HM
 * @since 2019-08-27
 */
@Service
public class TbUserServiceImpl extends ServiceImpl<TbUserMapper, TbUser> implements TbUserService {

    /**
     * 定义存入redis 数据key的前缀定义存入redis 数据key的前缀
     */
    private static final String KEY_PRE = "ly:user:phone:";

    @Autowired
    private AmqpTemplate amqpTemplate;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    /**
     * 校验数据 所输入的数据在数据库中是否唯一 type =1 data为用户名 =2 data为电话号
     * @param data
     * @param type
     * @return
     */
    @Override
    public Boolean checkDataType(String data, Integer type) {
        QueryWrapper<TbUser> queryWrapper = new QueryWrapper<>();
        //判断类型 相应的查询对应的数据
        switch (type){
            case 1:
                queryWrapper.lambda().eq(TbUser::getUsername,data);
                break;
            case 2:
                queryWrapper.lambda().eq(TbUser::getPhone,data);
                break;
            default:
                throw new LyException(ExceptionEnum.INVALID_PARAM_ERROR);
        }
        //获取总条数
        int count = this.count(queryWrapper);
        //总条数等于0 true 可用 不等于0 不可用
        return count == 0;
    }

    /**
     * 发送短信验证码
     * @return
     */
    @Override
    public void sendMessage(String phone) {
        //判断电话号是否符合要求
        if(!RegexUtils.isPhone(phone)){
            throw new LyException(ExceptionEnum.INVALID_PARAM_ERROR);
        }
        //生成随机验证码
        String code = RandomStringUtils.randomNumeric(6);
        //验证码保存在redis中 并且设置过期时间
        redisTemplate.opsForValue().set(KEY_PRE + phone,code,5,TimeUnit.MINUTES);
        Map<String,String > message = new HashMap<>();
        message.put("phone",phone);
        message.put("code",code);
        amqpTemplate.convertAndSend(MqConstants.Exchange.SMS_EXCHANGE_NAME,MqConstants.RoutingKey.VERIFY_CODE_KEY,message);
    }

    /**
     * 注册用户信息
     * @param user 账户 密码 电话号
     * @param code 验证码 用来判断验证码是否正确
     * @return
     */
    @Override
    public void registerUser(TbUser user, String code) {
        //验证随机验证码
        String redisCode = redisTemplate.opsForValue().get(KEY_PRE + user.getPhone());
        if(!redisCode.equals(code) || StringUtils.isEmpty(redisCode)){
            throw new LyException(ExceptionEnum.INVALID_VERIFY_CODE);
        }
        //密码进行加密
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        //保存到数据库
        boolean save = this.save(user);
        if(!save){
            throw new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }
    }

    /**
     * 查询功能，根据参数中的用户名和密码查询指定用户并且返回用户
     * @param username
     * @param password
     * @return
     */
    @Override
    public UserDTO queryUser(String username, String password) {
        if(StringUtils.isEmpty(username)){
            throw new LyException(ExceptionEnum.INVALID_PARAM_ERROR);
        }
        QueryWrapper<TbUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(TbUser::getUsername,username);
        TbUser tbUser = this.getOne(queryWrapper);
        if(tbUser == null){
            throw new LyException(ExceptionEnum.INVALID_USERNAME_PASSWORD);
        }
        //根据查询出来的用户信息来进行密码比对 true 密码正确 false 错误
        boolean b = passwordEncoder.matches(password, tbUser.getPassword());
        if(!b){
            throw new LyException(ExceptionEnum.INVALID_USERNAME_PASSWORD);
        }
        return BeanHelper.copyProperties(tbUser,UserDTO.class);
    }
}
