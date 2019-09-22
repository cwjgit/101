package com.leyou.service.impl;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.item.client.ItemClient;
import com.leyou.pojo.DTO.*;
import com.leyou.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @创建人 cwj
 * @创建时间 2019/9/7  10:00
 * @描述
 */
@Service
public class PageServiceImpl implements PageService {

    @Autowired
    private ItemClient itemClient;
    @Autowired
    private SpringTemplateEngine templateEngine;

    /**
     * 获取id查询对应的信息传递页面 用来渲染静态页面
     * @param spuId
     * @return
     */
    @Override
    public Map<String, Object> toItemPage(Long spuId) {
        Map<String ,Object> attributes = new HashMap<>();
        //查询spu
        SpuDTO spuDTO = itemClient.findSpuById(spuId);
        //categories
        List<CategoryDTO> categoryDTOS = itemClient.findCategoryById(spuDTO.getCategoryIds());
        //brand
        BrandDTO brandDTO = itemClient.findBrandById(spuDTO.getBrandId());
        //spuName
        String spuName = spuDTO.getName();
        //subTitle
        String subTitle = spuDTO.getSubTitle();
        //detail
        SpuDetailDTO spuDetailDTO = itemClient.findDetailById(spuId);
        //skus
        List<SkuDTO> skus = itemClient.findSkuBySpuId(spuId);
        //specs
        List<SpecGroupDTO> specGroupDTOS = itemClient.findSpecOfCid(spuDTO.getCid3());
        attributes.put("categories",categoryDTOS);
        attributes.put("brand",brandDTO);
        attributes.put("spuName",spuName);
        attributes.put("subTitle",subTitle);
        attributes.put("detail",spuDetailDTO);
        attributes.put("skus",skus);
        attributes.put("specs",specGroupDTOS);
        return attributes;
    }

    @Override
    public void createItemHtml(Long id){
        Map<String, Object> map = toItemPage(id);
        //上下文
        Context context = new Context();
        //设置 动态数据
        context.setVariables(map);
        File file = new File("E:\\developtools\\nginx-1.14.0\\html\\item");
        if(!file.exists()){
            //判断文件是否存在 不存在创建 如果创建失败 就抛异常
            if(!file.mkdir()){
                throw new LyException(ExceptionEnum.FILE_WRITER_ERROR);
            }
        }
        File path = new File(file,id+".html");
        PrintWriter printWriter = null;
        try {
            printWriter = new PrintWriter(path);
            templateEngine.process("item",context,printWriter);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }finally {
            printWriter.close();
        }
    }

    /**
     * 根据spuId删除页面
     * @param spuId
     */
    @Override
    public void deletePage(Long spuId) {
        File file = new File("E:\\developtools\\nginx-1.14.0\\html\\item",spuId+".html");
        if (file.exists()){
            if(file.delete()){
                throw new LyException(ExceptionEnum.DELETE_OPERATION_FAIL);
            }
        }
    }
}
