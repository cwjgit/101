package com.leyou.pojo.DTO;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @创建人 cwj
 * @创建时间 2019/8/29  10:10
 * @描述
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BrandDTO {
    private Long id;

    /**
     * 品牌名称
     */
    private String name;

    /**
     * 品牌图片地址
     */
    private String image;

    /**
     * 品牌的首字母
     */
    private String letter;

}
