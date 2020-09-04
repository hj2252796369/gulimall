package com.project.gulimall.product.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.common.constant.ProductConstant;
import com.project.common.to.SkuHasStockVo;
import com.project.common.to.SkuReductionTo;
import com.project.common.to.SpuBoundTo;
import com.project.common.to.es.SkuEsModel;
import com.project.common.utils.PageUtils;
import com.project.common.utils.Query;
import com.project.common.utils.R;
import com.project.gulimall.product.dao.SpuInfoDao;
import com.project.gulimall.product.entity.*;
import com.project.gulimall.product.feign.CouponFeignService;
import com.project.gulimall.product.feign.SearchFeignService;
import com.project.gulimall.product.feign.WareFeignService;
import com.project.gulimall.product.service.*;
import com.project.gulimall.product.vo.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Autowired
    SpuInfoDescService spuInfoDescService;

    @Autowired
    SpuImagesService spuImagesService;

    @Autowired
    AttrService attrService;

    @Autowired
    ProductAttrValueService productAttrValueService;

    @Autowired
    SkuInfoService skuInfoService;
    @Autowired
    SkuImagesService skuImagesService;

    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    CouponFeignService couponFeignService;
    @Autowired
    WareFeignService wareFeignService;


    @Autowired
    BrandService brandService;
    @Autowired
    CategoryService categoryService;

    @Autowired
    SearchFeignService searchFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<>()
        );

        return new PageUtils(page);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveSpuInfo(SpuSaveVo spuInfoVO) {
        System.out.println(spuInfoVO);
        //1、SPU基本信息 pms_spu_info
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(spuInfoVO, spuInfoEntity);
        spuInfoEntity.setCreateTime(new Date());
        spuInfoEntity.setUpdateTime(new Date());

        save(spuInfoEntity);

        //2、SPU描述图片  pms_spu_info_desc
        List<String> decript = spuInfoVO.getDecript();
        SpuInfoDescEntity spuInfoDescEntity = new SpuInfoDescEntity();
        spuInfoDescEntity.setSpuId(spuInfoEntity.getId());
        spuInfoDescEntity.setDecript(StringUtils.join(decript, ","));
        spuInfoDescService.saveSpuDesc(spuInfoDescEntity);

        //3、保存spu的图片集  pms_spu_images
        List<String> spuInfoVOImages = spuInfoVO.getImages();
        spuImagesService.saveSpuImages(spuInfoEntity.getId(), spuInfoVOImages);

        //4、保存spu规格参数 pms_product_attr_value
        List<BaseAttrs> baseAttrs = spuInfoVO.getBaseAttrs();
        List<ProductAttrValueEntity> productAttrValueEntityList = baseAttrs.stream().map(attr -> {
            ProductAttrValueEntity productAttrValueEntity = new ProductAttrValueEntity();
            productAttrValueEntity.setAttrId(attr.getAttrId());
            AttrRespVO attrInfo = attrService.getAttrInfo(attr.getAttrId());
            productAttrValueEntity.setAttrName(attrInfo.getAttrName());
            productAttrValueEntity.setAttrValue(attr.getAttrValues());
            productAttrValueEntity.setQuickShow(attr.getShowDesc());
            productAttrValueEntity.setSpuId(spuInfoEntity.getId());
            return productAttrValueEntity;
        }).collect(Collectors.toList());
        productAttrValueService.saveBatch(productAttrValueEntityList);

        //5、spu积分信息 gulimall_sms->sms_spu_bounds
        Bounds bounds = spuInfoVO.getBounds();
        SpuBoundTo spuBoundTo = new SpuBoundTo();
        BeanUtils.copyProperties(bounds, spuBoundTo);
        spuBoundTo.setSpuId(spuInfoEntity.getId());
        R spuBoundsResult = couponFeignService.saveSpuBounds(spuBoundTo);
        if (spuBoundsResult.getCode() != 0) {
            log.error("远程保存spu积分信息失败");
        }

        //5、保存当前spu对应的所有sku信息
        List<Skus> skus = spuInfoVO.getSkus();


        if (skus != null && skus.size() > 0) {
            skus.forEach(item -> {

                String defaultImg = "";
                for (Images image : item.getImages()) {
                    if (image.getDefaultImg() == 1) {
                        defaultImg = image.getImgUrl();
                    }
                }
                //a、sku基本信息 pms_sku_info
                SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                BeanUtils.copyProperties(item, skuInfoEntity);
                skuInfoEntity.setBrandId(spuInfoEntity.getBrandId());
                skuInfoEntity.setCatalogId(spuInfoEntity.getCatalogId());
                skuInfoEntity.setSpuId(spuInfoEntity.getId());
                skuInfoEntity.setSaleCount(0L);
                skuInfoEntity.setSkuDefaultImg(defaultImg);
                skuInfoService.save(skuInfoEntity);

                //b、sku图片信息pms_sku_images
                Long skuId = skuInfoEntity.getSkuId();
                List<SkuImagesEntity> skuImagesEntityList = item.getImages().stream().map(img -> {
                    SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                    skuImagesEntity.setSkuId(skuId);
                    skuImagesEntity.setImgUrl(img.getImgUrl());
                    skuImagesEntity.setDefaultImg(img.getDefaultImg());
                    return skuImagesEntity;
                }).filter(entity -> {
                    //返回true就是需要，false就是剔除
                    return !StringUtils.isEmpty(entity.getImgUrl());
                }).collect(Collectors.toList());
                skuImagesService.saveBatch(skuImagesEntityList);
                //TODO 没有图片路径的无需保存

                //c、sku销售属性信息 spm_sku_sale_attr_value
                List<Attr> skuSaleAttrs = item.getAttr();
                List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = skuSaleAttrs.stream().map(saleAttr -> {
                    SkuSaleAttrValueEntity skuSaleAttrValueEntity = new SkuSaleAttrValueEntity();
                    BeanUtils.copyProperties(saleAttr, skuSaleAttrValueEntity);
                    skuSaleAttrValueEntity.setSkuId(skuId);
                    return skuSaleAttrValueEntity;
                }).collect(Collectors.toList());
                skuSaleAttrValueService.saveBatch(skuSaleAttrValueEntities);


                //d、sku优惠、减慢等信息  gulimall_sms->sms_sku_ladder\sms_sku_full_reduction\sms_member_price
                SkuReductionTo skuReductionTo = new SkuReductionTo();
                BeanUtils.copyProperties(item, skuReductionTo);
                skuReductionTo.setSkuId(skuId);
                if (skuReductionTo.getFullCount() > 0 || skuReductionTo.getFullPrice().compareTo(new BigDecimal("0")) == 1) {
                    R r1 = couponFeignService.saveSkuReduction(skuReductionTo);
                    if (r1.getCode() != 0) {
                        log.error("远程保存sku优惠信息失败");
                    }
                }

            });
        }


    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {

        LambdaQueryWrapper<SpuInfoEntity> queryWrapper = new LambdaQueryWrapper<>();

        String key = (String) params.get("key");
        if (!StringUtils.isBlank(key)) {
            queryWrapper.and(w -> {
                w.like(SpuInfoEntity::getSpuName, key).or().eq(SpuInfoEntity::getId, key);
            });
        }

        String status = (String) params.get("status");
        if (!StringUtils.isBlank(status)) {
            queryWrapper.eq(SpuInfoEntity::getPublishStatus, status);
        }

        String catelogId = (String) params.get("catelogId");
        if (!StringUtils.isBlank(catelogId) && !"0".equalsIgnoreCase(catelogId)) {
            queryWrapper.eq(SpuInfoEntity::getCatalogId, catelogId);
        }

        String brandId = (String) params.get("brandId");
        if (!StringUtils.isBlank(brandId) && !"0".equalsIgnoreCase(brandId)) {
            queryWrapper.eq(SpuInfoEntity::getBrandId, brandId);
        }


        IPage<SpuInfoEntity> page = page(
                new Query<SpuInfoEntity>().getPage(params),
                queryWrapper
        );


        return new PageUtils(page);
    }

    /**
     * 整个业务逻辑就是围绕着封装SkuEsModel，并将它保存到ES中进行展开
     * 主要注意点是：
     * （1）Attrs中的所有属性都是可以检索的
     * （2）判断库存充足，需要借助于gulimall-ware来完成，通过openfeign远程调用对应方法
     * （3）将封装结果保存到ES中，即便存在重复调用的情况，也不会造成数据的重复插入，
     * 因为ES会比较插入文档的ID，相同则执行的是更新操作
     *
     * @param spuId
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void spuUp(Long spuId) {

        //查询快速展示的属性
        List<ProductAttrValueEntity> productAttrValueEntityList = productAttrValueService.baseAttrlistforspu(spuId);
        List<Long> attrIds = productAttrValueEntityList.stream().map(item -> {
            return item.getAttrId();
        }).collect(Collectors.toList());
        //获取支持检索的属性的属性ID
        List<Long> searchAttrIds = attrService.selectSearchAttrIds(attrIds);
        HashSet<Long> searchAttrIdsSet = new HashSet<>(searchAttrIds);
        //取得支持检索的的Attrs，用来封装SkuEsModel的attrs属性
        List<SkuEsModel.Attr> attrList = productAttrValueEntityList.stream().filter(item -> {
            return searchAttrIdsSet.contains(item.getAttrId());
        }).map(item -> {
            SkuEsModel.Attr attr = new SkuEsModel.Attr();
            attr.setAttrId(item.getAttrId());
            attr.setAttrName(item.getAttrName());
            attr.setAttrValue(item.getAttrValue());
            return attr;
        }).collect(Collectors.toList());


        //查询spuId对应的SKU信息
        List<SkuInfoEntity> skuLists = skuInfoService.getSkusBySpuId(spuId);

        //取得sku所对应的库存信息，即是否还有库存，为封装SkuEsModel的HasStock属性服务
        Map<Long, Boolean> stockMap = null;

        try {
            List<Long> skuIds = skuLists.stream().map(SkuInfoEntity::getSkuId).collect(Collectors.toList());
            R<List<SkuHasStockVo>> skuHasStock = wareFeignService.getSkuHasStock(skuIds);
            stockMap = skuHasStock.getData(new TypeReference<List<SkuHasStockVo>>() {
            }).stream().collect(Collectors.toMap(SkuHasStockVo::getSkuId, SkuHasStockVo::getHasStock));

        } catch (Exception e) {
            log.error("库存服务查询异常：原因{}", e);
        }

        //封装每个SKU的信息
        Map<Long, Boolean> finalStockMap = stockMap;

        //需要上架的数据
        List<SkuEsModel> upProductEsData = skuLists.stream().map(entity -> {
            //填装数据
            SkuEsModel skuEsModel = new SkuEsModel();
            //填装sku信息
            BeanUtils.copyProperties(entity, skuEsModel);
            skuEsModel.setSkuPrice(entity.getPrice());
            skuEsModel.setSkuImg(entity.getSkuDefaultImg());

            //填装库存信息（发送远程请求获取库存数量）
            if (finalStockMap == null) {
                skuEsModel.setHasStock(true);
            } else {
                skuEsModel.setHasStock(finalStockMap.get(entity.getSkuId()));
            }

            //热度评分  默认为0
            skuEsModel.setHotScore(0L);

            //填充品牌和分类信息
            BrandEntity resultBrand = brandService.getById(skuEsModel.getBrandId());
            skuEsModel.setBrandImg(resultBrand.getLogo());
            skuEsModel.setBrandName(resultBrand.getName());

            skuEsModel.setCatelogId(entity.getCatalogId());
            CategoryEntity resultCategory = categoryService.getById(skuEsModel.getCatelogId());
            skuEsModel.setCatelogName(resultCategory.getName());

            skuEsModel.setAttrs(attrList);

            return skuEsModel;
        }).collect(Collectors.toList());


        //将数据发送给ElasticSearch
        R statusUp = searchFeignService.productStatusUp(upProductEsData);
        if (statusUp.getCode() == 0) {
            //远程调用成功，修改status状态
            baseMapper.updateSpuStatus(spuId, ProductConstant.StatusEnum.SPU_UP.getCode());
        } else {
            //远程调用失败
            //TODO 7.重复调用的问题，接口幂等性

        }

    }

}
