package com.khy.mapper.dto;

import java.math.BigDecimal;

import io.swagger.annotations.ApiModelProperty;

public class CartMoneyDTO {

	@ApiModelProperty(value="转账的对方账户")
	private String phone;
	@ApiModelProperty(value="转账的数量")
	private BigDecimal amount;
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	
}
