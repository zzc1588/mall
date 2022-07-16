package com.atguigu.gulimall.product.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 商品评价回复关系
 * 
 * @author zzc
 * @email 786614275@qq.com
 * @date 2022-04-28 10:26:08
 */
@Data
@TableName("pms_comment_replay")
@ApiModel("商品评价回复关系entity")

public class CommentReplayEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	@ApiModelProperty("id")
	@TableId
	private Long id;
	/**
	 * 评论id
	 */
	@ApiModelProperty("评论id")
	private Long commentId;
	/**
	 * 回复id
	 */
	@ApiModelProperty("回复id")
	private Long replyId;

}
