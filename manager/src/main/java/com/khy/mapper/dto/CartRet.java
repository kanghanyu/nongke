package com.khy.mapper.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import com.khy.entity.Cart;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
@ApiModel(value = "响应的购物车对象", description = "购物车商品相关的内容")
public class CartRet implements Serializable{

	private static final long serialVersionUID = 1924380102858596983L;

	@ApiModelProperty(value="用户的uid")
	private String uid;
	
	@ApiModelProperty(value="总金额")
	private Double totalPrice;
	
	@ApiModelProperty(value="购物车的商品列表")
	private List<Cart> list;

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public Double getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(Double totalPrice) {
		this.totalPrice = totalPrice;
	}

	public List<Cart> getList() {
		return list;
	}

	public void setList(List<Cart> list) {
		this.list = list;
	}
}
