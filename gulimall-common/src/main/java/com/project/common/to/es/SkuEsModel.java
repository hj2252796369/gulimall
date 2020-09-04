package com.project.common.to.es;

import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

/**
 * @program: gulimall
 * @ClassName SkuEsModel
 * @description:
 * @author: hujie
 * @create: 2020-09-04 10:50
 **/
@Data
public class SkuEsModel {

    private Long skuId;
    private Long spuId;
    private String skuTitle;
    private BigDecimal skuPrice;
    private String skuImg;
    private Long saleCount;
    private Long brandId;
    private Long catelogId;


    private Boolean hasStock;
    private Long hotScore;
    private String brandName;
    private String brandImg;

    private String catelogName;

    private List<Attr> attrs;

    @Data
    @ToString
    public static class Attr {
        private Long attrId;
        private String attrName;
        private String attrValue;
    }

}
