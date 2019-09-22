package com.leyou.common.vo;

import lombok.Data;

import java.util.List;

/**
 * @创建人 cwj
 * @创建时间 2019/8/29  10:25
 * @描述
 */
@Data
public class PageResult<T> {
    private Long total;// 总条数
    private Integer totalPage;// 总页数
    private List<T> items;// 当前页数据

    public PageResult(Long total, Integer totalPage, List<T> items) {
        this.total = total;
        this.totalPage = totalPage;
        this.items = items;
    }

    public PageResult() {
    }
}
