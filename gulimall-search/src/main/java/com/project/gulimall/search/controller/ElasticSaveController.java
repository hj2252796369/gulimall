package com.project.gulimall.search.controller;

import com.project.common.exception.BizCodeEnume;
import com.project.common.to.es.SkuEsModel;
import com.project.common.utils.R;
import com.project.gulimall.search.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

/**
 * @program: gulimall
 * @ClassName ElasticSaveController
 * @description:
 * @author: hujie
 * @create: 2020-09-04 14:19
 **/
@Slf4j
@RestController
@RequestMapping("/search/save")
public class ElasticSaveController {


    @Autowired
    ProductSaveService productSaveService;

    @PostMapping("/product")
    public R productStatusUp(@RequestBody List<SkuEsModel> skuEsModelList) throws IOException {
        boolean status = false;
        try {
            status = productSaveService.productStatusUp(skuEsModelList);
        } catch (IOException e) {
            log.error("商品上架错误{}", e);
            return R.error(BizCodeEnume.PRODUCT_EXCEPTION.getCode(), BizCodeEnume.PRODUCT_EXCEPTION.getMsg());
        }

        if (status) {
            return R.error(BizCodeEnume.PRODUCT_EXCEPTION.getCode(), BizCodeEnume.PRODUCT_EXCEPTION.getMsg());
        } else {
            return R.ok();
        }
    }

}
