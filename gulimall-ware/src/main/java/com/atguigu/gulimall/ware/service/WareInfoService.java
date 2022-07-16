package com.atguigu.gulimall.ware.service;

import com.atguigu.gulimall.ware.entity.vo.FareVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.ware.entity.WareInfoEntity;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 仓库信息
 *
 * @author zzc
 * @email 786614275@qq.com
 * @date 2022-04-28 11:45:30
 */
public interface WareInfoService extends IService<WareInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 根据收获地址获取运费 TODO 该业务未完成，只是获取手机号最后一位为价格
     * @param addrId
     * @return
     */
    FareVo getFare(Long addrId);
}

