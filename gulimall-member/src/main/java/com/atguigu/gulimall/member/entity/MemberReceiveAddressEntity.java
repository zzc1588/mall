package com.atguigu.gulimall.member.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 会员收货地址
 * 
 * @author zzc
 * @email 786614275@qq.com
 * @date 2022-04-28 11:34:48
 */
@Data
@TableName("ums_member_receive_address")
public class MemberReceiveAddressEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	@TableId
	private Long id;
	/**
	 * member_id
	 */
	private Long memberId;
	/**
	 * 收货人姓名
	 */
	@NotBlank
	private String name;
	/**
	 * 电话
	 */
	@NotBlank
	private String phone;
	/**
	 * 邮政编码
	 */
	private String postCode;
	/**
	 * 省份/直辖市
	 */
	@NotBlank
	private String province;
	/**
	 * 城市
	 */
	@NotBlank
	private String city;
	/**
	 * 区
	 */
	@NotBlank
	private String region;
	/**
	 * 详细地址(街道)
	 */
	@NotBlank
	private String detailAddress;
	/**
	 * 省市区代码
	 */
	private String areacode;
	/**
	 * 是否默认
	 */
	@NotNull @Min(0) @Max(1)
	private Integer defaultStatus;

}
