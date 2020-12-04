package com.ethan.es1;

import com.ethan.es1.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

@SpringBootTest
public class documentsTest {

    @Autowired
    @Qualifier("client")
    private RestHighLevelClient client;

    //创建文档
    @Test
    void testCreateDocument() throws IOException {

        /**
         * PUT /czy/_doc/
         * {
         *  "id":"1",
         *  "name":"czy",
         *  "age":"20"
         * }
         *
         */
        //创建对象
        User user = new User(2, "lisi", "30");

        //创建请求
        IndexRequest indexRequest = new IndexRequest("czy");

        //设置规则 文档id
        indexRequest.id("1");
        //超时时间
        indexRequest.timeout(TimeValue.timeValueSeconds(5));

        ObjectMapper om = new ObjectMapper();
        String userJson = om.writeValueAsString(user);

        //将数据放入请求, 需要转化成json字符串
        indexRequest.source(userJson, XContentType.JSON);

        //客户端发送请求
        IndexResponse indexResponse =
                client.index(indexRequest, RequestOptions.DEFAULT);

        //打印请求
        System.out.println(indexResponse);

    }


    //是否存在文档
    @Test
    void testIsExists() throws IOException {

        // GET /czy/_doc/1
        GetRequest getRequest = new GetRequest("czy", "1");

        //过滤请求: 不过滤返回的_source的上下文
        getRequest.fetchSourceContext(new FetchSourceContext(false));

        getRequest.storedFields("_none_");

        boolean exsit =
                client.exists(getRequest, RequestOptions.DEFAULT);

        //是否存在这条数据
        System.out.println(exsit);
    }

    //查找文档
    @Test
    void testGetDocument() throws IOException {

        GetRequest getRequest = new GetRequest("czy", "1");

        GetResponse getResponse =
                client.get(getRequest, RequestOptions.DEFAULT);

        System.out.println(getResponse.getSourceAsString());

    }


    //修改文档
    @Test
    void testUpdateDocument() throws IOException {

        UpdateRequest updateRequest =
                new UpdateRequest("czy", "1");
        updateRequest.timeout("1s");

        User user = new User(3, "wangwu", "50");

        ObjectMapper om = new ObjectMapper();

        updateRequest.doc(om.writeValueAsString(user), XContentType.JSON);

        UpdateResponse updateResponse =
                client.update(updateRequest, RequestOptions.DEFAULT);

        System.out.println(updateResponse);
    }

    //删除文档
    @Test
    void testDeleteDocument() throws IOException {

        DeleteRequest deleteRequest =
                new DeleteRequest("czy", "1");
        //注意带上单位
        deleteRequest.timeout("1s");

        DeleteResponse deleteResponse =
                client.delete(deleteRequest, RequestOptions.DEFAULT);

        System.out.println(deleteResponse);
    }


    //批量插入文档
    @Test
    void testBatchInsert() throws IOException {

        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout("10s");

        ArrayList<User> users = new ArrayList<>();
        users.add(new User("zhangsan", "20"));
        users.add(new User("lisi", "30"));
        users.add(new User("wangwu", "40"));
        users.add(new User("zhaoliu", "50"));

        ObjectMapper om = new ObjectMapper();

        for (int i = 0; i < users.size(); i++) {

            users.get(i).setId(i);

            String userStr = om.writeValueAsString(users.get(i));

            //构建批请求
            bulkRequest.add(new IndexRequest("czy")
            .id(""+(i+1))
            .source(userStr, XContentType.JSON));
        }

        //处理批请求
        BulkResponse bulkResponse =
                client.bulk(bulkRequest, RequestOptions.DEFAULT);

        System.out.println(bulkResponse);

    }

    //查找
    /**
     *         //高亮构建对象
     *         HighlightBuilder highlightBuilder =
     *                 new HighlightBuilder();
     *
     *        //精准查询构建对象
     *         TermQueryBuilder termQueryBuilder =
     *                 QueryBuilders.termQuery("name", "lisi");
     *
     *        //匹配所有
     *          MatchAllQueryBuilder matchAllQueryBuilder = new MatchAllQueryBuilder();
     *
     *
     *
     */
    @Test
    void testSearch() throws IOException {

        SearchRequest searchRequest = new SearchRequest("czy");

        //构建搜索条件
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //searchSourceBuilder.sort("name");

        //精准查询构建对象
        TermQueryBuilder termQueryBuilder =
                QueryBuilders.termQuery("name", "lisi");
        searchSourceBuilder.query(termQueryBuilder);

        //设置查询时间
        searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

        //分页
        searchSourceBuilder.from(0);
        searchSourceBuilder.size(100);

        //配置搜索条件
        searchRequest.source(searchSourceBuilder);

        //发起搜索请求
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        ObjectMapper om = new ObjectMapper();

        System.out.println(om.writeValueAsString(searchResponse.getHits()));

        for (SearchHit searchHit : searchResponse.getHits().getHits()){

            System.out.println(searchHit);
        }
    }


}
