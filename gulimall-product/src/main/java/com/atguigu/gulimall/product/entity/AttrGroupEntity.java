package com.atguigu.gulimall.product.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * 属性分组
 * 
 * @author zzc
 * @email 786614275@qq.com
 * @date 2022-04-28 10:26:08
 */
@Data
@TableName("pms_attr_group")
@ApiModel("属性分组entity")
@ToString
public class AttrGroupEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 分组id
	 */
	@TableId
	@ApiModelProperty("分组id")
	private Long attrGroupId;
	/**
	 * 组名
	 */
	@ApiModelProperty("组名")
	private String attrGroupName;
	/**
	 * 排序
	 */
	@ApiModelProperty("排序")
	private Integer sort;
	/**
	 * 描述
	 */
	@ApiModelProperty("描述")
	private String descript;
	/**
	 * 组图标
	 */
	@ApiModelProperty("组图标")
	private String icon;
	/**
	 * 所属分类id
	 */
	@ApiModelProperty("所属分类id")
	private Long catelogId;
	/**
	 * 完整路径[1,13,131]
	 */
	//标记数据库不存在该字段
	@TableField(exist = false)
	@ApiModelProperty("完整路径[1,13,131]")
	private Long[] catelogPath;
}
