package com.leyou.item.service;

import com.leyou.item.entity.TbCategory;
import com.baomidou.mybatisplus.extension.service.IService;
import com.leyou.pojo.DTO.CategoryDTO;

import java.util.List;

/**
 * <p>
 * 商品类目表，类目和商品(spu)是一对多关系，类目与品牌是多对多关系 服务类
 * </p>
 *
 * @author HM
 * @since 2019-08-27
 */
public interface TbCategoryService extends IService<TbCategory> {

    List<CategoryDTO> findCategoryByParentId(Long pid);

    List<CategoryDTO> findCategoryById(List<Long> ids);

    List<CategoryDTO> findCategoryByBrandId(Long brandId);
}
