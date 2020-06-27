package com.project.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.common.utils.PageUtils;
import com.project.common.utils.Query;
import com.project.gulimall.product.dao.AttrGroupDao;
import com.project.gulimall.product.entity.AttrGroupEntity;
import com.project.gulimall.product.service.AttrGroupService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<>()
        );

        return new PageUtils(page);
    }


    @Override
    public PageUtils queryPage(Map<String, Object> params, Long catelogId) {
        if (catelogId == 0) {
            IPage<AttrGroupEntity> page = this.page(
                    new Query<AttrGroupEntity>().getPage(params),
                    new QueryWrapper<>()
            );
            return new PageUtils(page);
        } else {
            String key = (String) params.get("key");

            LambdaQueryWrapper<AttrGroupEntity> queryWrapper = new QueryWrapper<AttrGroupEntity>().lambda().eq(AttrGroupEntity::getCatelogId, catelogId);

            if (StringUtils.isNotEmpty(key)) {
                queryWrapper.and((obj) -> {
                    obj.eq(AttrGroupEntity::getAttrGroupId, key).or().like(AttrGroupEntity::getAttrGroupName, key);
                });
            }

            IPage<AttrGroupEntity> page = this.page(
                    new Query<AttrGroupEntity>().getPage(params),
                    queryWrapper
            );
            return new PageUtils(page);
        }


    }

}