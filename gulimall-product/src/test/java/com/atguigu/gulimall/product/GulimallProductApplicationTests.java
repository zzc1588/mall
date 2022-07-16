package com.atguigu.gulimall.product;

import com.atguigu.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.atguigu.gulimall.product.entity.AttrEntity;
import com.atguigu.gulimall.product.service.*;
import com.atguigu.gulimall.product.entity.vo.web.SkuItemVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@SpringBootTest
class GulimallProductApplicationTests {

    @Autowired
    private BrandService brandService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private AttrAttrgroupRelationService relationService;
    @Autowired
    private AttrService attrService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private AttrGroupService attrGroupService;
    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;

    @Test
    void test0001(){
        List<SkuItemVo.SpuItemAttrGroupVo> attrGroupAndAttrBySpuId = attrGroupService.getAttrGroupAndAttrBySpuId(14L, 225L);
        System.out.println(attrGroupAndAttrBySpuId.toString());
    }

    @Test
    void test0002(){
        List<SkuItemVo.SkuItemSaleAttrVo> saleAttrsBySpuId = skuSaleAttrValueService.getSaleAttrsBySpuId(14L);
        System.out.println(saleAttrsBySpuId);
    }

    @Test
    void contextLoads() {
        List<AttrAttrgroupRelationEntity> entityList = relationService
                .list(new LambdaQueryWrapper<AttrAttrgroupRelationEntity>()
                        .select(AttrAttrgroupRelationEntity::getAttrId).eq(AttrAttrgroupRelationEntity::getAttrGroupId, 1));
        List<Long> attrIds = entityList.stream().map(item -> {
            return item.getAttrId();
        }).collect(Collectors.toList());

        LambdaQueryWrapper<AttrEntity> queryWrapper = new LambdaQueryWrapper<AttrEntity>().notIn(AttrEntity::getAttrId, attrIds);
        List<AttrEntity> list = attrService.list(queryWrapper);
        System.out.println(list);
    }
    @Test
    void contextLoads1() {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        ops.set("name","zzc");
    }
    @Test
    void contextLoads2() {
        RLock lock = redissonClient.getLock("my-lock");
        lock.lock();
        try {
            System.out.println("加锁成功！！！");
            Thread.sleep(30000);
        }catch (Exception e){

        }finally {
            System.out.println("释放锁");
            lock.unlock();
        }
    }

}
