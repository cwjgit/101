package com.leyou.search.controller;

import com.leyou.common.vo.PageResult;
import com.leyou.search.DTO.GoodDTO;
import com.leyou.search.DTO.SearchRequest;
import com.leyou.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @创建人 cwj
 * @创建时间 2019/9/3  21:58
 * @描述
 */
@RestController
public class SearchController {

    @Autowired
    private SearchService searchService;

    /**
     * 用户输入关键词，进行搜索
     * @param searchRequest
     * @return
     */
    @PostMapping("/page")
    public ResponseEntity<PageResult<GoodDTO>> search(@RequestBody SearchRequest searchRequest){
        return ResponseEntity.ok(searchService.search(searchRequest));
    }

    /**
     * 根据输入关键字查询的结果来查询对应的过滤条件(分类和品牌)
     * @param request
     * @return
     */
    @PostMapping("/filter")
    public ResponseEntity<Map<String ,List<?>>> searchFilter(@RequestBody SearchRequest request){
        return ResponseEntity.ok(searchService.searchFilter(request));
    }
}
