package com.project.gulimall.order.dao;

import com.project.gulimall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author hujie
 * @email 2252796369@qq.com
 * @date 2020-06-09 21:04:53
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}
