package com.atguigu.gulimall.product.service.impl;

import com.atguigu.gulimall.product.service.CategoryBrandRelationService;
import com.atguigu.gulimall.product.entity.vo.web.Catelog2Vo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.CategoryDao;
import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {
    @Autowired
    CategoryBrandRelationService categoryBrandRelationService;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private RedissonClient redissonClient;

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
        List<CategoryEntity> list = baseMapper.selectList(null);
        List<CategoryEntity> oneLevel = list.stream()
                .map(menu -> {
                    menu.setChildren(getChildrens(menu,list));
                    return menu;
                })
                .sorted((menu1,menu2)->{
                    return (menu1.getSort()==null?0:menu1.getSort())-(menu2.getSort()==null?0:menu2.getSort());
                })
                .filter(l -> l.getParentCid() == 0)
                .collect(Collectors.toList());
        return oneLevel;
    }

    @Override
    public Long[] findCatelogPath(Long catelogId) {
        List<Long> path = new ArrayList<>();
        findParentPath(catelogId, path);
        return path.toArray(new Long[path.size()]);
    }

    @Override
    @CacheEvict(value = "category",allEntries = true)
    public void updateCategory(CategoryEntity category) {
        this.updateById(category);
        categoryBrandRelationService.updateCategory(category.getCatId(),category.getName());
    }

    @Override
    @Cacheable(value = {"category"},key = "#root.methodName")
    public List<CategoryEntity> getLevel1Catetorys() {
        Long t1 = System.currentTimeMillis();
        List<CategoryEntity> list =
                this.list(new LambdaQueryWrapper<CategoryEntity>().eq(CategoryEntity::getParentCid, 0));
        System.out.println("getLevel1Catetorys??????????????????????????????"+ (System.currentTimeMillis() - t1));
        return list;
    }


    @Transactional
    @Cacheable(value = "category",key="#root.methodName" )
    @Override
    public Map<String, List<Catelog2Vo>> getCatalogJson() {
        List<CategoryEntity> selectList = baseMapper.selectList(null);
        //???????????????1?????????
        List<CategoryEntity> level1Catetorys = getParentCid(selectList,0L);
        //???????????????????????????????????????map??????
        Map<String, List<Catelog2Vo>> listMap = level1Catetorys.stream()
                //?????????map key???????????????id???value???vo  list
                .collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
                    //?????????????????????????????????????????????
                    List<CategoryEntity> categoryEntities =
                            getParentCid(selectList, v.getCatId());
                    List<Catelog2Vo> catelog2VoList = null;
                    if (categoryEntities != null) {
                        catelog2VoList = categoryEntities.stream()
                                .map(l2 -> {
                                    //??????????????????Vo??????
                                    Catelog2Vo catelog2Vo =
                                            new Catelog2Vo(v.getCatId().toString(), null, l2.getCatId().toString(), l2.getName());

                                    //?????????????????????????????????????????????
                                    List<CategoryEntity> category3 =
                                            getParentCid(selectList, l2.getCatId());
                                    if(category3!=null){
                                        //?????????????????????????????????????????????
                                        List<Catelog2Vo.Catelog3Vo> catelog3VoList = category3.stream().map(l3 -> {
                                            Catelog2Vo.Catelog3Vo c3Vo =
                                                    new Catelog2Vo.Catelog3Vo(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName());
                                            return c3Vo;
                                        }).collect(Collectors.toList());
                                        //??????????????????Vo???????????????Vo??????
                                        catelog2Vo.setCatalog3List(catelog3VoList);

                                    }
                                    return catelog2Vo;
                                }).collect(Collectors.toList());
                    }
                    return catelog2VoList;
                }));
        return listMap;
    }

    /**
     * ???????????????id???????????????
     * @param selectList ??????????????????
     * @param parent_cid ?????????id
     * @return
     */
    private List<CategoryEntity> getParentCid(List<CategoryEntity> selectList,Long parent_cid) {
        List<CategoryEntity> collect = selectList.stream().filter(item -> item.getParentCid() == parent_cid).collect(Collectors.toList());
        return collect;
    }

    private List<Long> findParentPath(Long catelogId,List<Long> path){
        CategoryEntity categoryEntity = baseMapper.selectById(catelogId);
        Long parentId = categoryEntity.getParentCid();
        if(parentId!=0){
            findParentPath(parentId,path);
        }
        path.add(catelogId);
        return path;
    }

    public List<CategoryEntity> getChildrens(CategoryEntity root,List<CategoryEntity> all){
        List<CategoryEntity> collect = all.stream()
                .filter(categoryEntity -> categoryEntity.getParentCid().equals(root.getCatId()))
                .map(menu -> {
                    menu.setChildren(getChildrens(menu, all));
                    return menu;
                })
                .sorted((menu1, menu2) -> {
                    return (menu1.getSort()==null?0:menu1.getSort())-(menu2.getSort()==null?0:menu2.getSort());
                }).collect(Collectors.toList());
        return collect;
    }



}