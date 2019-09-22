package com.leyou.cart.service;

import com.leyou.cart.entity.Cart;

import java.util.List;

/**
 * @创建人 cwj
 * @创建时间 2019/9/16  9:45
 * @描述
 */
public interface CartService {

    void addCart(Cart cart);

    List<Cart> findCarts();

    void updateNum(Long skuId, Integer num);

    void deleteCart(Long skuId);

    void saveCates(List<Cart> carts);
}
