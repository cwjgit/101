package com.leyou.item.controller;

import com.leyou.item.entity.TbSpecGroup;
import com.leyou.item.entity.TbSpecParam;
import com.leyou.item.service.SpecService;
import com.leyou.pojo.DTO.SpecGroupDTO;
import com.leyou.pojo.DTO.SpecParamDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @创建人 cwj
 * @创建时间 2019/8/31  10:26
 * @描述
 */
@RestController
@RequestMapping("/spec")
public class SpecController {

    @Autowired
    private SpecService specService;

    /**
     * 根据分类id查询规格组信息
     * @param cid
     * @return
     */
    @GetMapping("/groups/of/category")
    public ResponseEntity<List<SpecGroupDTO>> findSpecGroupById(@RequestParam(name = "id") Long cid){
        return ResponseEntity.ok(specService.findSpecGroupById(cid));
    }

    /**
     * 新增规格组信息
     * @param tbSpecGroup
     * @return
     */
    @PostMapping("/group")
    public ResponseEntity<Void> saveSpecGroup(@RequestBody TbSpecGroup tbSpecGroup){
        specService.saveSpecGroup(tbSpecGroup);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 查询规格参数信息
     * @param gid 分组id
     * @param cid 分类id
     * @param searching 是否搜索
     * @return
     */
    @GetMapping("/params")
    public ResponseEntity<List<SpecParamDTO>> findSpecParamByParam(@RequestParam(name = "gid",required = false) Long gid ,
                                                                   @RequestParam(name = "cid",required = false) Long cid,
                                                                   @RequestParam(name = "searching",required = false) Boolean searching
                                                                   ){
        return ResponseEntity.ok(specService.findSpecParamByParam(gid,cid,searching));
    }

    /**
     * 更新规格分组信息
     * @param tbSpecGroup
     * @return
     */
    @PutMapping("/group")
    public ResponseEntity<Void> updateSpecGroup(@RequestBody TbSpecGroup tbSpecGroup){
        specService.updateSpecGroup(tbSpecGroup);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 删除规格分组信息
     * @param id
     * @return
     */
    @DeleteMapping("/group/{id}")
    public ResponseEntity<Void> deleteSpecGroup(@PathVariable("id") Long id){
        specService.deleteSpecGroup(id);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 添加规格属性信息
     * @param specParamDTO
     * @return
     */
    @PostMapping("/param")
    public ResponseEntity<Void> saveSpecParam(@RequestBody SpecParamDTO specParamDTO){
        specService.saveSpecParam(specParamDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    /**
     * 删除规格属性信息
     * @param id
     * @return
     */
    @DeleteMapping("/param/{id}")
    public ResponseEntity<Void> deleteSpecParam(@PathVariable("id") Long id){
        specService.deleteSpecParam(id);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 更新规格属性信息
     * @param specParamDTO
     * @return
     */
    @PutMapping("/param")
    public ResponseEntity<Void> updateSpecParam(@RequestBody SpecParamDTO specParamDTO){
        specService.updateSpecParam(specParamDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     *根据categoryId查询规格参数组和组内参数
     * @param cid
     * @return
     */
    @GetMapping("/of/category")
    public ResponseEntity<List<SpecGroupDTO>> findSpecOfCid(@RequestParam("id") Long cid){
        return ResponseEntity.ok(specService.findSpecOfCid(cid));
    }
}
