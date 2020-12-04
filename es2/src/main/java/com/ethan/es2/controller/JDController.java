package com.ethan.es2.controller;

import com.ethan.es2.entity.Content;
import com.ethan.es2.service.ESService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class JDController {

    @Autowired
    private ESService esService;


    //查询数据
    @RequestMapping(value = "/fetch")
    public List<Content> fetchData(String keyword){

        return this.esService.getContentList(keyword);
    }

    //根据关键字批量插入es中
    @RequestMapping(value = "/insert")
    public Boolean bulkInsertEs(String keywords){

        return this.esService.addDataToEs(keywords);
    }

    //在es中根据关键词查找
    @RequestMapping(value = "/search")
    public List<Object> search(String keywords, Integer from, Integer size){

        return this.esService.searchKeywords(keywords, from, size);
    }

    //在es中高亮查询
    @RequestMapping(value = "/highlight")
    public List<Object> highlightSearch(String keywords, Integer from, Integer size){

        return this.esService.highlightSearchKeywords(keywords, from, size);
    }
}
