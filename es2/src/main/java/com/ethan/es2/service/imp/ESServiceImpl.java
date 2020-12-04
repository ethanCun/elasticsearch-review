package com.ethan.es2.service.imp;

import com.ethan.es2.entity.Content;
import com.ethan.es2.service.ESService;
import com.ethan.es2.utils.HttpParseUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.data.elasticsearch.core.query.HighlightQueryBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class ESServiceImpl implements ESService {

    //获取注入的es高级客户端
    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Override
    public List<Content> getContentList(String keyword) {
        try {
            return HttpParseUtils.parseJD(keyword);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    //将查询出来的数据批量放入es索引中
    @Override
    public Boolean addDataToEs(String keywords) {

        List<Content> contents = this.getContentList(keywords);

        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout("2m");

        ObjectMapper om = new ObjectMapper();

        //注意：id需要写成随机的，不然再次添加时会把之前的数据给覆盖掉，
        //注意：索引不存在的时候，会自动创建索引
        for (int i = 0; i < contents.size(); i++) {

            try {
                bulkRequest.add(
                        new IndexRequest("jd_goods")
                                //.id(""+(i+1))
                                .source(om.writeValueAsString(contents.get(i)),
                                        XContentType.JSON));

            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }


        try {
            BulkResponse bulkResponse =
                    this.restHighLevelClient.bulk(bulkRequest,
                            RequestOptions.DEFAULT);

            //判断是否失败
            return !bulkResponse.hasFailures();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }


    //查找数据
    @Override
    public List<Object> searchKeywords(String keywords, Integer from, Integer size) {

        SearchRequest searchRequest = new SearchRequest("jd_goods");

        //构建查找对象
        SearchSourceBuilder searchSourceBuilder =
                new SearchSourceBuilder();

        //查询条件
        MatchQueryBuilder matchQueryBuilder =
                QueryBuilders.matchQuery("name", keywords);;
        searchSourceBuilder.query(matchQueryBuilder);
        searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

        //分页
        searchSourceBuilder.from(from);
        searchSourceBuilder.size(size);


        //装入请求对象
        searchRequest.source(searchSourceBuilder);

        try {

            SearchResponse search = this.restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

            //查询结果
            ArrayList<Object> contents = new ArrayList<>();

            for (int i = 0; i < search.getHits().getHits().length; i++) {

                contents.add(search.getHits().getHits()[i].getSourceAsMap());
            }

            return contents;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    //高亮搜索
    @Override
    public List<Object> highlightSearchKeywords(String keywords, Integer from, Integer size) {


        SearchRequest searchRequest = new SearchRequest("jd_goods");

        //构建搜索添加
        SearchSourceBuilder searchSourceBuilder =
                new SearchSourceBuilder();

        //分页
        searchSourceBuilder.from(from);
        searchSourceBuilder.size(size);

        //关键字
        MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("name", keywords);
        searchSourceBuilder.query(matchQueryBuilder);

        //高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("name");
        highlightBuilder.preTags("<span style='color:blue; size:30px'>");
        highlightBuilder.postTags("</span>");

        //是否设置多个关键字高亮
        highlightBuilder.requireFieldMatch(false);
        searchSourceBuilder.highlighter(highlightBuilder);


        searchRequest.source(searchSourceBuilder);

        try {
            SearchResponse searchResponse = this.restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

            //搜索结果
            ArrayList<Object> results = new ArrayList<>();

            for (int i = 0; i < searchResponse.getHits().getHits().length; i++) {

                //解析高亮字段
                SearchHit searchHit = searchResponse.getHits().getHits()[i];

                Map<String, HighlightField> highlightFields = searchHit.getHighlightFields();

                //高亮字段
                HighlightField highlightField = highlightFields.get("name");

                //转换为map
                Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();

                if (highlightField != null){

                    //解析字段 替换字段 将原来的字段替换为我们高亮的字段
                    Text[] fragments = highlightField.fragments();

                    String new_name = "";
                    for (Text fragment : fragments) {
                        new_name += fragment;
                    }

                    sourceAsMap.put("name", new_name);
                }

                results.add(sourceAsMap);

            }

            return results;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }


}
