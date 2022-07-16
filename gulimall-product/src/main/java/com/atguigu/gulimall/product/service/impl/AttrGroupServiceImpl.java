package com.atguigu.gulimall.product.service.impl;

import com.atguigu.common.constant.ProductConstant;
import com.atguigu.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.atguigu.gulimall.product.entity.AttrEntity;
import com.atguigu.gulimall.product.service.AttrAttrgroupRelationService;
import com.atguigu.gulimall.product.service.AttrService;
import com.atguigu.gulimall.product.entity.vo.AttrGroupWithAttrsVo;
import com.atguigu.gulimall.product.entity.vo.web.SkuItemVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.AttrGroupDao;
import com.atguigu.gulimall.product.entity.AttrGroupEntity;
import com.atguigu.gulimall.product.service.AttrGroupService;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {
    @Autowired
    private AttrAttrgroupRelationService relationService;
    @Autowired
    private AttrService attrService;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Long catelogyId) {
        LambdaQueryWrapper<AttrGroupEntity> query = new LambdaQueryWrapper<>();
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            query.and((obj) -> {
                obj.like(AttrGroupEntity::getAttrGroupId, key)
                        .or()
                        .like(AttrGroupEntity::getAttrGroupName, key);
            });
        }
        if (catelogyId == 0) {
            IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params),
                    query);
            return new PageUtils(page);
        } else {
            query.eq(AttrGroupEntity::getCatelogId, catelogyId);
            IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params),
                    query);
            return new PageUtils(page);
        }
    }

    @Override
    public List<AttrGroupWithAttrsVo> selectAttrGroupAndAttr(Long catelogId) {
        LambdaQueryWrapper<AttrGroupEntity> query = new LambdaQueryWrapper<>();
        query.eq(AttrGroupEntity::getCatelogId, catelogId);
        List<AttrGroupEntity> groupEntityList = this.list(query);

        List<AttrGroupWithAttrsVo> attrGroupWithAttrsVos = groupEntityList.stream().map(item -> {
            //创建收集的Vo
            AttrGroupWithAttrsVo attrGroupWithAttrsVo = new AttrGroupWithAttrsVo();
            //先赋值分组信息
            BeanUtils.copyProperties(item, attrGroupWithAttrsVo);
            //赋值分组下面的具体属性信息，先获取该分组下面的所有属性id
            List<AttrAttrgroupRelationEntity> relationEntityList = relationService.list(new LambdaQueryWrapper<AttrAttrgroupRelationEntity>()
                    .eq(AttrAttrgroupRelationEntity::getAttrGroupId, item.getAttrGroupId()));
            List<Long> attrIds = relationEntityList.stream().map(relation -> {
                return relation.getAttrId();
            }).collect(Collectors.toList());
            //根据属性id获取属性信息，并且赋值Vo类

            List<AttrEntity> attrEntityList = attrIds.stream().map(attrId -> {
                return attrService.getById(attrId);
            }).collect(Collectors.toList());

            attrGroupWithAttrsVo.setAttrs(attrEntityList);
            return attrGroupWithAttrsVo;
        }).collect(Collectors.toList());

        return attrGroupWithAttrsVos;
    }

    @Override
    public List<AttrEntity> selectAttrGroupAttrs(Long attrgroupId) {
        List<AttrAttrgroupRelationEntity> attrIds = relationService
                .list(new LambdaQueryWrapper<AttrAttrgroupRelationEntity>()
                        .select(AttrAttrgroupRelationEntity::getAttrId).eq(AttrAttrgroupRelationEntity::getAttrGroupId, attrgroupId));

        List<AttrEntity> attrEntityList = attrIds.stream().map(id -> {
            return attrService.getById(id);
        }).collect(Collectors.toList());
        return attrEntityList;
    }

    @Override
    public PageUtils selectAttrGroupNoRelationAttrs(Map<String, Object> params, Long attrgroupId) {
        //1.当前分类只能关联和自己相同所分类下面的属性
        // 获取分类id
        AttrGroupEntity groupEntity = this.getById(attrgroupId);
        Long catelogId = groupEntity.getCatelogId();

        //获取该分类下的分组id
        List<AttrGroupEntity> attrGroupEntityList =
                this.list(new LambdaQueryWrapper<AttrGroupEntity>()
                        .select(AttrGroupEntity::getAttrGroupId)
                        .eq(AttrGroupEntity::getCatelogId, catelogId));
        List<Long> groupIds = attrGroupEntityList.stream().map(item -> {
            return item.getAttrGroupId();
        }).collect(Collectors.toList());

        //获取分组-属性表中 属于该分类的分组的数据
        List<AttrAttrgroupRelationEntity> relationEntityList =
                relationService.list(new LambdaQueryWrapper<AttrAttrgroupRelationEntity>()
                        .in(AttrAttrgroupRelationEntity::getAttrGroupId, groupIds));
        //获取被分组关联过的属性id
        List<Long> attrIds = relationEntityList.stream().map(item -> {
            return item.getAttrId();
        }).collect(Collectors.toList());

        LambdaQueryWrapper<AttrEntity> queryWrapper = new LambdaQueryWrapper<>();
        //模糊查询
        String key = (String) params.get("key");
        if(!StringUtils.isEmpty(key)){
            queryWrapper.like(AttrEntity::getAttrId,key).like(AttrEntity::getAttrName,key);
        }

        //排除  被当前分类下-其他分组，包括当前分组关联过的  属性attr
        queryWrapper.eq(AttrEntity::getCatelogId, catelogId)
                .eq(AttrEntity::getAttrType, ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode());
        if(attrIds!=null&&attrIds.size()>0){
            queryWrapper.notIn(AttrEntity::getAttrId, attrIds);
        }

        IPage<AttrEntity> page = attrService.page(new Query<AttrEntity>().getPage(params), queryWrapper);
        return new PageUtils(page);
    }

    @Override
    public List<SkuItemVo.SpuItemAttrGroupVo> getAttrGroupAndAttrBySpuId(Long spuId, Long catalogId) {
        List<SkuItemVo.SpuItemAttrGroupVo> spuItemAttrGroupVos = baseMapper.getAttrGroupAndAttrBySpuId(spuId,catalogId);
        return spuItemAttrGroupVos;
    }

}