package com.project.common.to;

import lombok.Data;

/**
 * @program: gulimall
 * @ClassName SkuHasStockVo
 * @description:
 * @author: hujie
 * @create: 2020-09-04 13:46
 **/
@Data
public class SkuHasStockVo {
    private Long skuId;
    private Boolean hasStock;
}
