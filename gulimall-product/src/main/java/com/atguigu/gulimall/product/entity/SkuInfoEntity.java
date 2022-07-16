package com.atguigu.gulimall.product.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * sku信息
 * 
 * @author leifengyang
 * @email leifengyang@gmail.com
 * @date 2019-10-01 21:08:49
 */
@Data
@TableName("pms_sku_info")
@ApiModel("sku信息entity")

public class SkuInfoEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * skuId
	 */
	@TableId
	@ApiModelProperty("skuId")
	private Long skuId;
	/**
	 * spuId
	 */
	@ApiModelProperty("spuId")
	private Long spuId;
	/**
	 * sku名称
	 */
	@ApiModelProperty("sku名称")
	private String skuName;
	/**
	 * sku介绍描述
	 */
	@ApiModelProperty("sku介绍描述")
	private String skuDesc;
	/**
	 * 所属分类id
	 */
	@ApiModelProperty("所属分类id")
	private Long catalogId;
	/**
	 * 品牌id
	 */
	@ApiModelProperty("品牌id")
	private Long brandId;
	/**
	 * 默认图片
	 */
	@ApiModelProperty("默认图片")
	private String skuDefaultImg;
	/**
	 * 标题
	 */
	@ApiModelProperty("标题")
	private String skuTitle;
	/**
	 * 副标题
	 */
	@ApiModelProperty("副标题")
	private String skuSubtitle;
	/**
	 * 价格
	 */
	@ApiModelProperty("价格")
	private BigDecimal price;
	/**
	 * 销量
	 */
	@ApiModelProperty("销量")
	private Long saleCount;

}
