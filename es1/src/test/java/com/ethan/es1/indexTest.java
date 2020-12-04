package com.ethan.es1;

import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
public class indexTest {

    @Autowired
    @Qualifier("client")
    private RestHighLevelClient client;

    @Test
    void contextLoads() {

    }

    //创建索引的请求
    @Test
    void testCreateIndex() throws IOException {

        //创建索引请求
        CreateIndexRequest createIndexRequest =
                new CreateIndexRequest("czy");

        //执行请求
        CreateIndexResponse createIndexResponse =
                client.indices().create(createIndexRequest,
                        RequestOptions.DEFAULT);

        //打印响应结果
        System.out.println(createIndexResponse);

    }

    //获取索引
    @Test
    void testGetIndex() throws IOException {

        GetIndexRequest getIndexRequest = new GetIndexRequest("czy");

        //判断索引是否存在
        if (client.indices().exists(getIndexRequest, RequestOptions.DEFAULT)) {

            System.out.println("存在");

        }
    }

    //删除索引
    @Test
    void testDeleteIndex() throws IOException{

        DeleteIndexRequest deleteIndexRequest =
                new DeleteIndexRequest("czy");

        AcknowledgedResponse acknowledgedResponse =
                client.indices().delete(deleteIndexRequest, RequestOptions.DEFAULT);

        System.out.println(acknowledgedResponse.isAcknowledged());

    }

}
