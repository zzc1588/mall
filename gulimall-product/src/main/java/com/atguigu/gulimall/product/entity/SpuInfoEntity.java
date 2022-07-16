package com.atguigu.gulimall.product.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * spu信息
 * 
 * @author zzc
 * @email 786614275@qq.com
 * @date 2022-04-28 10:26:08
 */
@Data
@TableName("pms_spu_info")
@ApiModel("Spu详情entity")
public class SpuInfoEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 商品id
	 */
	@TableId
	@ApiModelProperty("商品id")
	private Long id;
	/**
	 * 商品名称
	 */
	@ApiModelProperty("商品名称")
	private String spuName;
	/**
	 * 商品描述
	 */
	@ApiModelProperty("商品描述")
	private String spuDescription;
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
	 * 商品重量
	 */
	@ApiModelProperty("商品重量")
	private BigDecimal weight;
	/**
	 * 上架状态[0 - 下架，1 - 上架]
	 */
	@ApiModelProperty("上架状态[0 - 下架，1 - 上架]")
	private Integer publishStatus;
	/**
	 * 创建时间
	 */
	@ApiModelProperty("创建时间")
	@TableField(fill = FieldFill.INSERT)
	private Date createTime;
	/**
	 * 更新时间
	 */
	@ApiModelProperty("更新时间")
	@TableField(fill = FieldFill.INSERT_UPDATE)
	private Date updateTime;

}
