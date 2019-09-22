package com.leyou.sms.listener;

import com.leyou.common.constants.MqConstants;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.JsonUtils;
import com.leyou.common.utils.RegexUtils;
import com.leyou.sms.config.SmsProperties;
import com.leyou.sms.utils.SmsHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @创建人 cwj
 * @创建时间 2019/9/10  20:01
 * @描述
 */
@Slf4j
@Component
@EnableConfigurationProperties(SmsProperties.class)
public class SmsLIstener {

    @Autowired
    private SmsProperties prop;
    @Autowired
    private SmsHelper smsHelper;

    @RabbitListener(bindings = @QueueBinding(
        value = @Queue(value = MqConstants.Queue.SMS_VERIFY_CODE_QUEUE,durable = "true"),
        exchange = @Exchange(value = MqConstants.Exchange.SMS_EXCHANGE_NAME,type = ExchangeTypes.TOPIC),
        key = {MqConstants.RoutingKey.VERIFY_CODE_KEY}
    ))
    public void listenVerifyCode(Map<String,String> map){
        if (map == null) {
            log.error("请传入参数");
            return;
        }
        //把手机号从map中移除 并保存电话号码
        String phone = map.remove("phone");
        if(!RegexUtils.isPhone(phone)){
            log.error("电话号码有误！！");
            return;
        }
        //map转换为json
        String codeJson = JsonUtils.toString(map);
        try{
            smsHelper.sendMessage(phone,prop.getSignName(),prop.getVerifyCodeTemplate(),codeJson);
        }catch(LyException e){
            log.error("【SMS服务】短信验证码发送失败", e);
        }
    }
}
