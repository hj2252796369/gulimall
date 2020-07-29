package com.project.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.common.constant.AttributeTypeConstant;
import com.project.common.utils.PageUtils;
import com.project.common.utils.Query;
import com.project.gulimall.product.dao.AttrDao;
import com.project.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.project.gulimall.product.entity.AttrEntity;
import com.project.gulimall.product.entity.AttrGroupEntity;
import com.project.gulimall.product.service.AttrAttrgroupRelationService;
import com.project.gulimall.product.service.AttrGroupService;
import com.project.gulimall.product.service.AttrService;
import com.project.gulimall.product.service.CategoryService;
import com.project.gulimall.product.vo.AttrRespVO;
import com.project.gulimall.product.vo.AttrVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
        attrAttrgroupRelationEntity.setAttrGroupId(attrVO.getAttrGroupId());
        attrAttrgroupRelationEntity.setAttrId(attrEntity.getAttrId());
        attrAttrgroupRelationService.save(attrAttrgroupRelationEntity);
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
                attrRespVO.setGroupName(attrGroupService.getById(relationEntity.getAttrGroupId()).getAttrGroupName());
            }
            attrRespVO.setCatelogName(categoryService.getById(attrEntity.getCatelogId()).getName());
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

}
