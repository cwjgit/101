package com.leyou.cart.service.impl;

import com.leyou.cart.entity.Cart;
import com.leyou.cart.service.CartService;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.threadlocals.UserHolder;
import com.leyou.common.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @创建人 cwj
 * @创建时间 2019/9/16  9:45
 * @描述
 */
@Slf4j
@Service
public class CartServiceImpl implements CartService {

    private static final String KEY_PREFIX = "ly:cart:uid:";

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 添加购物车信息 保存在redis中
     *
     * @param cart
     * @return
     */
    @Override
    public void addCart(Cart cart) {
        //获取当前用户id
        String uId = String.valueOf(UserHolder.getUser());
        //在设计时使用双层map Map<uId,Map<skuId,String>> 在redis中使用hash结构保存 根据uid获取当前用户所有购物车信息
        BoundHashOperations<String, String, String> boundHash = redisTemplate.boundHashOps(KEY_PREFIX + uId);
        // 获取商品id，作为hashKey
        String skuId = cart.getSkuId().toString();
        //先拿出要添加的数量 防止被覆盖
        Integer num = cart.getNum();
        //根据skuId来判断在此购物车信息中是否有当前的此条信息的数z据
        Boolean aSku = boundHash.hasKey(skuId);
        if (aSku != null && aSku) {
            String redisCart = boundHash.get(skuId);
            //覆盖原来的cart 更改数量
            cart = JsonUtils.toBean(redisCart, Cart.class);
            cart.setNum(cart.getNum() + num);
        }
        boundHash.put(skuId, JsonUtils.toString(cart));
    }

    /**
     * 查询购物车信息
     *
     * @return
     */
    @Override
    public List<Cart> findCarts() {
        // 获取登录用户
        String key = KEY_PREFIX + UserHolder.getUser();
        // 判断是否存在购物车
        Boolean boo = this.redisTemplate.hasKey(key);
        if (boo == null || !boo) {
            // 不存在，直接返回
            throw new LyException(ExceptionEnum.CARTS_NOT_FOUND);
        }
        BoundHashOperations<String, String, String> hashOperations = redisTemplate.boundHashOps(key);
        List<String> values = hashOperations.values();
        List<Cart> carts = values.stream().map(redisCart -> JsonUtils.toBean(redisCart, Cart.class)).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(carts)) {
            throw new LyException(ExceptionEnum.CARTS_NOT_FOUND);
        }
        return carts;
    }

    /**
     * 修改商品的数量
     *
     * @param skuId
     * @param num
     * @return
     */
    @Override
    public void updateNum(Long skuId, Integer num) {
        Long userId = UserHolder.getUser();
        String key = KEY_PREFIX + userId;
        BoundHashOperations<String, String, String> hashOperations = redisTemplate.boundHashOps(key);
        String hashKey = String.valueOf(skuId);
        Boolean boo = hashOperations.hasKey(hashKey);
        if (boo == null || !boo) {
            log.error("购物车商品不存在，用户：{}, 商品：{}", userId, skuId);
            throw new LyException(ExceptionEnum.CARTS_NOT_FOUND);
        }
        String jsonCart = hashOperations.get(hashKey);
        Cart cart = JsonUtils.toBean(jsonCart, Cart.class);
        cart.setNum(num);
        hashOperations.put(hashKey, JsonUtils.toString(cart));
    }

    /**
     * 删除购物车信息
     *
     * @param skuId
     * @return
     */
    @Override
    public void deleteCart(Long skuId) {
        try {
            String key = KEY_PREFIX + UserHolder.getUser();
            HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
            hashOperations.delete(key, skuId.toString());
        } catch (Exception e) {
            e.printStackTrace();
            throw new LyException(ExceptionEnum.CARTS_NOT_FOUND);
        }
    }

    /**
     * 用户登录后，把客户端的购物车数据，传到服务端，批量增加/ 修改 购物车数据
     *
     * @param carts
     * @return
     */
    @Override
    public void saveCates(List<Cart> carts) {
        String key = KEY_PREFIX + UserHolder.getUser();
        //判断此用户是否有购物车 没有直接添加
        Boolean akey = redisTemplate.hasKey(key);
        if (!akey) {
            for (Cart cart : carts) {
                redisTemplate.opsForHash().put(key, cart.getSkuId().toString(), JsonUtils.toString(cart));
            }
        }else {
            BoundHashOperations<String, String, String> hashOperations = redisTemplate.boundHashOps(key);
            for (Cart cart : carts) {
                String hashKey = cart.getSkuId().toString();
                Integer num = cart.getNum();
                //判断在这个购物车中是否有次条信息
                Boolean aSku = hashOperations.hasKey(hashKey);
                if (aSku) {
                    String jsonCart = hashOperations.get(hashKey);
                    cart = JsonUtils.toBean(jsonCart, Cart.class);
                    cart.setNum(cart.getNum() + num);
                }
                hashOperations.put(hashKey, JsonUtils.toString(cart));
            }
        }
    }
}
