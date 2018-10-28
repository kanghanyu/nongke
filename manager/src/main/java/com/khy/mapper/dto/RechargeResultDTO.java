package com.khy.mapper.dto;

import java.math.BigDecimal;

import io.swagger.annotations.ApiModelProperty;

public class RechargeResultDTO {

	@ApiModelProperty(value="订单id")
	private String orderId;
	
	@ApiModelProperty(value="描述内容")
	private String body;
	
	@ApiModelProperty(value="1:标识vip 购买 2:标识点卡购买 3:标识话费充值 ")
	private Integer orderType;
	
	@ApiModelProperty(value="1:标识点卡 2:标识余额(针对点卡)  3标识 支付宝 4标识微信")
	private Integer payType;
	
	@ApiModelProperty(value="话费充值金额 50 / 100  或者 购买点卡的金额 100元  vip 的价格 ")
	private BigDecimal totalMoney;
	
	@ApiModelProperty(value="付款的总额")
	private BigDecimal totalPay;
	
	@ApiModelProperty(value="支付的sign内容")
	private String paySign;
	
	@ApiModelProperty(value="1:标识余额扣除成功 2 标识支付宝/微信")
	private Integer flag;
	public Integer getFlag() {
		return flag;
	}

	public void setFlag(Integer flag) {
		this.flag = flag;
	}

	public String getPaySign() {
		return paySign;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public void setPaySign(String paySign) {
		this.paySign = paySign;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

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

	public String getSubject(){
		String ret = "商品消费";
		if(null != orderType){
			if(orderType == 1){
				ret = "升级成为VIP";
			}else if(orderType == 2){
				ret = "购买点卡";
			}else if(orderType == 3){
				ret = "手机话费充值";
			}else if(orderType == 4){
				ret = "购买商城物品";
			}
		}
		return ret;
	}
	
}
