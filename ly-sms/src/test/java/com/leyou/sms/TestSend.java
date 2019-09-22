package com.leyou.sms;

import com.leyou.common.constants.MqConstants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

/**
 * @创建人 cwj
 * @创建时间 2019/9/10  20:22
 * @描述
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TestSend {

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Test
    public void sendMasg(){
        Map<String,String> map = new HashMap<>();
        map.put("phone","15811268229");
        map.put("code","123321");
        amqpTemplate.convertAndSend(MqConstants.Exchange.SMS_EXCHANGE_NAME,MqConstants.RoutingKey.VERIFY_CODE_KEY,map);
    }
}
