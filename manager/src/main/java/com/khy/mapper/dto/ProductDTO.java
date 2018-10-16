package com.khy.mapper.dto;

import java.io.Serializable;

import com.khy.entity.BaseEntity;

import io.swagger.annotations.ApiModelProperty;

public class ProductDTO extends BaseEntity implements Serializable{

	private static final long serialVersionUID = 914603195824471399L;
	@ApiModelProperty(value="商品名称")
	private String productName;
	
	@ApiModelProperty(value="排序字段内容  是create_time时间 ,sales_amount 销售数量,product_price 价格")
	private String orderBy;
	
	@ApiModelProperty(value="正序或者倒序 DESC ASC")
	private String orderType;
	
	public String getOrderType() {
		return orderType;
	}
	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getOrderBy() {
		return orderBy;
	}
	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}
	
}
