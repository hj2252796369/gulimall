package com.project.gulimall.product.service.impl;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.common.utils.PageUtils;
import com.project.common.utils.Query;

import com.project.gulimall.product.dao.CategoryDao;
import com.project.gulimall.product.entity.CategoryEntity;
import com.project.gulimall.product.service.CategoryService;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        List<CategoryEntity> entityList = baseMapper.selectList(null);

        List<CategoryEntity> entities = entityList.stream().filter((categoryEntity)->{
            return categoryEntity.getParentCid() == 0;
        }).map((categoryEntity)->{
            categoryEntity.setChildren(findChildrens(categoryEntity, entityList));
            return categoryEntity;
        }).sorted((menu1, menu2)->{
            return (menu1.getSort()==null?0:menu1.getSort()) - (menu2.getSort()==null?0:menu2.getSort());
        }).collect(Collectors.toList());

        return entities;
    }

    /**
     * 递归寻找子菜单
     * @param entity            当前菜单项
     * @param entityList        所有的菜单项列表
     * @return
     */
    private List<CategoryEntity> findChildrens(CategoryEntity entity, List<CategoryEntity> entityList) {
        return entityList.stream().filter((categoryEntity)->{
            return  entity.getCatId().equals( categoryEntity.getParentCid());
        }).map((categoryEntity)->{
            categoryEntity.setChildren(findChildrens(categoryEntity, entityList));
            return categoryEntity;
        }).sorted((menu1, menu2)->{
            return (menu1.getSort()==null?0:menu1.getSort()) - (menu2.getSort()==null?0:menu2.getSort());
        }).collect(Collectors.toList());
    }

}
