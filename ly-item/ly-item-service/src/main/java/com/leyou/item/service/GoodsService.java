package com.leyou.item.service;

import com.leyou.common.vo.PageResult;
import com.leyou.pojo.DTO.SkuDTO;
import com.leyou.pojo.DTO.SpuDTO;
import com.leyou.pojo.DTO.SpuDetailDTO;

import java.util.List;

/**
 * @创建人 cwj
 * @创建时间 2019/8/31  20:15
 * @描述
 */
public interface GoodsService {

    PageResult<SpuDTO> findSpuPage(Integer page, Integer rows, String key, Boolean saleable);

    void saveSpuAndSku(SpuDTO spuDTO);

    void updateSaleable(Long spuId, Boolean saleable);

    SpuDetailDTO findDetailById(Long id);

    List<SkuDTO> findSkuBySpuId(Long spuId);

    void updateGoods(SpuDTO spuDTO);

    List<SkuDTO> findSkuBySkuIds(List<Long> ids);

    SpuDTO findSpuById(Long id);
}
