package com.leyou.item.service.impl;

import com.leyou.item.entity.TbSpu;
import com.leyou.item.mapper.TbSpuMapper;
import com.leyou.item.service.TbSpuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * spu表，该表描述的是一个抽象性的商品，比如 iphone8 服务实现类
 * </p>
 *
 * @author HM
 * @since 2019-08-27
 */
@Service
public class TbSpuServiceImpl extends ServiceImpl<TbSpuMapper, TbSpu> implements TbSpuService {

}
