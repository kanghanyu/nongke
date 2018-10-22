package com.khy.mapper.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import io.swagger.annotations.ApiModelProperty;

public class SubmitOrderResultDTO implements Serializable {

	private static final long serialVersionUID = -3979681842565440226L;

	@ApiModelProperty(value="前置订单id")
	private String orderId;

	@ApiModelProperty(value="1:标识点卡 2标识 支付宝 3标识微信")
	private Integer payType;

	@ApiModelProperty(value="uid")
	private String uid;
	
	@ApiModelProperty(value=" 商品未设置折扣之前的总金额")
	private BigDecimal totalMoney;

	@ApiModelProperty(value="总的付款金额")
	private BigDecimal totalPay;
	
	@ApiModelProperty(value="其中微信/支付宝/点卡的金额")
	private BigDecimal rmb;
	
	@ApiModelProperty(value="余额抵扣的钱")
	private BigDecimal cornMoney;
	
	@ApiModelProperty(value="商品集合的描述内容")
	private List<PayProductDetailDTO> list;
	
	@ApiModelProperty(value="该订单的邮费")
	private BigDecimal postage;
	 
	@ApiModelProperty(value="支付的sign内容")
	private String paySign;
	
	//如果是点卡支付/或者余额全部抵扣支付成功的1:如果需要调用sdk支付的
	private Integer flag;
	
	public String getPaySign() {
		return paySign;
	}
	public void setPaySign(String paySign) {
		this.paySign = paySign;
	}
}
