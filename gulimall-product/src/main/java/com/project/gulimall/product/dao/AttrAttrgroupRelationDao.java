package com.project.gulimall.product.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.project.gulimall.product.entity.AttrAttrgroupRelationEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 属性&属性分组关联
 *
 * @author hujie
 * @email 2252796369@qq.com
 * @date 2020-06-09 22:16:32
 */
@Mapper
public interface AttrAttrgroupRelationDao extends BaseMapper<AttrAttrgroupRelationEntity> {

    void deleteBatchRealation(@Param("entities") List<AttrAttrgroupRelationEntity> entities);
}
