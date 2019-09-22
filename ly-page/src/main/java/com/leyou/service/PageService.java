package com.leyou.service;

import java.util.Map;

/**
 * @创建人 cwj
 * @创建时间 2019/9/7  9:59
 * @描述
 */
public interface PageService {
    Map<String,Object> toItemPage(Long spuId);

    void createItemHtml(Long id);

    void deletePage(Long spuId);
}
