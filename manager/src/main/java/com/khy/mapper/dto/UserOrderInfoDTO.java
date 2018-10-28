package com.khy.mapper.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONArray;

import io.swagger.annotations.ApiModelProperty;

public class UserOrderInfoDTO implements Serializable{

	private static final long serialVersionUID = -3452886345205097080L;

	@ApiModelProperty(value="订单id")
	private String orderId;

	@ApiModelProperty(value="1:标识点卡 2标识 支付宝 3标识微信")
	private Integer payType;
	
	@ApiModelProperty(value="uid")
	private String uid;
	
	/** 总金额 */
	@ApiModelProperty(value="折扣前的商品总金额")
	private BigDecimal totalMoney;
	
	@ApiModelProperty(value="折扣")
	private Float discount;
	
	@ApiModelProperty(value="折扣后的金额")
	private BigDecimal discountMoney;
	
	@ApiModelProperty(value="总的付款金额")
	private BigDecimal totalPay;
	
	@ApiModelProperty(value="付款的rmb/点卡金额")
	private BigDecimal rmb;
	
	@ApiModelProperty(value="余额抵扣的金额")
	private BigDecimal cornMoney;
	
	@ApiModelProperty(value="订单状态描述0:无效订单 1:未付款 2:已付款3:交易成功4:订单已取消")
	private Integer statusDesc;
	
	@ApiModelProperty(value="详情商品的内容")
	private List<UserOrderProductDTO>products;
	
	@ApiModelProperty(value="1订单未完成 2:订单完成 ")
	private Integer status;
	
	@ApiModelProperty(value="付款状态 1未付款,2已付款 3:已取消 4: 佣金已经返还")
	private Integer payStatus;
	
	@ApiModelProperty(value="订单创建时间")
	private Date createTime;
	
	@ApiModelProperty(value="订单的付款时间")
	private Date payTime;
	
	@ApiModelProperty(value="邮费")
	private BigDecimal postage;
	
	@ApiModelProperty(value="收件人姓名")
	private String userName;

	@ApiModelProperty(value="收件人地址")
	private String address;

	@ApiModelProperty(value="收件人邮编")
	private String postCode;

	@ApiModelProperty(value="收件人手机号")
	private String phone;

	@ApiModelProperty(value="描述内容")
	private String description;

	public String getOrderId() {
		return orderId;
	}

	public Integer getPayType() {
		return payType;
	}

	public String getUid() {
		return uid;
	}
	public Integer getStatusDesc() {
		return statusDesc;
	}

	public void setStatusDesc(Integer statusDesc) {
		this.statusDesc = statusDesc;
	}

	public BigDecimal getTotalMoney() {
		return totalMoney;
	}

	public Float getDiscount() {
		return discount;
	}

	public BigDecimal getDiscountMoney() {
		return discountMoney;
	}

	public BigDecimal getTotalPay() {
		return totalPay;
	}

	public BigDecimal getRmb() {
		return rmb;
	}

	public BigDecimal getCornMoney() {
		return cornMoney;
	}

	public List<UserOrderProductDTO> getProducts() {
		return products;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public Date getPayTime() {
		return payTime;
	}

	public BigDecimal getPostage() {
		return postage;
	}

	public String getUserName() {
		return userName;
	}

	public String getAddress() {
		return address;
	}

	public String getPostCode() {
		return postCode;
	}

	public String getPhone() {
		return phone;
	}

	public String getDescription() {
		return description;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public void setPayType(Integer payType) {
		this.payType = payType;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public void setTotalMoney(BigDecimal totalMoney) {
		this.totalMoney = totalMoney;
	}

	public void setDiscount(Float discount) {
		this.discount = discount;
	}

	public void setDiscountMoney(BigDecimal discountMoney) {
		this.discountMoney = discountMoney;
	}

	public void setTotalPay(BigDecimal totalPay) {
		this.totalPay = totalPay;
	}

	public void setRmb(BigDecimal rmb) {
		this.rmb = rmb;
	}

	public void setCornMoney(BigDecimal cornMoney) {
		this.cornMoney = cornMoney;
	}

	public void setProducts(List<UserOrderProductDTO> products) {
		this.products = products;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public void setPayTime(Date payTime) {
		this.payTime = payTime;
	}

	public void setPostage(BigDecimal postage) {
		this.postage = postage;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
}
