package com.atguigu.gulimall.product.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * spu图片
 * 
 * @author zzc
 * @email 786614275@qq.com
 * @date 2022-04-28 10:26:08
 */
@Data
@TableName("pms_spu_images")
@ApiModel("Spu图片entity")

public class SpuImagesEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	@TableId
	@ApiModelProperty("id")
	private Long id;
	/**
	 * spu_id
	 */
	@ApiModelProperty("spu_id")
	private Long spuId;
	/**
	 * 图片名
	 */
	@ApiModelProperty("图片名")
	private String imgName;
	/**
	 * 图片地址
	 */
	@ApiModelProperty("图片地址")
	private String imgUrl;
	/**
	 * 顺序
	 */
	@ApiModelProperty("顺序")
	private Integer imgSort;
	/**
	 * 是否默认图
	 */
	@ApiModelProperty("是否默认图")
	private Integer defaultImg;

}
