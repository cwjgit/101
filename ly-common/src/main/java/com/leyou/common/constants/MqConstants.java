package com.leyou.common.constants;

/**
 * @创建人 cwj
 * @创建时间 2019/9/9  19:31
 * @描述 消息队列中定义的交换机 队列 和key
 */
public abstract class MqConstants {

    public static final class Exchange {
        /**
         * 商品服务交换机名称
         */
        public static final String ITEM_EXCHANGE_NAME = "ly.item.exchange";
        /**
         * 短信服务
         */
        public static final String SMS_EXCHANGE_NAME = "ly.sms.exchange";
    }

    public static final class RoutingKey {
        /**
         * 商品上架的routing-key
         */
        public static final String ITEM_UP_KEY = "item.up";
        /**
         * 商品下架的routing-key
         */
        public static final String ITEM_DOWN_KEY = "item.down";
        /**
         * 发送短信验证码
         */
        public static final String VERIFY_CODE_KEY = "sms.verify.code";
    }

    public static final class Queue{
        /**
         * 搜索服务，商品上架的队列
         */
        public static final String SEARCH_ITEM_UP = "search.item.up.queue";
        /**
         * 搜索服务，商品下架的队列
         */
        public static final String SEARCH_ITEM_DOWN = "search.item.down.queue";

        /**
         * 搜索服务，商品上架的队列
         */
        public static final String PAGE_ITEM_UP = "page.item.up.queue";
        /**
         * 搜索服务，商品下架的队列
         */
        public static final String PAGE_ITEM_DOWN = "page.item.down.queue";
        /**
         * 发送短信验证码
         */
        public static final String SMS_VERIFY_CODE_QUEUE = "sms.verify.code.queue";
    }
}
