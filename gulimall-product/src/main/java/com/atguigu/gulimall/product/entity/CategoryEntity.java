package com.atguigu.gulimall.product.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 商品三级分类
 * 
 * @author zzc
 * @email 786614275@qq.com
 * @date 2022-04-28 10:26:08
 */
@Data
@TableName("pms_category")
@ApiModel("商品三级分类entity")

public class CategoryEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 分类id
	 */
	@TableId
	@ApiModelProperty("分类id")
	private Long catId;
	/**
	 * 分类名称
	 */
	@ApiModelProperty("分类名称")
	private String name;
	/**
	 * 父分类id
	 */
	@ApiModelProperty("父分类id")
	private Long parentCid;
	/**
	 * 层级
	 */
	@ApiModelProperty("层级")
	private Integer catLevel;
	/**
	 * 是否显示[0-不显示，1显示]
	 */
	@ApiModelProperty("是否显示[0-不显示，1显示]")
	@TableLogic
	private Integer showStatus;
	/**
	 * 排序
	 */
	@ApiModelProperty("排序")
	private Integer sort;
	/**
	 * 图标地址
	 */
	@ApiModelProperty("图标地址")
	private String icon;
	/**
	 * 计量单位
	 */
	@ApiModelProperty("计量单位")
	private String productUnit;
	/**
	 * 商品数量
	 */
	@ApiModelProperty("商品数量")
	private Integer productCount;

	/**
	**
	 * 商品分类
    **/
	@ApiModelProperty("商品分类")
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	@TableField(exist = false)//表示数据库中不存在该字段
	private List<CategoryEntity> children;

}
