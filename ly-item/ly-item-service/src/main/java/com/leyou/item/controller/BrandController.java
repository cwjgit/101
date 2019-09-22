package com.leyou.item.controller;

import com.leyou.common.vo.PageResult;
import com.leyou.item.entity.TbBrand;
import com.leyou.item.service.TbBrandService;
import com.leyou.pojo.DTO.BrandDTO;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @创建人 cwj
 * @创建时间 2019/8/29  10:04
 * @描述
 */
@RestController
@RequestMapping("/brand")
public class BrandController {

    @Autowired
    private TbBrandService tbBrandService;

    /**
     * 根据指定条件查询品牌信息
     * @param key    模糊查询条件 不是必要条件
     * @param page   当前页数 默认为1
     * @param rows   每页显示条数 默认10
     * @param sortBy 需要排序的字段
     * @param desc   排序规则 默认Asc
     */
    @GetMapping("/page")
    public ResponseEntity<PageResult<BrandDTO>> findBrandByPage(@RequestParam(value = "key", required = false) String key,
                                                                @RequestParam(value = "page", defaultValue = "1") Integer page,
                                                                @RequestParam(value = "rows", defaultValue = "5") Integer rows,
                                                                @RequestParam(value = "sortBy", required = false) String sortBy,
                                                                @RequestParam(value = "desc", defaultValue = "false") Boolean desc
    ) {
        return ResponseEntity.ok(tbBrandService.findBrandByPage(key, page, rows, sortBy, desc));
    }

    /**
     * 添加品牌信息
     * @param tbBrand 品牌信息
     * @param cids  对应的分类id
     * @return
     */
    @PostMapping
    public ResponseEntity<Void> saveBrand(TbBrand tbBrand, @RequestParam("cids") List<Long> cids){
        tbBrandService.saveBrand(tbBrand,cids);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 修改品牌信息
     * @param tbBrand
     * @param cids
     * @return
     */
    @PutMapping
    public ResponseEntity<Void> updateBrand(TbBrand tbBrand, @RequestParam("cids") List<Long> cids){
        tbBrandService.updateBrand(tbBrand,cids);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 根据id查询品牌
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ResponseEntity<BrandDTO> findBrandById(@PathVariable("id") Long id){
        return ResponseEntity.ok(tbBrandService.findBrandById(id));
    }

    /**
     * 根据分类id查询品牌信息
     * @param cid
     * @return
     */
    @GetMapping("/of/category")
    public ResponseEntity<List<BrandDTO>> findBrandByCid(@RequestParam(name = "id") Long cid){
        return ResponseEntity.ok(tbBrandService.findBrandByCid(cid));
    }

    /**
     * 传递品牌的的ids，获取品牌的集合数据
     * @param ids
     * @return
     */
    @GetMapping("/list")
    public ResponseEntity<List<BrandDTO>> findBrandByIds(@RequestParam(name = "ids") List<Long> ids){
        return ResponseEntity.ok(tbBrandService.findBrandByIds(ids));
    }
}
