package com.project.gulimall.product.dao;

import com.project.gulimall.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 * 
 * @author hujie
 * @email 2252796369@qq.com
 * @date 2020-06-09 22:16:32
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
}
