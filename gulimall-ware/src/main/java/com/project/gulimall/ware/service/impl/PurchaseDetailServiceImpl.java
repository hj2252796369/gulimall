package com.project.gulimall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.common.utils.PageUtils;
import com.project.common.utils.Query;
import com.project.gulimall.ware.dao.PurchaseDetailDao;
import com.project.gulimall.ware.entity.PurchaseDetailEntity;
import com.project.gulimall.ware.service.PurchaseDetailService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service("purchaseDetailService")
public class PurchaseDetailServiceImpl extends ServiceImpl<PurchaseDetailDao, PurchaseDetailEntity> implements PurchaseDetailService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseDetailEntity> page = page(
                new Query<PurchaseDetailEntity>().getPage(params),
                new QueryWrapper<>()
        );

        return new PageUtils(page);
    }


    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {

        LambdaQueryWrapper<PurchaseDetailEntity> queryWrapper = Wrappers.lambdaQuery();

        String key = (String) params.get("key");
        if (!StringUtils.isBlank(key)) {
            queryWrapper.and(w -> {
                w.eq(PurchaseDetailEntity::getSkuId, key)
                        .or().eq(PurchaseDetailEntity::getSkuNum, key);
            });
        }

        String status = (String) params.get("status");
        if (!StringUtils.isBlank(status)) {
            queryWrapper.eq(PurchaseDetailEntity::getStatus, status);
        }

        String wareId = (String) params.get("wareId");
        if (!StringUtils.isBlank(wareId)) {
            queryWrapper.eq(PurchaseDetailEntity::getWareId, wareId);
        }

        IPage<PurchaseDetailEntity> page = page(
                new Query<PurchaseDetailEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);

    }

    @Override
    public List<PurchaseDetailEntity> listDetailByPurchaseId(Long purchaseId) {
        return list(Wrappers.<PurchaseDetailEntity>lambdaQuery().eq(PurchaseDetailEntity::getPurchaseId, purchaseId));
    }

}
