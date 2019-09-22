package com.leyou.item.service.impl;

import com.leyou.item.entity.TbSku;
import com.leyou.item.mapper.TbSkuMapper;
import com.leyou.item.service.TbSkuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * sku表,该表表示具体的商品实体,如黑色的 64g的iphone 8 服务实现类
 * </p>
 *
 * @author HM
 * @since 2019-08-27
 */
@Service
public class TbSkuServiceImpl extends ServiceImpl<TbSkuMapper, TbSku> implements TbSkuService {

}
