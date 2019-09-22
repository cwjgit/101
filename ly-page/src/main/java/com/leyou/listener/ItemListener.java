package com.leyou.listener;

import com.leyou.common.constants.MqConstants;
import com.leyou.service.PageService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @创建人 cwj
 * @创建时间 2019/9/9  20:14
 * @描述
 */
@Component
public class ItemListener {

    @Autowired
    private PageService pageService;

    /**
     * 商品上架
     * 创建页面
     * @param spuId
     */
    @RabbitListener(bindings = @QueueBinding(
        value = @Queue(value = MqConstants.Queue.PAGE_ITEM_UP,durable = "true"),
        exchange = @Exchange(value = MqConstants.Exchange.ITEM_EXCHANGE_NAME,type = ExchangeTypes.TOPIC),
        key = {MqConstants.RoutingKey.ITEM_UP_KEY}
    ))
    public void amqpSendCreatePage(Long spuId){
        pageService.createItemHtml(spuId);
    }

    /**
     * 商品下架
     * 删除页面
     * @param spuId
     */
    @RabbitListener(bindings = @QueueBinding(
        value = @Queue(value = MqConstants.Queue.PAGE_ITEM_DOWN,durable = "true"),
        exchange = @Exchange(value = MqConstants.Exchange.ITEM_EXCHANGE_NAME,type = ExchangeTypes.TOPIC),
        key = {MqConstants.RoutingKey.ITEM_DOWN_KEY}
    ))
    public void amqpSendDeletePage(Long spuId){
        pageService.deletePage(spuId);
    }
}
