package com.khy.mapper.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import io.swagger.annotations.ApiModelProperty;

public class SubmitOrderDTO implements Serializable {

	private static final long serialVersionUID = -3979681842565440226L;

	@ApiModelProperty(value="前置订单id")
	private String orderId;

	@ApiModelProperty(value="1:标识点卡 2标识 支付宝 3标识微信")
	private Integer payType;
	
	@ApiModelProperty(value="1:标识vip 购买 2:标识点卡购买 3:标识话费充值 4:标识购物内容 ")
	private Integer orderType;
	
	@ApiModelProperty(value="uid")
	private String uid;
	
	@ApiModelProperty(value="总的付款金额(商品和运费的和)")
	private BigDecimal totalPay;
	
	@ApiModelProperty(value="其中微信/支付宝/点卡的金额")
	private BigDecimal rmb;
	
	@ApiModelProperty(value="余额抵扣的钱")
	private BigDecimal cornMoney;
	
	@ApiModelProperty(value="商品集合的描述内容")
	private List<PayProductDetailDTO> list;
	
	@ApiModelProperty(value="该订单的邮费")
	private BigDecimal postage;
	
	@ApiModelProperty(value="该订单的收件人")
	private String userName;

	@ApiModelProperty(value="该订单的收件人地址")
	private String address;

	@ApiModelProperty(value="该订单的收件人邮编")
	private String postCode;

	@ApiModelProperty(value="该订单的收件人手机号")
	private String phone;
	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public Integer getPayType() {
		return payType;
	}

	public void setPayType(Integer payType) {
		this.payType = payType;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public Integer getOrderType() {
		return orderType;
	}

	public void setOrderType(Integer orderType) {
		this.orderType = orderType;
	}

	public BigDecimal getTotalPay() {
		return totalPay;
	}

	public void setTotalPay(BigDecimal totalPay) {
		this.totalPay = totalPay;
	}

	public BigDecimal getRmb() {
		return rmb;
	}

	public void setRmb(BigDecimal rmb) {
		this.rmb = rmb;
	}

	public BigDecimal getCornMoney() {
		return cornMoney;
	}

	public void setCornMoney(BigDecimal cornMoney) {
		this.cornMoney = cornMoney;
	}

	public List<PayProductDetailDTO> getList() {
		return list;
	}

	public void setList(List<PayProductDetailDTO> list) {
		this.list = list;
	}

	public BigDecimal getPostage() {
		return postage;
	}

	public void setPostage(BigDecimal postage) {
		this.postage = postage;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPostCode() {
		return postCode;
	}

	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
//	1:标识vip 购买 2:标识点卡购买 3:标识话费充值 4:标识购物内容
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
