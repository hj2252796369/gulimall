package com.project.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.common.constant.AttributeTypeConstant;
import com.project.common.utils.PageUtils;
import com.project.common.utils.Query;
import com.project.gulimall.product.dao.AttrDao;
import com.project.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.project.gulimall.product.entity.AttrEntity;
import com.project.gulimall.product.entity.AttrGroupEntity;
import com.project.gulimall.product.entity.CategoryEntity;
import com.project.gulimall.product.service.AttrAttrgroupRelationService;
import com.project.gulimall.product.service.AttrGroupService;
import com.project.gulimall.product.service.AttrService;
import com.project.gulimall.product.service.CategoryService;
import com.project.gulimall.product.vo.AttrGroupRelationVO;
import com.project.gulimall.product.vo.AttrRespVO;
import com.project.gulimall.product.vo.AttrVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Autowired
    AttrAttrgroupRelationService attrAttrgroupRelationService;
    @Autowired
    AttrGroupService attrGroupService;
    @Autowired
    CategoryService categoryService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<>()
        );

        return new PageUtils(page);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveAttr(AttrVO attrVO) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attrVO, attrEntity);
        super.save(attrEntity);

        if (attrVO.getAttrType().equals(AttributeTypeConstant.BASE_VALUE) && attrVO.getAttrGroupId() != null) {
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
            attrAttrgroupRelationEntity.setAttrGroupId(attrVO.getAttrGroupId());
            attrAttrgroupRelationEntity.setAttrId(attrEntity.getAttrId());
            attrAttrgroupRelationService.save(attrAttrgroupRelationEntity);
        }


    }

    @Override
    public PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId, String type) {

        QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<>();


        if (StringUtils.equalsIgnoreCase(type, AttributeTypeConstant.BASE_LABEL)) {
            queryWrapper.lambda().eq(AttrEntity::getAttrType, AttributeTypeConstant.BASE_VALUE);
        } else if (StringUtils.equalsIgnoreCase(type, AttributeTypeConstant.SALE_LABEL)) {
            queryWrapper.lambda().eq(AttrEntity::getAttrType, AttributeTypeConstant.SALE_VALUE);
        }

        if (catelogId != 0) {
            queryWrapper.lambda().eq(AttrEntity::getCatelogId, catelogId);
        }

        String key = (String) params.get("key");
        if (!StringUtils.isBlank(key)) {
            queryWrapper.lambda().and((wrapper) -> {
                wrapper.eq(AttrEntity::getAttrId, key).or().like(AttrEntity::getAttrName, key);
            });
        }

        IPage<AttrEntity> page = page(
                new Query<AttrEntity>().getPage(params),
                queryWrapper
        );
        PageUtils pageUtils = new PageUtils(page);
        List<AttrEntity> attrEntityList = page.getRecords();
        List<AttrRespVO> attrRespVOS = attrEntityList.stream().map((attrEntity) -> {
            AttrRespVO attrRespVO = new AttrRespVO();
            BeanUtils.copyProperties(attrEntity, attrRespVO);

            if (StringUtils.equalsIgnoreCase(type, AttributeTypeConstant.BASE_LABEL)) {
                AttrAttrgroupRelationEntity relationEntity = attrAttrgroupRelationService.getOne(new QueryWrapper<AttrAttrgroupRelationEntity>().lambda().eq(AttrAttrgroupRelationEntity::getAttrId, attrEntity.getAttrId()));
                if (relationEntity != null && relationEntity.getAttrGroupId() != null) {
                    AttrGroupEntity attrGroupEntity = attrGroupService.getById(relationEntity.getAttrGroupId());
                    attrRespVO.setGroupName(attrGroupEntity.getAttrGroupName());
                }

            }

            CategoryEntity categoryEntity = categoryService.getById(attrEntity.getCatelogId());
            if (categoryEntity != null) {
                attrRespVO.setCatelogName(categoryEntity.getName());
            }

            return attrRespVO;
        }).collect(Collectors.toList());
        pageUtils.setList(attrRespVOS);
        return pageUtils;
    }

    @Override
    public AttrRespVO getAttrInfo(Long attrId) {
        AttrRespVO attrRespVO = new AttrRespVO();

        AttrEntity attr = super.getById(attrId);
        Long[] catelogPath = categoryService.findCatelogPath(attr.getCatelogId());
        BeanUtils.copyProperties(attr, attrRespVO);

        attrRespVO.setCatelogPath(catelogPath);
        attrRespVO.setCatelogName(categoryService.getById(attr.getCatelogId()).getName());

        AttrAttrgroupRelationEntity relationEntity = attrAttrgroupRelationService.getOne(new QueryWrapper<AttrAttrgroupRelationEntity>().lambda().eq(AttrAttrgroupRelationEntity::getAttrId, attr.getAttrId()));
        AttrGroupEntity attrGroupEntity = attrGroupService.getById(relationEntity.getAttrGroupId());

        attrRespVO.setGroupName(attrGroupEntity.getAttrGroupName());
        attrRespVO.setAttrGroupId(attrGroupEntity.getAttrGroupId());

        return attrRespVO;
    }

    @Override
    public void updateAttrInfo(AttrVO attrVO) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attrVO, attrEntity);
        super.updateById(attrEntity);

        AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
        attrAttrgroupRelationEntity.setAttrGroupId(attrVO.getAttrGroupId());
        attrAttrgroupRelationEntity.setAttrId(attrEntity.getAttrId());
        attrAttrgroupRelationService.update(attrAttrgroupRelationEntity, new QueryWrapper<AttrAttrgroupRelationEntity>().lambda().eq(AttrAttrgroupRelationEntity::getAttrId, attrEntity.getAttrId()));
    }

    /**
     * 根据分组ID查询关联的所有基本属性
     *
     * @param attrgroupId
     * @return
     */
    @Override
    public List<AttrEntity> getRelationAttr(Long attrgroupId) {
        List<AttrAttrgroupRelationEntity> entityList = attrAttrgroupRelationService.list(Wrappers.<AttrAttrgroupRelationEntity>query().lambda().eq(AttrAttrgroupRelationEntity::getAttrGroupId, attrgroupId));
        List<Long> attrIds = entityList.stream().map((entity) -> {
            return entity.getAttrId();
        }).collect(Collectors.toList());
        if (attrIds == null || attrIds.size() == 0) {
            return null;
        }

        return (List<AttrEntity>) listByIds(attrIds);
    }

    /**
     * 获取属性分组里面还没有关联的本分类里面的其他基本属性，方便添加新的关联
     *
     * @param attrGroupRelationVOS
     */
    @Override
    public void relationDelete(AttrGroupRelationVO[] attrGroupRelationVOS) {
//        boolean remove = attrAttrgroupRelationService.remove(Wrappers.<AttrAttrgroupRelationEntity>query().lambda().eq(AttrAttrgroupRelationEntity::getAttrGroupId, attrGroupRelationVO.getAttrGroupId()).eq(AttrAttrgroupRelationEntity::getAttrId, attrGroupRelationVO.getAttrId()));
        List<AttrAttrgroupRelationEntity> collect = Arrays.asList(attrGroupRelationVOS).stream().map((item) -> {
            AttrAttrgroupRelationEntity realationEntity = new AttrAttrgroupRelationEntity();
            BeanUtils.copyProperties(item, realationEntity);
            return realationEntity;
        }).collect(Collectors.toList());
        attrAttrgroupRelationService.deleteBatchRealation(collect);
    }

    @Override
    public PageUtils getNoAttrRelation(Map<String, Object> params, Long attrgroupId) {
        AttrGroupEntity attrGroupEntity = attrGroupService.getById(attrgroupId);
        Long catelogId = attrGroupEntity.getCatelogId();

        List<AttrGroupEntity> attrGroupEntities = attrGroupService.list(Wrappers.<AttrGroupEntity>query().lambda().eq(AttrGroupEntity::getCatelogId, catelogId));

        List<Long> noAttrGroupIds = attrGroupEntities.stream().map((entity) -> {
            return entity.getAttrGroupId();
        }).collect(Collectors.toList());

        List<AttrAttrgroupRelationEntity> relationEntities = attrAttrgroupRelationService.list(Wrappers.<AttrAttrgroupRelationEntity>query().lambda().in(AttrAttrgroupRelationEntity::getAttrGroupId, noAttrGroupIds));
        List<Long> attrIds = relationEntities.stream().map((relation -> {
            return relation.getAttrId();
        })).collect(Collectors.toList());

        LambdaQueryWrapper<AttrEntity> queryWrapper = Wrappers.<AttrEntity>query().lambda().eq(AttrEntity::getCatelogId, catelogId).eq(AttrEntity::getAttrType, AttributeTypeConstant.BASE_VALUE);
        if (attrIds != null && attrIds.size() > 0) {
            queryWrapper.notIn(AttrEntity::getAttrId, attrIds);
        }


        String key = (String) params.get("key");
        if (StringUtils.isBlank(key)) {
            queryWrapper.and((w) -> {
                w.eq(AttrEntity::getAttrId, key).or().like(AttrEntity::getAttrName, key);
            });
        }

        IPage<AttrEntity> page = page(
                new Query<AttrEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }


}
