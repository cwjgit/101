package com.leyou;

import com.leyou.common.vo.PageResult;
import com.leyou.item.client.ItemClient;
import com.leyou.pojo.DTO.SpuDTO;
import com.leyou.search.pojo.Goods;
import com.leyou.search.repository.GoodsRepository;
import com.leyou.search.service.SearchService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @创建人 cwj
 * @创建时间 2019/9/3  10:16
 * @描述
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class LoadDataTest {

    @Autowired
    private ItemClient itemClient;
    @Autowired
    private SearchService searchService;
    @Autowired
    private GoodsRepository goodsRepository;

    @Test
    public void loadData(){
        int page = 1;
        int totle = 100;
        while (true){
            PageResult<SpuDTO> spuPage = itemClient.findSpuPage(page, totle, null, true);
            if(spuPage == null || CollectionUtils.isEmpty(spuPage.getItems())){
                break;
            }
            List<Goods> goods = spuPage.getItems().stream().map(searchService::copySputoGoods)
                    .collect(Collectors.toList());
            goodsRepository.saveAll(goods);
            if(goods.size() < totle){
                break;
            }
            page ++;
        }
    }
}
