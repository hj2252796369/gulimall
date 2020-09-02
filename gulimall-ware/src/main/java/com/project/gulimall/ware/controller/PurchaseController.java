package com.project.gulimall.ware.controller;

import com.project.common.utils.PageUtils;
import com.project.common.utils.R;
import com.project.gulimall.ware.entity.PurchaseEntity;
import com.project.gulimall.ware.service.PurchaseService;
import com.project.gulimall.ware.vo.DonePurchaseVo;
import com.project.gulimall.ware.vo.MergeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * 采购信息
 *
 * @author hujie
 * @email 2252796369@qq.com
 * @date 2020-06-09 21:22:21
 */
@RestController
@RequestMapping("ware/purchase")
public class PurchaseController {
    @Autowired
    private PurchaseService purchaseService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = purchaseService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
        PurchaseEntity purchase = purchaseService.getById(id);

        return R.ok().put("purchase", purchase);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody PurchaseEntity purchase) {
        purchase.setCreateTime(new Date());
        purchase.setUpdateTime(new Date());
        purchaseService.save(purchase);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody PurchaseEntity purchase) {
        purchaseService.updateById(purchase);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
        purchaseService.removeByIds(Arrays.asList(ids));
        return R.ok();
    }

    /**
     * /ware/purchase/merge
     * 合并采购需求
     */
    @PostMapping("/merge")
    public R mergePurchase(@RequestBody MergeVo mergeVo) {
        purchaseService.mergePurchase(mergeVo);
        return R.ok();
    }


    @RequestMapping("/unreceive/list")
    public R unreceivelist(@RequestParam Map<String, Object> params) {
        PageUtils page = purchaseService.unreceivelist(params);
        return R.ok().put("page", page);
    }

    /**
     * 领取采购单
     * /ware/purchase/received
     */
    @RequestMapping("/received")
    public R receivedPurchase(@RequestBody List<Long> ids) {
        purchaseService.receivedPurchase(ids);
        return R.ok();
    }

    /**
     * 完成采购
     * /ware/purchase/done
     */
    @RequestMapping("/done")
    public R donePurchase(@RequestBody DonePurchaseVo donePurchaseVo) {
        purchaseService.donePurchase(donePurchaseVo);
        return R.ok();
    }
}
