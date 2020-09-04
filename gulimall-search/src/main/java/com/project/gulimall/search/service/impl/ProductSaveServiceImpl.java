package com.project.gulimall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.project.common.to.es.SkuEsModel;
import com.project.gulimall.search.config.ElasticSearchConfig;
import com.project.gulimall.search.constant.ESConstant;
import com.project.gulimall.search.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @program: gulimall
 * @ClassName ProductSaveService
 * @description:
 * @author: hujie
 * @create: 2020-09-04 14:30
 **/
@Slf4j
@Service
public class ProductSaveServiceImpl implements ProductSaveService {

    @Autowired
    RestHighLevelClient restHighLevelClient;

    @Override
    public Boolean productStatusUp(List<SkuEsModel> skuEsModelList) throws IOException {

        //BulkRequest bulkRequest, RequestOptions options
        BulkRequest bulkRequest = new BulkRequest();

        skuEsModelList.forEach(item -> {
            IndexRequest indexRequest = new IndexRequest(ESConstant.PRODUCT_INDEX);
            indexRequest.id(item.getSkuId().toString());
            String dataJsonString = JSON.toJSONString(item);
            indexRequest.source(dataJsonString, XContentType.JSON);
            bulkRequest.add(indexRequest);
        });

        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, ElasticSearchConfig.COMMON_OPTIONS);
        boolean hasFailures = bulk.hasFailures();
        List<String> collect = Arrays.asList(bulk.getItems()).stream().map(item -> {
            return item.getId();
        }).collect(Collectors.toList());

        log.info("商品上架完成：{}", collect);


        return hasFailures;
    }
}
