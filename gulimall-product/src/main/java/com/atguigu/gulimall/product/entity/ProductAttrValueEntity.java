package com.atguigu.gulimall.product.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * spu属性值
 * 
 * @author zzc
 * @email 786614275@qq.com
 * @date 2022-04-28 10:26:08
 */
@Data
@TableName("pms_product_attr_value")
@ApiModel("spu属性值entity")

public class ProductAttrValueEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	@ApiModelProperty("id")
	@TableId
	private Long id;
	/**
	 * 商品id
	 */
	@ApiModelProperty("商品id")
	private Long spuId;
	/**
	 * 属性id
	 */
	@ApiModelProperty("属性id")
	private Long attrId;
	/**
	 * 属性名
	 */
	@ApiModelProperty("属性名")
	private String attrName;
	/**
	 * 属性值
	 */
	@ApiModelProperty("属性值")
	private String attrValue;
	/**
	 * 顺序
	 */
	@ApiModelProperty("顺序")
	private Integer attrSort;
	/**
	 * 快速展示【是否展示在介绍上；0-否 1-是】
	 */
	@ApiModelProperty("快速展示【是否展示在介绍上；0-否 1-是】")
	private Integer quickShow;

}
