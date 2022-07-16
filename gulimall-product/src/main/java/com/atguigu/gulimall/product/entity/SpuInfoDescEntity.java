package com.atguigu.gulimall.product.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * spu信息介绍
 * 
 * @author zzc
 * @email 786614275@qq.com
 * @date 2022-04-28 10:26:08
 */
@Data
@TableName("pms_spu_info_desc")
@ApiModel("Spu描述entity")
public class SpuInfoDescEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 商品id
	 */
	@ApiModelProperty("商品id")
	@TableId(type = IdType.INPUT)
	private Long spuId;
	/**
	 * 商品介绍
	 */
	@ApiModelProperty("商品介绍")
	private String decript;

}
