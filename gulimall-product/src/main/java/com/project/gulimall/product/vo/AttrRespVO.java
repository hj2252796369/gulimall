package com.project.gulimall.product.vo;

import lombok.Data;

/**
 * @program: gulimall
 * @ClassName AttrRespVO
 * @description:
 * @author: hujie
 * @create: 2020-07-28 19:26
 **/
@Data
public class AttrRespVO extends AttrVO {
    /**
     * "catelogName": "手机/数码/手机", //所属分类名字
     * "groupName": "主体", //所属分组名字
     */

    private String groupName;
    private Long attrGroupId;

    private Long[] catelogPath;
    private String catelogName;
}
