package com.leyou.search.repository;

import com.leyou.search.pojo.Goods;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @创建人 cwj
 * @创建时间 2019/9/3  20:28
 * @描述
 */
public interface GoodsRepository extends ElasticsearchRepository<Goods,Long> {
}
