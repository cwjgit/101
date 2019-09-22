package com.leyou.pojo.DTO;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @创建人 cwj
 * @创建时间 2019/8/28  20:30
 * @描述
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class CategoryDTO {
    private Long id;

    /**
     * 类目名称
     */
    private String name;

    /**
     * 父类目id,顶级类目填0
     */
    private Long parentId;

    /**
     * 是否为父节点，0为否，1为是
     */
    private Boolean isParent;

    /**
     * 排序指数，越小越靠前
     */
    private Integer sort;

}
