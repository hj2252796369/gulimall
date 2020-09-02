package com.project.gulimall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.project.common.constant.WareConstant;
import com.project.common.utils.PageUtils;
import com.project.common.utils.Query;
import com.project.gulimall.ware.dao.PurchaseDao;
import com.project.gulimall.ware.entity.PurchaseDetailEntity;
import com.project.gulimall.ware.entity.PurchaseEntity;
import com.project.gulimall.ware.service.PurchaseDetailService;
import com.project.gulimall.ware.service.PurchaseService;
import com.project.gulimall.ware.service.WareSkuService;
import com.project.gulimall.ware.vo.DonePurchaseStatusVo;
import com.project.gulimall.ware.vo.DonePurchaseVo;
import com.project.gulimall.ware.vo.MergeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {


    @Autowired
    PurchaseDetailService purchaseDetailService;
    @Autowired
    WareSkuService wareSkuService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<>()
        );

        return new PageUtils(page);
    }

    @Override
    public void mergePurchase(MergeVo mergeVo) {

        Long purchaseId = mergeVo.getPurchaseId();
        if (purchaseId == null) {
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            purchaseEntity.setCreateTime(new Date());
            purchaseEntity.setUpdateTime(new Date());
            purchaseEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.CREATED.getCode());

            save(purchaseEntity);
            purchaseId = purchaseEntity.getId();
        }

        List<Long> items = mergeVo.getItems();
        Long finalPurchaseId = purchaseId;
        List<PurchaseDetailEntity> purchaseDetailEntities = items.stream().map(i -> {
            PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
            purchaseDetailEntity.setId(i);
            purchaseDetailEntity.setPurchaseId(finalPurchaseId);
            purchaseDetailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.ASSIGNED.getCode());
            return purchaseDetailEntity;

        }).collect(Collectors.toList());
        purchaseDetailService.updateBatchById(purchaseDetailEntities);

        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setId(purchaseId);
        purchaseEntity.setUpdateTime(new Date());
        updateById(purchaseEntity);
    }

    @Override
    public PageUtils unreceivelist(Map<String, Object> params) {
        IPage<PurchaseEntity> page = page(
                new Query<PurchaseEntity>().getPage(params),
                Wrappers.<PurchaseEntity>lambdaQuery().eq(PurchaseEntity::getStatus, 0).or().eq(PurchaseEntity::getStatus, 1)
        );
        return new PageUtils(page);
    }

    @Override
    public void receivedPurchase(List<Long> ids) {
        //1、确认当前采购单是新建或者已分配的状态

        List<PurchaseEntity> purchaseEntities = ids.stream().map(id -> {
            PurchaseEntity result = getById(id);
            return result;
        }).filter(entity -> {
            if (entity.getStatus() == WareConstant.PurchaseStatusEnum.CREATED.getCode() || entity.getStatus() == WareConstant.PurchaseStatusEnum.ASSIGNED.getCode()) {
                return true;
            }
            return false;
        }).map(item -> {
            item.setStatus(WareConstant.PurchaseStatusEnum.RECEIVE.getCode());
            item.setUpdateTime(new Date());
            return item;
        }).collect(Collectors.toList());

        //更改采购单的状态
        updateBatchById(purchaseEntities);

        //更改采购项的状态
        purchaseEntities.forEach(item -> {
            List<PurchaseDetailEntity> purchaseDetailEntities = purchaseDetailService.listDetailByPurchaseId(item.getId());
            List<PurchaseDetailEntity> collect = purchaseDetailEntities.stream().map(entity -> {
                PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
                purchaseDetailEntity.setId(entity.getId());
                purchaseDetailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.BUYING.getCode());
                return purchaseDetailEntity;
            }).collect(Collectors.toList());
            purchaseDetailService.updateBatchById(collect);
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void donePurchase(DonePurchaseVo donePurchaseVo) {

        //1、更改采购单的状态
        Long purchaseId = donePurchaseVo.getId();


        //2、更改采购项的状态
        Boolean flag = true;
        List<DonePurchaseStatusVo> items = donePurchaseVo.getItems();

        List<PurchaseDetailEntity> updateEntity = Lists.newArrayList();
        for (DonePurchaseStatusVo item : items) {
            PurchaseDetailEntity detailEntity = new PurchaseDetailEntity();
            if (item.getStatus() == WareConstant.PurchaseDetailStatusEnum.HASERROR.getCode()) {
                flag = false;
                detailEntity.setStatus(item.getStatus());
            } else {
                detailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.FINISH.getCode());
                //3、更改已采购商品的库存
                PurchaseDetailEntity result = purchaseDetailService.getById(item.getItemId());
                wareSkuService.addStock(result.getSkuId(), result.getWareId(), result.getSkuNum());
            }
            detailEntity.setId(item.getItemId());
            updateEntity.add(detailEntity);
        }
        purchaseDetailService.updateBatchById(updateEntity);
//1、改变采购单状态
        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setId(purchaseId);
        purchaseEntity.setStatus(flag ? WareConstant.PurchaseStatusEnum.FINISH.getCode() : WareConstant.PurchaseStatusEnum.HASERROR.getCode());
        purchaseEntity.setUpdateTime(new Date());
        updateById(purchaseEntity);

    }

}
