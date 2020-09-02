package com.project.gulimall.ware.vo;

import lombok.Data;

import java.util.List;

/**
 * @program: gulimall
 * @ClassName MergeVo
 * @description:
 * @author: hujie
 * @create: 2020-09-02 17:12
 **/
@Data
public class MergeVo {
    /**
     * 整单id
     */
    private Long purchaseId;

    /**
     * 合并项集合
     */
    private List<Long> items;
}
