package com.leyou.item.client;

import com.leyou.common.vo.PageResult;
import com.leyou.pojo.DTO.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @创建人 cwj
 * @创建时间 2019/9/2  20:10
 * @描述
 */
@FeignClient("item-service")
public interface ItemClient {

    /**
     * 根据spu的id查询Sku集合接口
     * @param spuId
     * @return
     */
    @GetMapping("/sku/of/spu")
    List<SkuDTO> findSkuBySpuId(@RequestParam(name = "id") Long spuId);

    /**
     * 查询多个分类
     * @param ids
     * @return
     */
    @GetMapping("/category/list")
    List<CategoryDTO> findCategoryById(@RequestParam(name = "ids") List<Long> ids);

    /**
     * 根据id查询品牌
     * @param id
     * @return
     */
    @GetMapping("/brand/{id}")
    BrandDTO findBrandById(@PathVariable("id") Long id);

    /**
     * 查询规格参数信息
     * @param gid 分组id
     * @param cid 分类id
     * @param searching 是否搜索
     * @return
     */
    @GetMapping("/spec/params")
    List<SpecParamDTO> findSpecParamByParam(@RequestParam(name = "gid",required = false) Long gid ,
                                            @RequestParam(name = "cid",required = false) Long cid,
                                            @RequestParam(name = "searching",required = false) Boolean searching);

    /**
     * 查询SpuDetail接口
     * @param id
     * @return
     */
    @GetMapping("/spu/detail")
    SpuDetailDTO findDetailById(@RequestParam(name = "id") Long id);

    /**
     * 分页查询spu
     * @param page 页码
     * @param rows 每页条数
     * @param key 模糊查询的条件
     * @return
     */
    @GetMapping("/spu/page")
    PageResult<SpuDTO> findSpuPage(@RequestParam(name = "page",defaultValue = "1") Integer page,
                                   @RequestParam(name = "rows",defaultValue = "5") Integer rows,
                                   @RequestParam(name = "key",required = false) String key,
                                   @RequestParam(name = "saleable",required = false) Boolean saleable);

    /**
     * 传递品牌的的ids，获取品牌的集合数据
     * @param ids
     * @return
     */
    @GetMapping("/brand/list")
     List<BrandDTO> findBrandByIds(@RequestParam(name = "ids") List<Long> ids);

    /**
     * 根据spuId查询spu信息
     * @param id
     * @return
     */
    @GetMapping("/spu/{id}")
    SpuDTO findSpuById(@PathVariable("id") Long id);

    /**
     *根据categoryId查询规格参数组和组内参数
     * @param cid
     * @return
     */
    @GetMapping("/spec/of/category")
    List<SpecGroupDTO> findSpecOfCid(@RequestParam("id") Long cid);

    /**
     * 传递sku的ids，获取sku的集合数据
     * @param ids
     * @return
     */
    @GetMapping("/sku/list")
    List<SkuDTO> findSkuBySkuIds(@RequestParam(name = "ids") List<Long> ids);
}
