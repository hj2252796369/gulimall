package com.project.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.project.common.utils.PageUtils;
import com.project.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.project.gulimall.product.vo.AttrGroupRelationVO;

import java.util.List;
import java.util.Map;

/**
 * 属性&属性分组关联
 *
 * @author hujie
 * @email 2252796369@qq.com
 * @date 2020-06-09 22:16:32
 */
public interface AttrAttrgroupRelationService extends IService<AttrAttrgroupRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void deleteBatchRealation(List<AttrAttrgroupRelationEntity> collect);

    void relationSaveBatch(List<AttrGroupRelationVO> voList);
}

