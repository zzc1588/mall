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
        log.info("保存spu开始：{}",spuSaveVo);
        //1.保存spu基本信息 pms_spu_info
        SpuInfoEntity spuInfo = new SpuInfoEntity();
        BeanUtils.copyProperties(spuSaveVo, spuInfo);
        this.save(spuInfo);
        //返回的spu_info的id
        Long spuId = spuInfo.getId();
        //2.保存spu商品描述中轮播图的图片  pms_spu_info_desc
        SpuInfoDescEntity spuInfoDescEntity = new SpuInfoDescEntity();
        spuInfoDescEntity.setSpuId(spuId);
        spuInfoDescEntity.setDecript(String.join(",", spuSaveVo.getDecript()));
        spuInfoDescService.save(spuInfoDescEntity);
        //3.保存spu的商品介绍的图片集     pms_spu_images
        spuImagesService.saveImages(spuId, spuSaveVo.getImages());
        //4.保存基本属性信息（规格参数信息）  pms_product_attr_value
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
        //5.保存 sms_spu_bounds 积分表
        //5.保存sku信息
        //5.1 保存sku基本信息 pms_sku_info
        //5.2 保存sku图片信息  pms_sku_images
        List<Skus> skusList = spuSaveVo.getSkus();
        Long brandId = spuInfo.getBrandId();
        Long catalogId = spuInfo.getCatalogId();
        skusList.stream().forEach(item -> {
            //获取默认图片
            String defaultImage = "";
            for (Images img : item.getImages()) {
                if (img.getDefaultImg() == 1) {
                    defaultImage = img.getImgUrl();
                }
            }
            //保存sku基本信息
            SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
            skuInfoEntity.setSpuId(spuId);
            BeanUtils.copyProperties(item, skuInfoEntity);
            skuInfoEntity.setBrandId(brandId);
            skuInfoEntity.setCatalogId(catalogId);
            skuInfoEntity.setSkuDefaultImg(defaultImage);
            skuInfoService.save(skuInfoEntity);
            Long skuId = skuInfoEntity.getSkuId();
            //保存skuImage信息
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
            //5.3 保存sku销售属性信息  pms_sku_sale_attr_value
            List<SkuSaleAttrValueEntity> saleAttrValueEntityList = item.getAttr().stream().map(attr -> {
                SkuSaleAttrValueEntity skuSaleAttrValueEntity = new SkuSaleAttrValueEntity();
                BeanUtils.copyProperties(attr, skuSaleAttrValueEntity);
                skuSaleAttrValueEntity.setSkuId(skuId);
                return skuSaleAttrValueEntity;
            }).collect(Collectors.toList());
            skuSaleAttrValueService.saveBatch(saleAttrValueEntityList);
            //满减信息
            SpuReductionTo spuReductionTo = new SpuReductionTo();
            BeanUtils.copyProperties(item, spuReductionTo);
            spuReductionTo.setSkuId(skuId);
            if (spuReductionTo.getFullCount() > 0 ||
                    spuReductionTo.getFullPrice().compareTo(new BigDecimal("0")) == 1 ||
                    spuReductionTo.getMemberPrice() != null) {
                R r = couponFeignService.savaSpuReduction(spuReductionTo);
                if (r.getCode() != 0) {
                    log.error("远程保存Reduction失败！！！");
                }
            }
        });

        //5.4 保存sku优惠满减信息  跨库-gulimall-sms
        // sms_sku_ladder打折表，sms_sku_full_reduction 满减表，sms_member_price会员价格表，
        SpuBoundTo spuBoundTo = new SpuBoundTo();
        BeanUtils.copyProperties(spuSaveVo.getBounds(), spuBoundTo);
        spuBoundTo.setSpuId(spuId);

        couponFeignService.savaSpuBounds(spuBoundTo);
        log.info("保存spu结束：{}");
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
        //组装前端页面需要的数据
        //根据spuId查询对应的sku信息，和品牌名字
        List<SkuInfoEntity> skus = skuInfoService.getSkuBySpuId(spuId);
        List<Long> skuIdList = skus.stream()
                .map(SkuInfoEntity::getSkuId)
                .collect(Collectors.toList());
        //TODO 查询当前sku所有可以被检索的规格属性
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
        //TODO 发送远程请求ware查询 是否有库存
        Map<Long, Boolean> StockMap = null;
        try {
            R hasStock = wareFeignService.getSkuHasStock(skuIdList);
            TypeReference<List<SkuHasStockVo>> typeReference = new TypeReference<List<SkuHasStockVo>>(){};
            StockMap = hasStock.getData(typeReference)
                    .stream()
                    .collect(Collectors.toMap(SkuHasStockVo::getSkuId, item -> item.getHasStock()));
        }catch (Exception e){
            log.error("库存查询远程调用异常{}",e);
        }


        //封装数据
        Map<Long, Boolean> finalStockMap = StockMap;
        List<SkuEsModel> upProducts = skus.stream().map(sku -> {
            SkuEsModel skuEsModel = new SkuEsModel();
            BeanUtils.copyProperties(sku, skuEsModel);
            skuEsModel.setSkuPrice(sku.getPrice());
            skuEsModel.setSkuImg(sku.getSkuDefaultImg());
            //TODO 默认评分 0
            skuEsModel.setHotScore(0L);
            //TODO 查询品牌名字和分类名
            BrandEntity brand = brandService.getById(skuEsModel.getBrandId());
            skuEsModel.setBrandName(brand.getName());
            skuEsModel.setBrandImg(brand.getLogo());
            //设置是否有库存，远程查询失败默认为true
            if(finalStockMap == null){
                skuEsModel.setHasStock(true);
            }else {
                skuEsModel.setHasStock(finalStockMap.get(sku.getSkuId()));
            }

            CategoryEntity category = categoryService.getById(skuEsModel.getCatalogId());
            skuEsModel.setCatalogName(category.getName());
            //设置检索属性
            skuEsModel.setAttrs(attrsList);

            return skuEsModel;
        }).collect(Collectors.toList());
        //TODO 将封装好的数据发给es保存 gulimall-search
        R r = searchFeignService.productStatusUp(upProducts);
        if(r.getCode() == 0){
            //调用成功
            baseMapper.updateSpuStatus(spuId, ProductConstant.StatusEnum.SPU_UP.getCode());
        }else {
            //远程调用失败
            //TODO 重复调用问题？接口幂等性，重试机制？
        }

    }

    @Override
    public SpuInfoEntity getSpuBySkuId(Long skuId) {
        SkuInfoEntity skuInfo = skuInfoService.getById(skuId);
        SpuInfoEntity spuInfo = this.baseMapper.selectById(skuInfo.getSpuId());
        return spuInfo;
    }

    /**
     * 根据spuId下架商品
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