package com.atguigu.gulimall.product.entity;

import com.atguigu.common.valid.ListValue;
import com.atguigu.common.valid.UpdateStatusGroup;
import com.atguigu.common.validator.group.AddGroup;
import com.atguigu.common.validator.group.UpdateGroup;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.*;


/**
 * 品牌
 * 
 * @author zzc
 * @email 786614275@qq.com
 * @date 2022-04-28 10:26:08
 */
@Data
@TableName("pms_brand")
@ApiModel("品牌entity")

public class BrandEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 品牌id
	 */
	@NotNull(message = "修改必须指定品牌id",groups = {UpdateGroup.class})
	@Null(message = "新增数据不能指定id",groups = {AddGroup.class})
	@TableId
	@ApiModelProperty("品牌id")
	private Long brandId;
	/**
	 * 品牌名
	 */
	@ApiModelProperty("品牌名")
	@NotBlank(message = "品牌名必须提交",groups = {AddGroup.class,UpdateGroup.class})
	private String name;
	/**
	 * 品牌logo地址
	 */
	@ApiModelProperty("品牌logo地址")
	@NotBlank(groups = {AddGroup.class})
	@URL(message = "logo必须是一个合法的url",groups = {AddGroup.class,UpdateGroup.class})
	private String logo;
	/**
	 * 介绍
	 */
	@ApiModelProperty("介绍")
	private String descript;
	/**
	 * 显示状态[0-不显示；1-显示]
	 */
	@ApiModelProperty("显示状态[0-不显示；1-显示]")
	@NotNull(groups = {AddGroup.class,UpdateStatusGroup.class})
//	@Min(value = 0,groups = {UpdateStatusGroup.class,AddGroup.class},message = "showStatus必须为数字")
	@ListValue(vals = {0,1},groups = {AddGroup.class, UpdateStatusGroup.class},message="showStatus必须为0或1")
//	@Pattern(regexp = "[/d]+",groups = {AddGroup.class, UpdateStatusGroup.class},message="showStatus必须为0或1")
	private Integer showStatus;
	/**
	 * 检索首字母
	 */
	@ApiModelProperty("检索首字母")
	@NotEmpty(groups = {AddGroup.class})
	@Pattern(regexp = "^[a-zA-Z]$",message = "检索首字母必须是一个字母",groups = {AddGroup.class, UpdateGroup.class})
	private String firstLetter;
	/**
	 * 排序
	 */
	@ApiModelProperty("排序")
	@NotNull
	@Min(value = 0,message = "排序必须大于0",groups = {AddGroup.class,UpdateGroup.class})
	private Integer sort;

}
