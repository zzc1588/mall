package com.atguigu.gulimall.product.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 品牌分类关联
 * 
 * @author zzc
 * @email 786614275@qq.com
 * @date 2022-04-28 10:26:08
 */
@Data
@TableName("pms_category_brand_relation")
@ApiModel("品牌分类关联entity")

public class CategoryBrandRelationEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@ApiModelProperty("品牌分类关联id")
	@TableId
	private Long id;
	/**
	 * 品牌id
	 */
	@ApiModelProperty("品牌id")
	private Long brandId;
	/**
	 * 分类id
	 */
	@ApiModelProperty("分类id")
	private Long catelogId;
	/**
	 * 品牌名
	 */
	@ApiModelProperty("品牌名")
	private String brandName;
	/**
	 * 分类名
	 */
	@ApiModelProperty("分类名")
	private String catelogName;

}
