package com.leyou.item.controller;

import com.leyou.item.service.TbCategoryService;
import com.leyou.pojo.DTO.CategoryDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @创建人 cwj
 * @创建时间 2019/8/28  20:24
 * @描述
 */
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private TbCategoryService tbCategoryService;

    /**
     * 根据父分类id查询分类
     * @param pid
     * @return
     */
    @GetMapping("/of/parent")
    public ResponseEntity<List<CategoryDTO>> findCategoryByParentId(@RequestParam(name = "pid") Long pid){
        return ResponseEntity.ok(tbCategoryService.findCategoryByParentId(pid));
    }

    /**
     * 查询多个分类
     * @param ids
     * @return
     */
    @GetMapping("/list")
    public ResponseEntity<List<CategoryDTO>> findCategoryById(@RequestParam(name = "ids") List<Long> ids){
        return ResponseEntity.ok(tbCategoryService.findCategoryById(ids));
    }

    /**
     * 根据品牌id查询分类id
     * @param brandId
     * @return
     */
    @GetMapping("/of/brand")
    public ResponseEntity<List<CategoryDTO>> findCategoryByBrandId(@RequestParam(name = "id") Long brandId){
        return ResponseEntity.ok(tbCategoryService.findCategoryByBrandId(brandId));
    }
}
