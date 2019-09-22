package com.leyou.order.controller;

import com.leyou.order.DTO.OrderDTO;
import com.leyou.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @创建人 cwj
 * @创建时间 2019/9/16  22:04
 * @描述
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 保存订单
     * @param orderDTO
     * @return
     */
    @PostMapping
    public ResponseEntity<Long> saveOrder(OrderDTO orderDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.saveOrder(orderDTO));
    }
}
