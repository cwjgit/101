package com.leyou.item.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leyou.common.constants.MqConstants;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.BeanHelper;
import com.leyou.common.vo.PageResult;
import com.leyou.item.entity.*;
import com.leyou.item.service.*;
import com.leyou.pojo.DTO.SkuDTO;
import com.leyou.pojo.DTO.SpuDTO;
import com.leyou.pojo.DTO.SpuDetailDTO;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @创建人 cwj
 * @创建时间 2019/8/31  20:16
 * @描述
 */
@Service
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private TbSpuService tbSpuService;
    @Autowired
    private TbSpuDetailService tbSpuDetailService;
    @Autowired
    private TbSkuService tbSkuService;
    @Autowired
    private TbBrandService tbBrandService;
    @Autowired
    private TbCategoryService tbCategoryService;
    @Autowired
    private AmqpTemplate amqpTemplate;

    /**
     * 分页查询spu
     *
     * @param page 页码
     * @param rows 每页条数
     * @param key  模糊查询的条件
     * @return
     */
    @Override
    public PageResult<SpuDTO> findSpuPage(Integer page, Integer rows, String key, Boolean saleable) {
        Page<TbSpu> tbSpuPage = new Page<>(page, rows);
        QueryWrapper<TbSpu> tbSpuQueryWrapper = new QueryWrapper<>();
        if (!StringUtils.isEmpty(key)) {
            tbSpuQueryWrapper.lambda().like(TbSpu::getName, key);
        }
        if (saleable != null) {
            tbSpuQueryWrapper.lambda().eq(TbSpu::getSaleable, saleable);
        }
        IPage<TbSpu> spuIPage = tbSpuService.page(tbSpuPage, tbSpuQueryWrapper);
        if (spuIPage == null || CollectionUtils.isEmpty(spuIPage.getRecords())) {
            throw new LyException(ExceptionEnum.GOODS_NOT_FOUND);
        }
        List<SpuDTO> spuDTOS = BeanHelper.copyWithCollection(spuIPage.getRecords(), SpuDTO.class);
        //自定义方法查询brandName和categoryName
        spuDTOS = getBrandNameAndCategoryName(spuDTOS);
        return new PageResult<SpuDTO>(spuIPage.getTotal(), Integer.parseInt(String.valueOf(spuIPage.getPages())), spuDTOS);
    }

    private List<SpuDTO> getBrandNameAndCategoryName(List<SpuDTO> spuDTOS) {
        for (SpuDTO spuDTO : spuDTOS) {
            //查询品牌名字赋值
            TbBrand tbBrand = tbBrandService.getById(spuDTO.getBrandId());
            spuDTO.setBrandName(tbBrand.getName());
            //查询分类名字赋值
            List<Long> categoryIds = spuDTO.getCategoryIds();
            Collection<TbCategory> tbCategories = tbCategoryService.listByIds(categoryIds);
            String categoryName = tbCategories.stream().map(TbCategory::getName).collect(Collectors.joining("/"));
            spuDTO.setCategoryName(categoryName);
        }
        return spuDTOS;
    }

    /**
     * 新增商品信息 包括spu spuDetail Sku
     *
     * @param spuDTO
     * @return
     */
    @Override
    @Transactional
    public void saveSpuAndSku(SpuDTO spuDTO) {
        //保存spu
        TbSpu tbSpu = BeanHelper.copyProperties(spuDTO, TbSpu.class);
        tbSpu.setCreateTime(null);
        boolean saveSpu = tbSpuService.save(tbSpu);
        if (!saveSpu) {
            throw new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }
        Long tbSpuId = tbSpu.getId();
        //保存spuDetail
        TbSpuDetail tbSpuDetail = BeanHelper.copyProperties(spuDTO.getSpuDetail(), TbSpuDetail.class);
        tbSpuDetail.setSpuId(tbSpuId);
        boolean saveSpuDetail = tbSpuDetailService.save(tbSpuDetail);
        if (!saveSpuDetail) {
            throw new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }
        //保存sku
        List<TbSku> tbSkus = spuDTO.getSkus().stream().map(skuDTO -> {
            skuDTO.setSpuId(tbSpuId);
            return BeanHelper.copyProperties(skuDTO, TbSku.class);
        }).collect(Collectors.toList());
        boolean saveSku = tbSkuService.saveBatch(tbSkus);
        if (!saveSku) {
            throw new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }
    }

    /**
     * 修改商品上下架
     *
     * @param spuId
     * @param saleable
     * @return
     */
    @Override
    @Transactional
    public void updateSaleable(Long spuId, Boolean saleable) {
        TbSpu tbSpu = new TbSpu();
        tbSpu.setId(spuId);
        tbSpu.setSaleable(saleable);
        boolean updateSpu = tbSpuService.updateById(tbSpu);
        if (!updateSpu) {
            throw new LyException(ExceptionEnum.UPDATE_OPERATION_FAIL);
        }
        UpdateWrapper<TbSku> tbSkuUpdateWrapper = new UpdateWrapper<>();
        tbSkuUpdateWrapper.lambda().eq(TbSku::getSpuId, spuId);
        tbSkuUpdateWrapper.lambda().set(TbSku::getEnable, saleable);
        boolean updateSku = tbSkuService.update(tbSkuUpdateWrapper);
        if (!updateSku) {
            throw new LyException(ExceptionEnum.UPDATE_OPERATION_FAIL);
        }
        String key = saleable ? MqConstants.RoutingKey.ITEM_UP_KEY : MqConstants.RoutingKey.ITEM_DOWN_KEY;
        amqpTemplate.convertAndSend(MqConstants.Exchange.ITEM_EXCHANGE_NAME, key, spuId);
    }

    /**
     * 查询SpuDetail接口
     *
     * @param id
     * @return
     */
    @Override
    public SpuDetailDTO findDetailById(Long id) {
        TbSpuDetail tbSpuDetail = tbSpuDetailService.getById(id);
        if (tbSpuDetail == null) {
            throw new LyException(ExceptionEnum.GOODS_NOT_FOUND);
        }
        return BeanHelper.copyProperties(tbSpuDetail, SpuDetailDTO.class);
    }

    /**
     * 根据spu的id查询Sku集合接口
     *
     * @param spuId
     * @return
     */
    @Override
    public List<SkuDTO> findSkuBySpuId(Long spuId) {
        QueryWrapper<TbSku> tbSkuQueryWrapper = new QueryWrapper<>();
        tbSkuQueryWrapper.lambda().eq(TbSku::getSpuId, spuId);
        List<TbSku> tbSkus = tbSkuService.list(tbSkuQueryWrapper);
        if (CollectionUtils.isEmpty(tbSkus)) {
            throw new LyException(ExceptionEnum.GOODS_NOT_FOUND);
        }
        return BeanHelper.copyWithCollection(tbSkus, SkuDTO.class);
    }

    /**
     * 修改商品信息 包括spu spuDetail Sku
     *
     * @param spuDTO
     * @return
     */
    @Override
    @Transactional
    public void updateGoods(SpuDTO spuDTO) {
        //删除sku
        Long spuId = spuDTO.getId();
        if (spuId == null) {
            throw new LyException(ExceptionEnum.INVALID_PARAM_ERROR);
        }
        QueryWrapper<TbSku> tbSkuQueryWrapper = new QueryWrapper<>();
        tbSkuQueryWrapper.lambda().eq(TbSku::getSpuId, spuId);
        boolean remove = tbSkuService.remove(tbSkuQueryWrapper);
        if (!remove) {
            throw new LyException(ExceptionEnum.DELETE_OPERATION_FAIL);
        }
        //修改spu
        TbSpu tbSpu = BeanHelper.copyProperties(spuDTO, TbSpu.class);
        tbSpu.setCreateTime(null);
        boolean updateSpu = tbSpuService.updateById(tbSpu);
        if (!updateSpu) {
            throw new LyException(ExceptionEnum.UPDATE_OPERATION_FAIL);
        }
        //修改spuDetail
        TbSpuDetail tbSpuDetail = BeanHelper.copyProperties(spuDTO.getSpuDetail(), TbSpuDetail.class);
        tbSpuDetail.setSpuId(spuId);
        boolean updateSpuDetail = tbSpuDetailService.updateById(tbSpuDetail);
        if (!updateSpuDetail) {
            throw new LyException(ExceptionEnum.UPDATE_OPERATION_FAIL);
        }
        //新增sku
        List<TbSku> tbSkus = spuDTO.getSkus().stream().map(skuDTO -> {
            skuDTO.setSpuId(spuId);
            skuDTO.setEnable(false);
            return BeanHelper.copyProperties(skuDTO, TbSku.class);
        }).collect(Collectors.toList());
        boolean saveSkus = tbSkuService.saveBatch(tbSkus);
        if (!saveSkus) {
            throw new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }
    }

    /**
     * 传递sku的ids，获取sku的集合数据
     *
     * @param ids
     * @return
     */
    @Override
    public List<SkuDTO> findSkuBySkuIds(List<Long> ids) {
        Collection<TbSku> tbSkus = tbSkuService.listByIds(ids);
        if (CollectionUtils.isEmpty(tbSkus)) {
            throw new LyException(ExceptionEnum.GOODS_NOT_FOUND);
        }
        return BeanHelper.copyWithCollection((List) tbSkus, SkuDTO.class);
    }

    /**
     * 根据spuId查询spu信息
     *
     * @param id
     * @return
     */
    @Override
    public SpuDTO findSpuById(Long id) {
        TbSpu tbSpu = tbSpuService.getById(id);
        if (tbSpu == null) {
            throw new LyException(ExceptionEnum.GOODS_NOT_FOUND);
        }
        return BeanHelper.copyProperties(tbSpu, SpuDTO.class);
    }
}
