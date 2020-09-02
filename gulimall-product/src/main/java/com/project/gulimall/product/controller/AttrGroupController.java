package com.project.gulimall.product.controller;

import com.project.common.utils.PageUtils;
import com.project.common.utils.R;
import com.project.gulimall.product.entity.AttrEntity;
import com.project.gulimall.product.entity.AttrGroupEntity;
import com.project.gulimall.product.service.AttrAttrgroupRelationService;
import com.project.gulimall.product.service.AttrGroupService;
import com.project.gulimall.product.service.AttrService;
import com.project.gulimall.product.service.CategoryService;
import com.project.gulimall.product.vo.AttrGroupRelationVO;
import com.project.gulimall.product.vo.AttrGroupWithAttrVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * 属性分组
 *
 * @author hujie
 * @email 2252796369@qq.com
 * @date 2020-06-09 22:16:32
 */
@RestController
@RequestMapping("product/attrgroup")
public class AttrGroupController {
    @Autowired
    AttrAttrgroupRelationService attrAttrgroupRelationService;
    @Autowired
    private AttrGroupService attrGroupService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private AttrService attrService;

    /**
     * 获取属性分组的关联的所有属性
     *
     * @param attrgroupId
     * @return
     */
    //  /product/attrgroup/{attrgroupId}/attr/relation
    @RequestMapping("/{attrgroupId}/attr/relation")
    public R attrRelation(@PathVariable("attrgroupId") Long attrgroupId) {
        List<AttrEntity> attrEntityList = attrService.getRelationAttr(attrgroupId);
        return R.ok().put("data", attrEntityList);
    }


    /**
     * 删除属性与分组的关联关系
     *
     * @param attrGroupRelationVOS
     * @return
     */
    //    /product/attrgroup/attr/relation/delete
    @PostMapping("/attr/relation/delete")
    public R relationDelete(@RequestBody AttrGroupRelationVO[] attrGroupRelationVOS) {
        attrService.relationDelete(attrGroupRelationVOS);
        return R.ok();
    }

    /**
     * 获取属性分组没有关联的其他属性
     *
     * @param params
     * @param attrgroupId
     * @return
     */
    @GetMapping("/{attrgroupId}/noattr/relation")
    public R noAttrRelation(@RequestParam Map<String, Object> params, @PathVariable("attrgroupId") Long attrgroupId) {
        PageUtils page = attrService.getNoAttrRelation(params, attrgroupId);
        return R.ok().put("page", page);
    }


    /**
     * 获取分类下所有分组&关联属性
     *
     * @return
     */
    @GetMapping("/{catelogId}/withattr")
    public R attrGroupListWithAttr(@PathVariable("catelogId") String catelogId) {
        List<AttrGroupWithAttrVo> data = attrGroupService.attrGroupListWithAttr(catelogId);
        return R.ok().put("data", data);
    }


    /**
     * 添加属性与分组关联关系
     * /product/attrgroup/attr/relation
     *
     * @param voList
     * @return
     */
    @PostMapping("/attr/relation")
    public R relationSaveBatch(@RequestBody List<AttrGroupRelationVO> voList) {
        attrAttrgroupRelationService.relationSaveBatch(voList);
        return R.ok();
    }

    /**
     * 列表
     */
    @RequestMapping("/list/{catelogId}")
    public R list(@RequestParam Map<String, Object> params, @PathVariable("catelogId") Long catelogId) {
        PageUtils page = attrGroupService.queryPage(params, catelogId);
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrGroupId}")
    public R info(@PathVariable("attrGroupId") Long attrGroupId) {
        AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);

        Long catelogId = attrGroup.getCatelogId();
        Long[] path = categoryService.findCatelogPath(catelogId);

        attrGroup.setCatelogPath(path);

        return R.ok().put("attrGroup", attrGroup);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody AttrGroupEntity attrGroup) {
        attrGroupService.save(attrGroup);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody AttrGroupEntity attrGroup) {
        attrGroupService.updateById(attrGroup);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] attrGroupIds) {
        attrGroupService.removeByIds(Arrays.asList(attrGroupIds));

        return R.ok();
    }

}
