package com.leyou.order.service.impl;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.threadlocals.UserHolder;
import com.leyou.common.utils.IdWorker;
import com.leyou.item.client.ItemClient;
import com.leyou.order.DTO.CartDTO;
import com.leyou.order.DTO.OrderDTO;
import com.leyou.order.entity.TbOrder;
import com.leyou.order.entity.TbOrderDetail;
import com.leyou.order.enums.OrderStatusEnum;
import com.leyou.order.service.OrderService;
import com.leyou.order.service.TbOrderDetailService;
import com.leyou.order.service.TbOrderService;
import com.leyou.pojo.DTO.SkuDTO;
import com.leyou.user.DTO.AddressDTO;
import com.leyou.user.client.UserClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @创建人 cwj
 * @创建时间 2019/9/16  22:06
 * @描述
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private IdWorker idWorker;
    @Autowired
    private ItemClient itemClient;
    @Autowired
    private UserClient userClient;
    @Autowired
    private TbOrderService tbOrderService;
    @Autowired
    private TbOrderDetailService tbOrderDetailService;

    /**
     * 保存订单
     *
     * @param orderDTO
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveOrder(OrderDTO orderDTO) {
        //1 id
        long orderId = idWorker.nextId();
        //2 总金额
        List<CartDTO> carts = orderDTO.getCarts();
        List<Long> skuIds = carts.stream().map(CartDTO::getSkuId).collect(Collectors.toList());
        //得到每个sku对应的id和购买的数量
        Map<Long, Integer> skuNumAndId = carts.stream().collect(Collectors.toMap(CartDTO::getSkuId, CartDTO::getNum));
        List<SkuDTO> skuDTOS = itemClient.findSkuBySkuIds(skuIds);
        //总价格
        long totalFee = 0;
        ArrayList<TbOrderDetail> tbOrderDetails = new ArrayList<>();
        for (SkuDTO skuDTO : skuDTOS) {
            Long skuPrice = skuDTO.getPrice();
            Long skuId = skuDTO.getId();
            Integer num = skuNumAndId.get(skuId);
            totalFee += num * skuPrice;
            TbOrderDetail tbOrderDetail = new TbOrderDetail();
            tbOrderDetail.setOrderId(orderId);
            tbOrderDetail.setSkuId(skuId);
            tbOrderDetail.setNum(num);
            tbOrderDetail.setTitle(skuDTO.getTitle());
            tbOrderDetail.setOwnSpec(skuDTO.getOwnSpec());
            tbOrderDetail.setPrice(skuPrice);
            tbOrderDetail.setImage(skuDTO.getImages());
            tbOrderDetails.add(tbOrderDetail);
        }
        //3 实付金额
        long actualFee = totalFee;
        //4 订单状态
        TbOrder tbOrder = new TbOrder();
        tbOrder.setStatus(OrderStatusEnum.INIT.value());
        tbOrder.setOrderId(orderId);
        tbOrder.setTotalFee(totalFee);
        tbOrder.setActualFee(actualFee);
        tbOrder.setUserId(UserHolder.getUser());
        tbOrder.setPostFee(0L);
        tbOrder.setSourceType(2);
        tbOrder.setPaymentType(1);
        //保存order 订单
        boolean saveOrder = tbOrderService.save(tbOrder);
        if(!saveOrder){
            throw new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }
        //保存orderDetail 订单中的商品
        boolean saveDetail = tbOrderDetailService.saveBatch(tbOrderDetails);
        if(!saveDetail){
            throw new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }
        //保存OrderLogistics 订单中的物流
//        TODO 保存订单中的物流信息
        AddressDTO addressDTO = userClient.findAddress();
        if(addressDTO == null){
            throw new LyException(ExceptionEnum.INVALID_PARAM_ERROR);
        }
        //更改库存
        return orderId;
    }
}
