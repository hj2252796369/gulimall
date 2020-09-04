package com.project.gulimall.search;


import com.alibaba.fastjson.JSON;
import com.project.gulimall.search.config.ElasticSearchConfig;
import lombok.Data;
import lombok.ToString;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.aggregations.metrics.AvgAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
class GulimallSearchApplicationTests {

    @Autowired
    private RestHighLevelClient client;

    @Test
    void contextLoads() {
        System.out.println(client);
    }

    @Test
    public void indexData() throws IOException {
        IndexRequest request = new IndexRequest("users");
        request.id("1");
        User user = new User();
        user.setUserName("Mike");
        user.setGender("M");
        user.setAge(12);

        String jsonString = JSON.toJSONString(user);
        request.source(jsonString, XContentType.JSON);

        IndexResponse indexResponse = client.index(request, RequestOptions.DEFAULT);
        System.out.println(indexResponse.toString());
    }

    //简单的查询操作
    @Test
    public void getData() throws IOException {
        GetRequest getRequest = new GetRequest("bank", "1");
        GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
        System.out.println(getResponse);
    }

    @Test
    public void searchData() throws IOException {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("bank");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchRequest.source(searchSourceBuilder);

        //按照年龄分布进行聚合
        TermsAggregationBuilder ageAgg = AggregationBuilders.terms("ageAgg").field("age").size(100);
        TermsAggregationBuilder genderAgg = AggregationBuilders.terms("genderAgg").field("gender.keyword");
        AvgAggregationBuilder totalAvgAgg = AggregationBuilders.avg("totalAvgAgg").field("balance");

        AvgAggregationBuilder avgAgg = AggregationBuilders.avg("avgAgg").field("balance");
        genderAgg.subAggregation(avgAgg);

        //在年龄分布下进行聚合
        ageAgg.subAggregation(genderAgg);
        ageAgg.subAggregation(totalAvgAgg);

        //将聚合条件加入查询条件当中
        searchSourceBuilder.aggregation(ageAgg);

        System.out.println("检索调价：" + searchSourceBuilder.toString());

        SearchResponse searchResponse = client.search(searchRequest, ElasticSearchConfig.COMMON_OPTIONS);
        System.out.println(searchResponse);

        //获取数据并转换
        SearchHits responseHits = searchResponse.getHits();
        SearchHit[] hits = responseHits.getHits();
        for (SearchHit hit : hits) {
            String sourceAsString = hit.getSourceAsString();
            Account account = JSON.parseObject(sourceAsString, Account.class);
            System.out.println(account);
        }

        //获取聚合数据
        Aggregations aggregations = searchResponse.getAggregations();
        Terms ageAgg1 = aggregations.get("ageAgg");
        for (Terms.Bucket bucket : ageAgg1.getBuckets()) {

            String keyAsString = bucket.getKeyAsString();
            System.out.println("年龄：" + keyAsString);

            Avg totalAvgAgg1 = bucket.getAggregations().get("totalAvgAgg");
            System.out.println("总得薪水比例：" + totalAvgAgg1.getValueAsString());
        }

    }

    //## 查出所有年龄分布，并且这些年龄段中M的平均薪资和F的平均薪资以及这个年龄段的总体平均薪资
    @Test
    public void getAggsData() throws IOException {
        GetRequest getRequest = new GetRequest("bank", "1");
        GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
        System.out.println(getResponse);
    }

    @Data
    @ToString
    static class Account {

        private int account_number;
        private int balance;
        private String firstname;
        private String lastname;
        private int age;
        private String gender;
        private String address;
        private String employer;
        private String email;
        private String city;
        private String state;

    }

    @Data
    class User {
        private String userName;
        private String gender;
        private Integer age;
    }
}
