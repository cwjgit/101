package com.leyou.cart.entity;

import lombok.Data;

/**
 * @创建人 cwj
 * @创建时间 2019/9/16  9:40
 * @描述 购物车实体类
 */
@Data
public class Cart {
    private Long skuId;// 商品id
    private String title;// 标题
    private String image;// 图片
    private Long price;// 加入购物车时的价格
    private Integer num;// 购买数量
    private String ownSpec;// 商品规格参数
}
