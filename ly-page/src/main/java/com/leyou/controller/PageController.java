package com.leyou.controller;

import com.leyou.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

/**
 * @创建人 cwj
 * @创建时间 2019/9/7  9:54
 * @描述
 */
@Controller
public class PageController {

    @Autowired
    private PageService pageService;

    /**
     * 获取id查询对应的信息传递页面 用来渲染静态页面
     * @param model
     * @param spuId
     * @return
     */
    @GetMapping("item/{id}.html")
    public String toItemPage(Model model , @PathVariable(name = "id") Long spuId){
        Map<String ,Object> attributes = pageService.toItemPage(spuId);
        model.addAllAttributes(attributes);
        return "item";
    }
}
