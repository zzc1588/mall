package com.atguigu.gulimall.product.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * sku销售属性&值
 * 
 * @author leifengyang
 * @email leifengyang@gmail.com
 * @date 2019-10-01 21:08:49
 */
@Data
@TableName("pms_sku_sale_attr_value")
@ApiModel("sku销售属性&值entity")
public class SkuSaleAttrValueEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	@TableId
	@ApiModelProperty("id")
	private Long id;
	/**
	 * sku_id
	 */
	@ApiModelProperty("sku_id")
	private Long skuId;
	/**
	 * attr_id
	 */
	@ApiModelProperty("attr_id")
	private Long attrId;
	/**
	 * 销售属性名
	 */
	@ApiModelProperty("销售属性名")
	private String attrName;
	/**
	 * 销售属性值
	 */
	@ApiModelProperty("销售属性值")
	private String attrValue;
	/**
	 * 顺序
	 */
	@ApiModelProperty("顺序")
	private Integer attrSort;

}
