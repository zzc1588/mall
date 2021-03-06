package com.atguigu.gulimall.product.service.impl;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.constant.ProductConstant;
import com.atguigu.common.to.SkuHasStockVo;
import com.atguigu.common.to.SpuBoundTo;
import com.atguigu.common.to.SpuReductionTo;
import com.atguigu.common.to.es.SkuEsModel;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.product.entity.*;
import com.atguigu.gulimall.product.feign.CouponFeignService;
import com.atguigu.gulimall.product.feign.SearchFeignService;
import com.atguigu.gulimall.product.feign.WareFeignService;
import com.atguigu.gulimall.product.service.*;
import com.atguigu.gulimall.product.entity.vo.Images;
import com.atguigu.gulimall.product.entity.vo.Skus;
import com.atguigu.gulimall.product.entity.vo.SpuSaveVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.SpuInfoDao;


@Service("spuInfoService")
@Slf4j
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {
    @Autowired
    private SpuImagesService spuImagesService;
    @Autowired
    private SpuInfoDescService spuInfoDescService;
    @Autowired
    private ProductAttrValueService productAttrValueService;
    @Autowired
    private SkuInfoService skuInfoService;
    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;
    @Autowired
    private AttrService attrService;
    @Autowired
    private SkuImagesService skuImagesService;
    @Autowired
    private CouponFeignService couponFeignService;
    @Autowired
    private BrandService brandService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private WareFeignService wareFeignService;
    @Autowired
    private SearchFeignService searchFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );
        return new PageUtils(page);
    }

    @Override
//    @Transactional
    @GlobalTransactional
    public void saveInfo(SpuSaveVo spuSaveVo) {
        log.info("??????spu?????????{}",spuSaveVo);
        //1.??????spu???????????? pms_spu_info
        SpuInfoEntity spuInfo = new SpuInfoEntity();
        BeanUtils.copyProperties(spuSaveVo, spuInfo);
        this.save(spuInfo);
        //?????????spu_info???id
        Long spuId = spuInfo.getId();
        //2.??????spu?????????????????????????????????  pms_spu_info_desc
        SpuInfoDescEntity spuInfoDescEntity = new SpuInfoDescEntity();
        spuInfoDescEntity.setSpuId(spuId);
        spuInfoDescEntity.setDecript(String.join(",", spuSaveVo.getDecript()));
        spuInfoDescService.save(spuInfoDescEntity);
        //3.??????spu???????????????????????????     pms_spu_images
        spuImagesService.saveImages(spuId, spuSaveVo.getImages());
        //4.????????????????????????????????????????????????  pms_product_attr_value
        List<ProductAttrValueEntity> productAttrValueEntityList = spuSaveVo.getBaseAttrs().stream().map(item -> {
            ProductAttrValueEntity productAttrValueEntity = new ProductAttrValueEntity();
            productAttrValueEntity.setSpuId(spuId);
            productAttrValueEntity.setAttrValue(item.getAttrValues());
            productAttrValueEntity.setAttrId(item.getAttrId());
            productAttrValueEntity.setQuickShow(item.getShowDesc());
            AttrEntity attrEntity = attrService.getById(item.getAttrId());
            productAttrValueEntity.setAttrName(attrEntity.getAttrName());
            return productAttrValueEntity;
        }).collect(Collectors.toList());
        productAttrValueService.saveBatch(productAttrValueEntityList);
        //5.?????? sms_spu_bounds ?????????
        //5.??????sku??????
        //5.1 ??????sku???????????? pms_sku_info
        //5.2 ??????sku????????????  pms_sku_images
        List<Skus> skusList = spuSaveVo.getSkus();
        Long brandId = spuInfo.getBrandId();
        Long catalogId = spuInfo.getCatalogId();
        skusList.stream().forEach(item -> {
            //??????????????????
            String defaultImage = "";
            for (Images img : item.getImages()) {
                if (img.getDefaultImg() == 1) {
                    defaultImage = img.getImgUrl();
                }
            }
            //??????sku????????????
            SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
            skuInfoEntity.setSpuId(spuId);
            BeanUtils.copyProperties(item, skuInfoEntity);
            skuInfoEntity.setBrandId(brandId);
            skuInfoEntity.setCatalogId(catalogId);
            skuInfoEntity.setSkuDefaultImg(defaultImage);
            skuInfoService.save(skuInfoEntity);
            Long skuId = skuInfoEntity.getSkuId();
            //??????skuImage??????
            List<SkuImagesEntity> imagesEntityList = item.getImages()
                    .stream()
                    .filter(url -> {
                        return url.getImgUrl() != null && url.getImgUrl() != "";
                    })
                    .map(image -> {
                        SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                        skuImagesEntity.setImgUrl(image.getImgUrl());
                        skuImagesEntity.setSkuId(skuId);
                        skuImagesEntity.setDefaultImg(image.getDefaultImg());
                        return skuImagesEntity;
                    }).collect(Collectors.toList());
            if (imagesEntityList != null && imagesEntityList.size() > 0) {
                skuImagesService.saveBatch(imagesEntityList);
            }
            //5.3 ??????sku??????????????????  pms_sku_sale_attr_value
            List<SkuSaleAttrValueEntity> saleAttrValueEntityList = item.getAttr().stream().map(attr -> {
                SkuSaleAttrValueEntity skuSaleAttrValueEntity = new SkuSaleAttrValueEntity();
                BeanUtils.copyProperties(attr, skuSaleAttrValueEntity);
                skuSaleAttrValueEntity.setSkuId(skuId);
                return skuSaleAttrValueEntity;
            }).collect(Collectors.toList());
            skuSaleAttrValueService.saveBatch(saleAttrValueEntityList);
            //????????????
            SpuReductionTo spuReductionTo = new SpuReductionTo();
            BeanUtils.copyProperties(item, spuReductionTo);
            spuReductionTo.setSkuId(skuId);
            if (spuReductionTo.getFullCount() > 0 ||
                    spuReductionTo.getFullPrice().compareTo(new BigDecimal("0")) == 1 ||
                    spuReductionTo.getMemberPrice() != null) {
                R r = couponFeignService.savaSpuReduction(spuReductionTo);
                if (r.getCode() != 0) {
                    log.error("????????????Reduction???????????????");
                }
            }
        });

        //5.4 ??????sku??????????????????  ??????-gulimall-sms
        // sms_sku_ladder????????????sms_sku_full_reduction ????????????sms_member_price??????????????????
        SpuBoundTo spuBoundTo = new SpuBoundTo();
        BeanUtils.copyProperties(spuSaveVo.getBounds(), spuBoundTo);
        spuBoundTo.setSpuId(spuId);

        couponFeignService.savaSpuBounds(spuBoundTo);
        log.info("??????spu?????????{}");
    }

    @Override
    public PageUtils queryPageByConfition(Map<String, Object> params) {
        LambdaQueryWrapper<SpuInfoEntity> wrapper = new LambdaQueryWrapper<>();
        String status = (String) params.get("status");
        if (!StringUtils.isEmpty(status)) {
            wrapper.eq(SpuInfoEntity::getPublishStatus, status);
        }
        String brandId = (String) params.get("brandId");
        if (!StringUtils.isEmpty(brandId) && !"0".equalsIgnoreCase(brandId)) {
            wrapper.eq(SpuInfoEntity::getBrandId, brandId);
        }
        String catelogId = (String) params.get("catelogId");
        if (!StringUtils.isEmpty(catelogId) && !"0".equalsIgnoreCase(catelogId)) {
            wrapper.eq(SpuInfoEntity::getCatalogId, catelogId);
        }
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            wrapper.and(w -> {
                w.like(SpuInfoEntity::getId, key).or().like(SpuInfoEntity::getSpuName, key);
            });
        }

        IPage<SpuInfoEntity> page = this.page(new Query<SpuInfoEntity>().getPage(params), wrapper);
        return new PageUtils(page);
    }

    @Override
    public void up(Long spuId) {
        //?????????????????????????????????
        //??????spuId???????????????sku????????????????????????
        List<SkuInfoEntity> skus = skuInfoService.getSkuBySpuId(spuId);
        List<Long> skuIdList = skus.stream()
                .map(SkuInfoEntity::getSkuId)
                .collect(Collectors.toList());
        //TODO ????????????sku????????????????????????????????????
        List<ProductAttrValueEntity> baseAttrs = productAttrValueService.baseAttrlistforspu(spuId);
        List<Long> attrIds = baseAttrs.stream().map(attr -> {
            return attr.getAttrId();
        }).collect(Collectors.toList());
        List<Long> searchAttrIds = attrService.selectSearchAttrs(attrIds);
        Set<Long> set = new HashSet<>(searchAttrIds);
        List<SkuEsModel.Attrs> attrsList = baseAttrs.stream()
                .filter(attr -> set.contains(attr.getAttrId()))
                .map(item -> {
                    SkuEsModel.Attrs attr = new SkuEsModel.Attrs();
                    BeanUtils.copyProperties(item, attr);
                    return attr;
                })
                .collect(Collectors.toList());
        //TODO ??????????????????ware?????? ???????????????
        Map<Long, Boolean> StockMap = null;
        try {
            R hasStock = wareFeignService.getSkuHasStock(skuIdList);
            TypeReference<List<SkuHasStockVo>> typeReference = new TypeReference<List<SkuHasStockVo>>(){};
            StockMap = hasStock.getData(typeReference)
                    .stream()
                    .collect(Collectors.toMap(SkuHasStockVo::getSkuId, item -> item.getHasStock()));
        }catch (Exception e){
            log.error("??????????????????????????????{}",e);
        }


        //????????????
        Map<Long, Boolean> finalStockMap = StockMap;
        List<SkuEsModel> upProducts = skus.stream().map(sku -> {
            SkuEsModel skuEsModel = new SkuEsModel();
            BeanUtils.copyProperties(sku, skuEsModel);
            skuEsModel.setSkuPrice(sku.getPrice());
            skuEsModel.setSkuImg(sku.getSkuDefaultImg());
            //TODO ???????????? 0
            skuEsModel.setHotScore(0L);
            //TODO ??????????????????????????????
            BrandEntity brand = brandService.getById(skuEsModel.getBrandId());
            skuEsModel.setBrandName(brand.getName());
            skuEsModel.setBrandImg(brand.getLogo());
            //???????????????????????????????????????????????????true
            if(finalStockMap == null){
                skuEsModel.setHasStock(true);
            }else {
                skuEsModel.setHasStock(finalStockMap.get(sku.getSkuId()));
            }

            CategoryEntity category = categoryService.getById(skuEsModel.getCatalogId());
            skuEsModel.setCatalogName(category.getName());
            //??????????????????
            skuEsModel.setAttrs(attrsList);

            return skuEsModel;
        }).collect(Collectors.toList());
        //TODO ???????????????????????????es?????? gulimall-search
        R r = searchFeignService.productStatusUp(upProducts);
        if(r.getCode() == 0){
            //????????????
            baseMapper.updateSpuStatus(spuId, ProductConstant.StatusEnum.SPU_UP.getCode());
        }else {
            //??????????????????
            //TODO ??????????????????????????????????????????????????????
        }

    }

    @Override
    public SpuInfoEntity getSpuBySkuId(Long skuId) {
        SkuInfoEntity skuInfo = skuInfoService.getById(skuId);
        SpuInfoEntity spuInfo = this.baseMapper.selectById(skuInfo.getSpuId());
        return spuInfo;
    }

    /**
     * ??????spuId????????????
     * @param spuId
     * @throws IOException
     */
    @Override
    public void soldOutSpuById(Long spuId) throws IOException {
        List<SkuInfoEntity> skuList = skuInfoService.getSkuBySpuId(spuId);
        List<Long> skuIds = skuList.stream().map(item -> {
            return item.getSkuId();
        }).collect(Collectors.toList());
        searchFeignService.soldOutProduct(skuIds);
        baseMapper.updateSpuStatus(spuId, ProductConstant.StatusEnum.SPU_DOWN.getCode());

    }

}