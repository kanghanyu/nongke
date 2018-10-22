package com.khy.mapper.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import io.swagger.annotations.ApiModelProperty;

public class RechargeSubmitDTO implements Serializable{
	
	private static final long serialVersionUID = 5523355172851945415L;

	@ApiModelProperty(value="1:标识vip 购买 2:标识点卡购买 3:标识话费充值 ")
	private Integer orderType;
	
	@ApiModelProperty(value="1:标识点卡 2:标识余额(针对点卡)  3标识 支付宝 4标识微信")
	private Integer payType;
	
	@ApiModelProperty(value="话费充值金额 50 / 100  或者 购买点卡的金额 100元  vip 的价格 ")
	private BigDecimal totalMoney;
	
	@ApiModelProperty(value="付款的总额")
	private BigDecimal totalPay;

	public Integer getOrderType() {
		return orderType;
	}

	public void setOrderType(Integer orderType) {
		this.orderType = orderType;
	}

	public Integer getPayType() {
		return payType;
	}

	public void setPayType(Integer payType) {
		this.payType = payType;
	}

	public BigDecimal getTotalMoney() {
		return totalMoney;
	}

	public void setTotalMoney(BigDecimal totalMoney) {
		this.totalMoney = totalMoney;
	}

	public BigDecimal getTotalPay() {
		return totalPay;
	}

	public void setTotalPay(BigDecimal totalPay) {
		this.totalPay = totalPay;
	}
	

}
