package com.leyou.search.listener;

import com.leyou.common.constants.MqConstants;
import com.leyou.item.client.ItemClient;
import com.leyou.pojo.DTO.SpuDTO;
import com.leyou.search.pojo.Goods;
import com.leyou.search.repository.GoodsRepository;
import com.leyou.search.service.SearchService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @创建人 cwj
 * @创建时间 2019/9/9  19:42
 * @描述
 */
@Component
public class ItemListener {

    @Autowired
    private SearchService searchService;

    /**
     * 商品上架
     * 在索引库添加索引
     * @param spuId
     */
    @RabbitListener(bindings = @QueueBinding(
        value = @Queue(value = MqConstants.Queue.SEARCH_ITEM_UP,durable = "true"),
        exchange = @Exchange(value = MqConstants.Exchange.ITEM_EXCHANGE_NAME,type = ExchangeTypes.TOPIC),
        key = {MqConstants.RoutingKey.ITEM_UP_KEY}
    ))
    public void amqpSendInsert(Long spuId){
        searchService.createIndex(spuId);
    }

    /**
     * 商品下架
     * 在索引库中删除索引
     * @param spuId
     */
    @RabbitListener(bindings = @QueueBinding(
        value = @Queue(value = MqConstants.Queue.SEARCH_ITEM_DOWN,durable = "true"),
        exchange = @Exchange(value = MqConstants.Exchange.ITEM_EXCHANGE_NAME,type = ExchangeTypes.TOPIC),
        key = {MqConstants.RoutingKey.ITEM_DOWN_KEY}
    ))
    public void amqpSendDelete(Long spuId){
        searchService.deleteById(spuId);
    }
}
