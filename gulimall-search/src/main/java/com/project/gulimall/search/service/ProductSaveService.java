package com.project.gulimall.search.service;

import com.project.common.to.es.SkuEsModel;

import java.io.IOException;
import java.util.List;

/**
 * @author hujie
 */
public interface ProductSaveService {
    Boolean productStatusUp(List<SkuEsModel> skuEsModelList) throws IOException;
}
