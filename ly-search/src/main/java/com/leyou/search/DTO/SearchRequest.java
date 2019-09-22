package com.leyou.search.DTO;

import java.util.Map;

/**
 * @创建人 cwj
 * @创建时间 2019/9/3  22:03
 * @描述
 */
public class SearchRequest {

    private Integer page;// 当前页
    private String key;// 搜索条件
    private Map<String ,String> filter; //选中的过滤项
    private static final Integer DEFAULT_SIZE = 20;// 每页大小，不从页面接收，而是固定大小
    private static final Integer DEFAULT_PAGE = 1;// 默认页

    public Map<String, String> getFilter() {
        return filter;
    }

    public void setFilter(Map<String, String> filter) {
        this.filter = filter;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Integer getPage() {
        if (page == null) {
            return DEFAULT_PAGE;
        }
        // 获取页码时做一些校验，不能小于1
        return Math.max(DEFAULT_PAGE, page);
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return DEFAULT_SIZE;
    }
}
