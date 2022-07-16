package com.atguigu.gulimall.product.service.impl;

import com.atguigu.gulimall.product.entity.SkuImagesEntity;
import com.atguigu.gulimall.product.entity.SpuInfoDescEntity;
import com.atguigu.gulimall.product.service.*;
import com.atguigu.gulimall.product.entity.vo.Skus;
import com.atguigu.gulimall.product.entity.vo.web.SkuItemVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.SkuInfoDao;
import com.atguigu.gulimall.product.entity.SkuInfoEntity;
import org.springframework.transaction.annotation.Transactional;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {
    @Autowired
    private SkuImagesService skuImagesService;
    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    private SpuInfoDescService spuInfoDescService;
    @Autowired
    private AttrGroupService attrGroupService;
    @Autowired
    private ThreadPoolExecutor executor;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<SkuInfoEntity>()
        );

        return new PageUtils(page);
    }



    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        LambdaQueryWrapper<SkuInfoEntity> wrapper = new LambdaQueryWrapper<>();
        /**
         * key:
         * catelogId: 0
         * brandId: 0
         * min: 0
         * max: 0
         */
        String key = (String) params.get("key");
        if(!StringUtils.isEmpty(key)){
            wrapper.like(SkuInfoEntity::getSkuName,key).or().like(SkuInfoEntity::getSkuId,key);
        }
        String catelogId = (String) params.get("catelogId");
        if(!StringUtils.isEmpty(catelogId) && !"0".equalsIgnoreCase(catelogId)){
            wrapper.eq(SkuInfoEntity::getCatalogId,catelogId);
        }
        String brandId = (String) params.get("brandId");
        if(!StringUtils.isEmpty(brandId) && !"0".equalsIgnoreCase(brandId)){
            wrapper.eq(SkuInfoEntity::getBrandId,brandId);
        }
        String min = (String) params.get("min");
        if(!StringUtils.isEmpty(min)){
            wrapper.ge(SkuInfoEntity::getPrice,min);
        }
        String max = (String) params.get("max");
        if(!StringUtils.isEmpty(max)){
            try {
                BigDecimal bigDecimal = new BigDecimal(max);
                if(bigDecimal.compareTo(new BigDecimal("0")) == 1){
                    wrapper.le(SkuInfoEntity::getPrice,max);
                }
            }catch (Exception e){

            }
        }
        IPage<SkuInfoEntity> page = this.page(new Query<SkuInfoEntity>().getPage(params), wrapper);
        return new PageUtils(page);
    }
    @Transactional
    @Override
    public void saveSkuInfo(Long brandId,Long catalogId,Long spuId, List<Skus> skusList) {
//        skusList.stream().forEach(item->{
//            //获取默认图片
//            String defaultImage = "";
//            for (Images img : item.getImages()) {
//                if(img.getDefaultImg() == 1){
//                    defaultImage = img.getImgUrl();
//                }
//            }
//            //保存sku基本信息
//            SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
//            skuInfoEntity.setSpuId(spuId);
//            BeanUtils.copyProperties(item,skuInfoEntity);
//            skuInfoEntity.setBrandId(brandId);
//            skuInfoEntity.setCatalogId(catalogId);
//            skuInfoEntity.setSkuDefaultImg(defaultImage);
//            this.save(skuInfoEntity);
//            Long skuId = skuInfoEntity.getSkuId();
//            //保存skuImage信息
//            List<SkuImagesEntity> imagesEntityList = item.getImages().stream().map(image -> {
//                SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
//                skuImagesEntity.setImgUrl(image.getImgUrl());
//                skuImagesEntity.setSkuId(skuId);
//                skuImagesEntity.setDefaultImg(image.getDefaultImg());
//                return skuImagesEntity;
//            }).collect(Collectors.toList());
//            skuImagesService.saveBatch(imagesEntityList);
//        });
    }

    @Override
    public List<SkuInfoEntity> getSkuBySpuId(Long spuId) {
        List<SkuInfoEntity> list = this.list(new LambdaQueryWrapper<SkuInfoEntity>().eq(SkuInfoEntity::getSpuId, spuId));
        return list;
    }

    @Override
    public SkuItemVo item(Long skuId) throws ExecutionException, InterruptedException {
        //sku基本信息获取
        SkuItemVo skuItemVo = new SkuItemVo();
        CompletableFuture<SkuInfoEntity> infoFuture = CompletableFuture.supplyAsync(() -> {
            SkuInfoEntity skuInfo = this.getById(skuId);
            skuItemVo.setInfo(skuInfo);
            return skuInfo;
        }, executor);
        CompletableFuture<Void> saleAttrFuture = infoFuture.thenAcceptAsync((res) -> {
            //获取spu销售属性组合
            List<SkuItemVo.SkuItemSaleAttrVo> saleAttr = skuSaleAttrValueService.getSaleAttrsBySpuId(res.getSpuId());
            skuItemVo.setSaleAttr(saleAttr);
        }, executor);

        CompletableFuture<Void> infoDescFuture = infoFuture.thenAcceptAsync((res) -> {
            //商品介绍（共享spu信息）
            SpuInfoDescEntity spuInfoDesc = spuInfoDescService.getById(res.getSpuId());
            skuItemVo.setDesc(spuInfoDesc);
        }, executor);

        CompletableFuture<Void> groupAttrsFuture = infoFuture.thenAcceptAsync((res) -> {
            //spu规格参数信息
            List<SkuItemVo.SpuItemAttrGroupVo> groupAttrs =
                    attrGroupService.getAttrGroupAndAttrBySpuId(res.getSpuId(), res.getCatalogId());
            skuItemVo.setGroupAttrs(groupAttrs);
        }, executor);


        //sku图片信息  sku images
        CompletableFuture<Void> imagesFuture = CompletableFuture.runAsync(() -> {
            List<SkuImagesEntity> images = skuImagesService.getBySkuId(skuId);
            skuItemVo.setImages(images);
        }, executor);

        CompletableFuture.allOf(saleAttrFuture,infoDescFuture,groupAttrsFuture,imagesFuture).get();
        return skuItemVo;
    }

    @Override
    public BigDecimal getNewSkuPriceById(Long skuId) {
        SkuInfoEntity skuInfoEntity = this.baseMapper.selectOne(new LambdaQueryWrapper<SkuInfoEntity>().eq(SkuInfoEntity::getSkuId, skuId));
        return skuInfoEntity.getPrice();
    }

}