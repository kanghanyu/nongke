package com.khy.mapper.dto;

import java.io.Serializable;
import java.util.List;

import io.swagger.annotations.ApiModelProperty;

public class PreOrderDTO implements Serializable{

	private static final long serialVersionUID = -1486608373319583485L;
	@ApiModelProperty(value="购物车中提交生成前置订单的商品集合")
	private List<CartDTO> list ;
	public List<CartDTO> getList() {
		return list;
	}
	public void setList(List<CartDTO> list) {
		this.list = list;
	}
    
}
