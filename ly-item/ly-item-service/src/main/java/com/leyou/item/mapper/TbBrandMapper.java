package com.leyou.item.mapper;

import com.leyou.item.entity.TbBrand;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 品牌表，一个品牌下有多个商品（spu），一对多关系 Mapper 接口
 * </p>
 *
 * @author HM
 * @since 2019-08-27
 */
public interface TbBrandMapper extends BaseMapper<TbBrand> {

    @Select("select b.id,b.name,b.letter from tb_category_brand a inner join  tb_brand b on a.brand_id = b.id where a.category_id = #{cid}")
    List<TbBrand> findBrandByCid(@Param("cid") Long cid);
}
