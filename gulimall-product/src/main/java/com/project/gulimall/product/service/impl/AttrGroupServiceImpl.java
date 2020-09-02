package com.project.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.common.utils.PageUtils;
import com.project.common.utils.Query;
import com.project.gulimall.product.dao.AttrGroupDao;
import com.project.gulimall.product.entity.AttrEntity;
import com.project.gulimall.product.entity.AttrGroupEntity;
import com.project.gulimall.product.service.AttrAttrgroupRelationService;
import com.project.gulimall.product.service.AttrGroupService;
import com.project.gulimall.product.service.AttrService;
import com.project.gulimall.product.vo.AttrGroupWithAttrVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Autowired
    AttrAttrgroupRelationService attrAttrgroupRelationService;
    @Autowired
    AttrService attrService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<>()
        );

        return new PageUtils(page);
    }


    @Override
    public PageUtils queryPage(Map<String, Object> params, Long catelogId) {
        String key = (String) params.get("key");

        LambdaQueryWrapper<AttrGroupEntity> queryWrapper = new LambdaQueryWrapper<>();

        if (StringUtils.isNotEmpty(key)) {
            queryWrapper.and((obj) -> {
                obj.eq(AttrGroupEntity::getAttrGroupId, key).or().like(AttrGroupEntity::getAttrGroupName, key);
            });
        }

        if (catelogId == 0) {
            IPage<AttrGroupEntity> page = page(
                    new Query<AttrGroupEntity>().getPage(params),
                    queryWrapper
            );
            return new PageUtils(page);
        } else {

            IPage<AttrGroupEntity> page = page(
                    new Query<AttrGroupEntity>().getPage(params),
                    queryWrapper.eq(AttrGroupEntity::getCatelogId, catelogId)
            );
            return new PageUtils(page);
        }


    }


    @Override
    public List<AttrGroupWithAttrVo> attrGroupListWithAttr(String catelogId) {
        List<AttrGroupEntity> attrGroupEntityList = baseMapper.selectList(Wrappers.<AttrGroupEntity>query().lambda().eq(AttrGroupEntity::getCatelogId, catelogId));
        List<AttrGroupWithAttrVo> attrGroupWithAttrVos = attrGroupEntityList.stream().map((entity) -> {
            AttrGroupWithAttrVo vo = new AttrGroupWithAttrVo();
            BeanUtils.copyProperties(entity, vo);
            List<AttrEntity> relationAttr = attrService.getRelationAttr(vo.getAttrGroupId());
            vo.setAttrs(relationAttr);
            return vo;
        }).collect(Collectors.toList());
        return attrGroupWithAttrVos;
    }
}
