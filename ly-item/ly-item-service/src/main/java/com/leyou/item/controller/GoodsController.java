package com.leyou.item.controller;

import com.baomidou.mybatisplus.extension.api.R;
import com.leyou.common.vo.PageResult;
import com.leyou.item.service.GoodsService;
import com.leyou.pojo.DTO.SkuDTO;
import com.leyou.pojo.DTO.SpuDTO;
import com.leyou.pojo.DTO.SpuDetailDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @创建人 cwj
 * @创建时间 2019/8/31  20:09
 * @描述
 */
@RestController
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    /**
     * 分页查询spu
     * @param page 页码
     * @param rows 每页条数
     * @param key 模糊查询的条件
     * @return
     */
    @GetMapping("/spu/page")
    public ResponseEntity<PageResult<SpuDTO>> findSpuPage(@RequestParam(name = "page",defaultValue = "1") Integer page,
                                                          @RequestParam(name = "rows",defaultValue = "5") Integer rows,
                                                          @RequestParam(name = "key",required = false) String key,
                                                          @RequestParam(name = "saleable",required = false) Boolean saleable){
        return ResponseEntity.ok(goodsService.findSpuPage(page,rows,key,saleable));
    }

    /**
     * 新增商品信息 包括spu spuDetail Sku
     * @param spuDTO
     * @return
     */
    @PostMapping("/goods")
    public ResponseEntity<Void> saveSpuAndSku(@RequestBody SpuDTO spuDTO){
        goodsService.saveSpuAndSku(spuDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 修改商品上下架
     * @param spuId
     * @param saleable
     * @return
     */
    @PutMapping("/spu/saleable")
    public ResponseEntity<Void> updateSaleable(@RequestParam(name = "id") Long spuId,@RequestParam(name = "saleable") Boolean saleable){
        goodsService.updateSaleable(spuId,saleable);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 查询SpuDetail接口
     * @param id
     * @return
     */
    @GetMapping("/spu/detail")
    public ResponseEntity<SpuDetailDTO> findDetailById(@RequestParam(name = "id") Long id){
        return ResponseEntity.ok(goodsService.findDetailById(id));
    }

    /**
     * 根据spu的id查询Sku集合接口
     * @param spuId
     * @return
     */
    @GetMapping("/sku/of/spu")
    public ResponseEntity<List<SkuDTO>> findSkuBySpuId(@RequestParam(name = "id") Long spuId){
        return ResponseEntity.ok(goodsService.findSkuBySpuId(spuId));
    }


    /**
     * 修改商品信息 包括spu spuDetail Sku
     * @param spuDTO
     * @return
     */
    @PutMapping("/goods")
    public ResponseEntity<Void> updateGoods(@RequestBody SpuDTO spuDTO){
        goodsService.updateGoods(spuDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 传递sku的ids，获取sku的集合数据
     * @param ids
     * @return
     */
    @GetMapping("/sku/list")
    public ResponseEntity<List<SkuDTO>> findSkuBySkuIds(@RequestParam(name = "ids") List<Long> ids){
        return ResponseEntity.ok(goodsService.findSkuBySkuIds(ids));
    }

    /**
     * 根据spuId查询spu信息
     * @param id
     * @return
     */
    @GetMapping("/spu/{id}")
    public ResponseEntity<SpuDTO> findSpuById(@PathVariable("id") Long id){
        return ResponseEntity.ok(goodsService.findSpuById(id));
    }
}
