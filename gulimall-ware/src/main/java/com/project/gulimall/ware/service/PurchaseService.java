package com.project.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.project.common.utils.PageUtils;
import com.project.gulimall.ware.entity.PurchaseEntity;
import com.project.gulimall.ware.vo.DonePurchaseVo;
import com.project.gulimall.ware.vo.MergeVo;

import java.util.List;
import java.util.Map;

/**
 * 采购信息
 *
 * @author hujie
 * @email 2252796369@qq.com
 * @date 2020-06-09 21:22:21
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void mergePurchase(MergeVo mergeVo);

    PageUtils unreceivelist(Map<String, Object> params);

    void receivedPurchase(List<Long> ids);

    void donePurchase(DonePurchaseVo donePurchaseVo);
}

