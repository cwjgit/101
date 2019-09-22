package com.leyou;

import com.leyou.service.PageService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

/**
 * @创建人 cwj
 * @创建时间 2019/9/8  10:46
 * @描述
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class CreatStartPage {

    @Autowired
    private PageService pageService;

    @Test
    public void test(){
        List<Long> ids = Arrays.asList(2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L);
        for (Long id : ids) {
            pageService.createItemHtml(id);
        }
    }
}
