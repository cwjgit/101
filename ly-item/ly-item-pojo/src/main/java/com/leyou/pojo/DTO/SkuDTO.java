package com.leyou.pojo.DTO;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * sku表,该表表示具体的商品实体,如黑色的 64g的iphone 8
 * </p>
 *
 * @author HM
 * @since 2019-08-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SkuDTO {

    private Long id;

    /**
     * spu id
     */
    private Long spuId;

    /**
     * 商品标题
     */
    private String title;

    /**
     * 商品的图片，多个图片以‘,’分割
     */
    private String images;

    /**
     * 库存
     */
    private Integer stock;

    /**
     * 销售价格，单位为分
     */
    private Long price;

    /**
     * 特有规格属性在spu属性模板中的对应下标组合
     */
    private String indexes;

    /**
     * sku的特有规格参数键值对，json格式，反序列化时请使用linkedHashMap，保证有序
     */
    private String ownSpec;

    /**
     * 是否有效，0无效，1有效
     */
    private Boolean enable;


}
