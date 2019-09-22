package com.leyou.item.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.BeanHelper;
import com.leyou.item.entity.TbCategory;
import com.leyou.item.entity.TbCategoryBrand;
import com.leyou.item.mapper.TbCategoryMapper;
import com.leyou.item.service.TbCategoryBrandService;
import com.leyou.item.service.TbCategoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leyou.pojo.DTO.CategoryDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 商品类目表，类目和商品(spu)是一对多关系，类目与品牌是多对多关系 服务实现类
 * </p>
 *
 * @author HM
 * @since 2019-08-27
 */
@Service
public class TbCategoryServiceImpl extends ServiceImpl<TbCategoryMapper, TbCategory> implements TbCategoryService {

    @Autowired
    private TbCategoryBrandService tbCategoryBrandService;
    @Autowired
    private TbCategoryMapper tbCategoryMapper;

    @Override
    public List<CategoryDTO> findCategoryByParentId(Long pid) {
        QueryWrapper<TbCategory> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(TbCategory::getParentId,pid);
        List<TbCategory> tbCategories = this.list(queryWrapper);
        //判断是否查出数据
        if(CollectionUtils.isEmpty(tbCategories)){
            throw new LyException(ExceptionEnum.CATEGORY_NOT_FOUND);
        }
        return BeanHelper.copyWithCollection(tbCategories,CategoryDTO.class);
    }

    @Override
    public List<CategoryDTO> findCategoryById(List<Long> ids) {
        QueryWrapper<TbCategory> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().in(TbCategory::getId,ids);
        List<TbCategory> tbCategories = this.list(queryWrapper);
        if(CollectionUtils.isEmpty(tbCategories)){
            throw new LyException(ExceptionEnum.CATEGORY_NOT_FOUND);
        }
        return BeanHelper.copyWithCollection(tbCategories,CategoryDTO.class);
    }

    /**
     * 根据品牌id查询分类id
     * @param brandId
     * @return
     */
    @Override
    public List<CategoryDTO> findCategoryByBrandId(Long brandId) {
        //根据brandId查询中间表所有
        QueryWrapper<TbCategoryBrand> tbCategoryBrandQueryWrapper = new QueryWrapper<>();
        tbCategoryBrandQueryWrapper.lambda().eq(TbCategoryBrand::getBrandId,brandId);
        List<TbCategoryBrand> tbCategoryBrands = tbCategoryBrandService.list(tbCategoryBrandQueryWrapper);
        if(CollectionUtils.isEmpty(tbCategoryBrands)){
            throw new LyException(ExceptionEnum.CATEGORY_NOT_FOUND);
        }
        //创建list集合 把查询到的分类对象放入集合
        ArrayList<TbCategory> tbCategories = new ArrayList<>();
        //循环所有数据找到对应的分类id
        for (TbCategoryBrand tbCategoryBrand : tbCategoryBrands) {
            Long categoryId = tbCategoryBrand.getCategoryId();
            TbCategory tbCategory = tbCategoryMapper.selectById(categoryId);
            tbCategories.add(tbCategory);
        }
        if(CollectionUtils.isEmpty(tbCategories)){
            throw new LyException(ExceptionEnum.CATEGORY_NOT_FOUND);
        }
        return BeanHelper.copyWithCollection(tbCategories,CategoryDTO.class);
    }
}
