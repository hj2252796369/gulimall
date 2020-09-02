package com.project.gulimall.ware.vo;

import lombok.Data;

import java.util.List;

/**
 * @program: gulimall
 * @ClassName DonePurchaseVo
 * @description:
 * @author: hujie
 * @create: 2020-09-02 20:59
 **/
@Data
public class DonePurchaseVo {
    private Long id;
    private List<DonePurchaseStatusVo> items;
}
