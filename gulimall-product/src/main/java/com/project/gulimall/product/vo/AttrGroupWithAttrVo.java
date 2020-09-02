package com.project.gulimall.product.vo;

import com.project.gulimall.product.entity.AttrEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @program: gulimall
 * @ClassName WithAttrVo
 * @description:
 * @author: hujie
 * @create: 2020-08-13 22:02
 **/
@Data
public class AttrGroupWithAttrVo implements Serializable {

    private static final long serialVersionUID = 5313431645947962667L;
    private Long attrGroupId;
    /**
     * 组名
     */
    private String attrGroupName;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 描述
     */
    private String descript;
    /**
     * 组图标
     */
    private String icon;
    /**
     * 所属分类id
     */
    private Long catelogId;

    private List<AttrEntity> attrs;

}
