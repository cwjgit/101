package com.leyou.cart.controller;

import com.leyou.cart.entity.Cart;
import com.leyou.cart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.naming.Name;
import java.util.List;

/**
 * @创建人 cwj
 * @创建时间 2019/9/16  9:41
 * @描述
 */
@RestController
public class CartController {

    @Autowired
    private CartService cartService;

    /**
     * 添加购物车信息
     *
     * @param cart
     * @return
     */
    @PostMapping
    public ResponseEntity<Void> addCart(@RequestBody Cart cart) {
        cartService.addCart(cart);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 查询购物车信息
     *
     * @return
     */
    @GetMapping("/list")
    public ResponseEntity<List<Cart>> findCarts() {
        return ResponseEntity.ok(cartService.findCarts());
    }

    /**
     * 修改商品的数量
     * @param skuId
     * @param num
     * @return
     */
    @PutMapping
    public ResponseEntity<Void> updateNum(@RequestParam("id") Long skuId,
                                          @RequestParam("num") Integer num) {
        cartService.updateNum(skuId, num);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 删除购物车信息
     * @param skuId
     * @return
     */
    @DeleteMapping("{skuId}")
    public ResponseEntity<Void> deleteCart(@PathVariable(name = "skuId") Long skuId){
        cartService.deleteCart(skuId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 用户登录后，把客户端的购物车数据，传到服务端，批量增加/ 修改 购物车数据
     * @param carts
     * @return
     */
    @PostMapping("/list")
    public ResponseEntity<Void> saveCates(@RequestBody List<Cart> carts){
        cartService.saveCates(carts);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
