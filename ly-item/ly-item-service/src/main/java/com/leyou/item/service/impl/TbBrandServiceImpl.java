package com.leyou.item.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.BeanHelper;
import com.leyou.common.vo.PageResult;
import com.leyou.item.entity.TbBrand;
import com.leyou.item.entity.TbCategoryBrand;
import com.leyou.item.mapper.TbBrandMapper;
import com.leyou.item.service.TbBrandService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leyou.item.service.TbCategoryBrandService;
import com.leyou.pojo.DTO.BrandDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * <p>
 * 品牌表，一个品牌下有多个商品（spu），一对多关系 服务实现类
 * </p>
 *
 * @author HM
 * @since 2019-08-27
 */
@Service
public class TbBrandServiceImpl extends ServiceImpl<TbBrandMapper, TbBrand> implements TbBrandService {

    @Autowired
    private TbCategoryBrandService tbCategoryBrandService;
    @Autowired
    private TbBrandMapper tbBrandMapper;

    /**
     * @param key    模糊查询条件 不是必要条件
     * @param page   当前页数 默认为1
     * @param rows   每页显示条数 默认10
     * @param sortBy 需要排序的字段
     * @param desc   排序规则 默认Asc
     */
    @Override
    public PageResult<BrandDTO> findBrandByPage(String key, Integer page, Integer rows, String sortBy, Boolean desc) {
        IPage<TbBrand> iPage = new Page<>(page,rows);
        QueryWrapper<TbBrand> queryWrapper = new QueryWrapper<>();
        if (!StringUtils.isEmpty(key)) {
            queryWrapper.lambda().like(TbBrand::getName, key).or().like(TbBrand::getLetter, key);
        }
        if (!StringUtils.isEmpty(sortBy)) {
            if (desc) {
                queryWrapper.orderByDesc(sortBy);
            } else {
                queryWrapper.orderByAsc(sortBy);
            }
        }
        IPage<TbBrand> tbBrandIPage = this.page(iPage, queryWrapper);
        if (tbBrandIPage == null || CollectionUtils.isEmpty(tbBrandIPage.getRecords())) {
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        return new PageResult<BrandDTO>(tbBrandIPage.getTotal(),
                Integer.parseInt(String.valueOf(tbBrandIPage.getPages())),//页数转换为string然后转换为Integer
                BeanHelper.copyWithCollection(tbBrandIPage.getRecords(), BrandDTO.class));//获取到查询出的集合 TbBrand转换为BrandDTO赋值给自定义的页面vo
    }

    /**
     * 添加品牌信息
     * @param tbBrand 品牌信息
     * @param cids  对应的分类id
     * @return
     */
    @Override
    @Transactional
    public void saveBrand(TbBrand tbBrand, List<Long> cids) {
        boolean saveB = this.save(tbBrand);
        if(!saveB){
            throw new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }
        Long tbBrandId = tbBrand.getId();
        ArrayList<TbCategoryBrand> tbCategoryBrands = new ArrayList<>();
        for (Long cid : cids) {
            TbCategoryBrand tbCategoryBrand = new TbCategoryBrand();
            tbCategoryBrand.setBrandId(tbBrandId);
            tbCategoryBrand.setCategoryId(cid);
            tbCategoryBrands.add(tbCategoryBrand);
        }
        boolean saveCB = tbCategoryBrandService.saveBatch(tbCategoryBrands);
        if(!saveCB){
            throw new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }
    }

    /**
     * 修改品牌信息
     * @param tbBrand
     * @param cids
     * @return
     */
    @Override
    @Transactional
    public void updateBrand(TbBrand tbBrand, List<Long> cids) {
        //修改brand信息
        boolean updateB = this.updateById(tbBrand);
        if(!updateB){
            throw new LyException(ExceptionEnum.UPDATE_OPERATION_FAIL);
        }
        Long tbBrandId = tbBrand.getId();
        //删除中间表信息
        QueryWrapper<TbCategoryBrand> tbCategoryBrandQueryWrapper = new QueryWrapper<>();
        tbCategoryBrandQueryWrapper.lambda().eq(TbCategoryBrand::getBrandId,tbBrandId);
        boolean remove = tbCategoryBrandService.remove(tbCategoryBrandQueryWrapper);
        if(!remove){
            throw new LyException(ExceptionEnum.DELETE_OPERATION_FAIL);
        }
        //添加中间表信息
        ArrayList<TbCategoryBrand> tbCategoryBrands = new ArrayList<>();
        for (Long cid : cids) {
            TbCategoryBrand tbCategoryBrand = new TbCategoryBrand();
            tbCategoryBrand.setBrandId(tbBrandId);
            tbCategoryBrand.setCategoryId(cid);
            tbCategoryBrands.add(tbCategoryBrand);
        }
        boolean saveCB = tbCategoryBrandService.saveBatch(tbCategoryBrands);
        if(!saveCB){
            throw new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }
    }

    /**
     * 根据id查询品牌
     * @param id
     * @return
     */
    @Override
    public BrandDTO findBrandById(Long id) {
        TbBrand tbBrand = tbBrandMapper.selectById(id);
        return BeanHelper.copyProperties(tbBrand,BrandDTO.class);
    }

    /**
     * 根据分类id查询品牌信息
     * @param cid
     * @return
     */
    @Override
    public List<BrandDTO> findBrandByCid(Long cid) {
        List<TbBrand> tbBrands = tbBrandMapper.findBrandByCid(cid);
        return BeanHelper.copyWithCollection(tbBrands,BrandDTO.class);
    }

    /**
     * 传递品牌的的ids，获取品牌的集合数据
     * @param ids
     * @return
     */
    @Override
    public List<BrandDTO> findBrandByIds(List<Long> ids) {
        Collection<TbBrand> tbBrands = this.listByIds(ids);
        if(CollectionUtils.isEmpty(tbBrands)){
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        return BeanHelper.copyWithCollection((List)tbBrands,BrandDTO.class);
    }
}
