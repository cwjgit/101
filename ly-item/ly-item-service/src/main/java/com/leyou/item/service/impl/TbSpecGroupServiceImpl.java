package com.leyou.item.service.impl;

import com.leyou.item.entity.TbSpecGroup;
import com.leyou.item.mapper.TbSpecGroupMapper;
import com.leyou.item.service.TbSpecGroupService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 规格参数的分组表，每个商品分类下有多个规格参数组 服务实现类
 * </p>
 *
 * @author HM
 * @since 2019-08-27
 */
@Service
public class TbSpecGroupServiceImpl extends ServiceImpl<TbSpecGroupMapper, TbSpecGroup> implements TbSpecGroupService {

}
