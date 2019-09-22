package com.leyou.order.service;

import com.leyou.order.DTO.OrderDTO;

/**
 * @创建人 cwj
 * @创建时间 2019/9/16  22:05
 * @描述
 */
public interface OrderService {

    Long saveOrder(OrderDTO orderDTO);

}
