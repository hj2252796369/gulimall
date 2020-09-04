package com.project.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.project.common.utils.PageUtils;
import com.project.gulimall.product.entity.AttrEntity;
import com.project.gulimall.product.vo.AttrGroupRelationVO;
import com.project.gulimall.product.vo.AttrRespVO;
import com.project.gulimall.product.vo.AttrVO;

import java.util.List;
import java.util.Map;

/**
 * 商品属性
 *
 * @author hujie
 * @email 2252796369@qq.com
 * @date 2020-06-09 22:16:32
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveAttr(AttrVO attrVO);

    PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId, String type);

    AttrRespVO getAttrInfo(Long attrId);

    void updateAttrInfo(AttrVO attrVO);

    List<AttrEntity> getRelationAttr(Long attrgroupId);

    void relationDelete(AttrGroupRelationVO[] attrGroupRelationVOS);

    PageUtils getNoAttrRelation(Map<String, Object> params, Long attrgroupId);

    List<Long> selectSearchAttrIds(List<Long> attrIds);
}

