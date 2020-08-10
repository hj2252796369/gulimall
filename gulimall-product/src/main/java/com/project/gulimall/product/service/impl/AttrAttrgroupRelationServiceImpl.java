package com.project.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.common.utils.PageUtils;
import com.project.common.utils.Query;
import com.project.gulimall.product.dao.AttrAttrgroupRelationDao;
import com.project.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.project.gulimall.product.service.AttrAttrgroupRelationService;
import com.project.gulimall.product.vo.AttrGroupRelationVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("attrAttrgroupRelationService")
public class AttrAttrgroupRelationServiceImpl extends ServiceImpl<AttrAttrgroupRelationDao, AttrAttrgroupRelationEntity> implements AttrAttrgroupRelationService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrAttrgroupRelationEntity> page = page(
                new Query<AttrAttrgroupRelationEntity>().getPage(params),
                new QueryWrapper<>()
        );

        return new PageUtils(page);
    }

    @Override
    public void deleteBatchRealation(List<AttrAttrgroupRelationEntity> collect) {
        baseMapper.deleteBatchRealation(collect);
    }

    @Override
    public void relationSaveBatch(List<AttrGroupRelationVO> voList) {
        List<AttrAttrgroupRelationEntity> collect = voList.stream().map((entity) -> {
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            BeanUtils.copyProperties(entity, relationEntity);
            return relationEntity;
        }).collect(Collectors.toList());
        saveBatch(collect);
    }

}
