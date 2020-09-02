package com.project.gulimall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.common.utils.PageUtils;
import com.project.common.utils.Query;
import com.project.gulimall.ware.dao.WareInfoDao;
import com.project.gulimall.ware.entity.WareInfoEntity;
import com.project.gulimall.ware.service.WareInfoService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("wareInfoService")
public class WareInfoServiceImpl extends ServiceImpl<WareInfoDao, WareInfoEntity> implements WareInfoService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {

        LambdaQueryWrapper<WareInfoEntity> queryWrapper = Wrappers.lambdaQuery();

        String key = (String) params.get("key");
        if (!StringUtils.isBlank(key)) {
            queryWrapper.eq(WareInfoEntity::getId, key).or().like(WareInfoEntity::getName, key).or().like(WareInfoEntity::getAddress, key).or().like(WareInfoEntity::getAreacode, key);
        }

        IPage<WareInfoEntity> page = page(
                new Query<WareInfoEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

}
