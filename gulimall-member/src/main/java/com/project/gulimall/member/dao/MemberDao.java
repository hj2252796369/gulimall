package com.project.gulimall.member.dao;

import com.project.gulimall.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author hujie
 * @email 2252796369@qq.com
 * @date 2020-06-09 21:10:12
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
