package com.ethan.es2.service;

import com.ethan.es2.entity.Content;

import java.util.List;

public interface ESService {

    //解析网页 爬取数据
    List<Content> getContentList(String keyword);

    //数据放入es中
    Boolean addDataToEs(String keywords);

    //在es中查找数据
    List<Object> searchKeywords(String keywords, Integer from, Integer size);

    //高亮搜索
    List<Object> highlightSearchKeywords(String keywords, Integer from, Integer size);
}
