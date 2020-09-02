package com.project.gulimall.ware.vo;

import lombok.Data;

/**
 * @program: gulimall
 * @ClassName DonePurchaseStatusVo
 * @description:
 * @author: hujie
 * @create: 2020-09-02 21:00
 **/
@Data
public class DonePurchaseStatusVo {
    private Long itemId;
    private Integer status;
    private String reason;
}
