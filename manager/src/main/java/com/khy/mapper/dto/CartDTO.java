package com.khy.mapper.dto;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;

public class CartDTO implements Serializable{

	private static final long serialVersionUID = 1924380102858596983L;

	@ApiModelProperty(value="商品标识")
    private Long productId;

	@ApiModelProperty(value="添加到购物车/更新购物车中/提交生成前置订单  商品的数量")
    private Integer amount;
	public Long getProductId() {
		return productId;
	}
	public void setProductId(Long productId) {
		this.productId = productId;
	}
	public Integer getAmount() {
		return amount;
	}
	public void setAmount(Integer amount) {
		this.amount = amount;
	}
    
}
