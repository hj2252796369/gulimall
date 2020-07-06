package com.project.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.project.common.utils.PageUtils;
import com.project.common.utils.Query;
import com.project.gulimall.product.dao.CategoryDao;
import com.project.gulimall.product.entity.CategoryEntity;
import com.project.gulimall.product.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        List<CategoryEntity> entityList = this.baseMapper.selectList(null);

        List<CategoryEntity> entities = entityList.stream().filter((categoryEntity) -> {
            return categoryEntity.getParentCid() == 0;
        }).map((categoryEntity) -> {
            categoryEntity.setChildren(this.findChildrens(categoryEntity, entityList));
            return categoryEntity;
        }).sorted((menu1, menu2) -> {
            return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
        }).collect(Collectors.toList());

        return entities;
    }

    @Override
    public void removeMeunByIds(List<Long> asList) {
        //TODO 其他逻辑  这里只处理逻辑删除
        this.baseMapper.deleteBatchIds(asList);
    }

    /**
     * 找到三级分类的全部路径
     *
     * @param catelogId
     * @return
     */
    @Override
    public Long[] findCatelogPath(Long catelogId) {
        List<Long> paths = Lists.newArrayList();
        List<Long> parentPath = this.findParentPath(catelogId, paths);

        Collections.reverse(parentPath);

        return parentPath.toArray(new Long[parentPath.size()]);
    }

    //225,25,2
    private List<Long> findParentPath(Long catelogId, List<Long> paths) {
        //1、添加当前节点id
        paths.add(catelogId);
        //查找父类
        CategoryEntity byId = this.getById(catelogId);
        if (byId.getParentCid() != 0) {
            this.findParentPath(byId.getParentCid(), paths);
        }
        return paths;
    }

    /**
     * 递归寻找子菜单
     *
     * @param entity     当前菜单项
     * @param entityList 所有的菜单项列表
     * @return
     */
    private List<CategoryEntity> findChildrens(CategoryEntity entity, List<CategoryEntity> entityList) {
        return entityList.stream().filter((categoryEntity) -> {
            return entity.getCatId().equals(categoryEntity.getParentCid());
        }).map((categoryEntity) -> {
            categoryEntity.setChildren(this.findChildrens(categoryEntity, entityList));
            return categoryEntity;
        }).sorted((menu1, menu2) -> {
            return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
        }).collect(Collectors.toList());
    }

}
