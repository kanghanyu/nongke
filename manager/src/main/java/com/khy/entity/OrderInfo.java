package com.khy.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class OrderInfo implements Serializable {
	private Long id;

	private String orderId;

	/** 1:标识vip 购买 2:标识点卡购买 3:标识话费充值 4:标识购物内容 */
	private Integer orderType;

	/** 1:标识点卡 2标识 支付宝 3标识微信 */
	private Integer payType;

	private String uid;
	/** 总金额 */
	private BigDecimal totalMoney;

	private Float discount;

	private String discountDetail;

	private BigDecimal discountMoney;
	/**总的付款金额*/
	private BigDecimal totalPay;
	/**其中微信/支付宝/点卡的金额*/
	private BigDecimal rmb;
	/**余额抵扣的钱*/
	private BigDecimal cornMoney;
	
	private String producDetail;

	/** 1未付款,2 付款中 3付款完成 4待收货   */
	private Integer status;

	private Date createTime;

	private Date payTime;


	// 邮费
	private BigDecimal postage;

	private String userName;

	private String address;

	private String postCode;

	private String phone;

	private String description;
	private static final long serialVersionUID = 1L;

	public Long getId() {
		return id;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	public void setId(Long id) {
		this.id = id;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId == null ? null : orderId.trim();
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

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid == null ? null : uid.trim();
	}

	public BigDecimal getTotalMoney() {
		return totalMoney;
	}

	public void setTotalMoney(BigDecimal totalMoney) {
		this.totalMoney = totalMoney;
	}

	public Float getDiscount() {
		return discount;
	}

	public void setDiscount(Float discount) {
		this.discount = discount;
	}

	public String getDiscountDetail() {
		return discountDetail;
	}

	public void setDiscountDetail(String discountDetail) {
		this.discountDetail = discountDetail == null ? null : discountDetail.trim();
	}

	public BigDecimal getDiscountMoney() {
		return discountMoney;
	}

	public void setDiscountMoney(BigDecimal discountMoney) {
		this.discountMoney = discountMoney;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getPayTime() {
		return payTime;
	}

	public void setPayTime(Date payTime) {
		this.payTime = payTime;
	}

	public String getProducDetail() {
		return producDetail;
	}

	public void setProducDetail(String producDetail) {
		this.producDetail = producDetail == null ? null : producDetail.trim();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName());
		sb.append(" [");
		sb.append("Hash = ").append(hashCode());
		sb.append(", id=").append(id);
		sb.append(", orderId=").append(orderId);
		sb.append(", orderType=").append(orderType);
		sb.append(", payType=").append(payType);
		sb.append(", uid=").append(uid);
		sb.append(", totalMoney=").append(totalMoney);
		sb.append(", discount=").append(discount);
		sb.append(", discountDetail=").append(discountDetail);
		sb.append(", discountMoney=").append(discountMoney);
		sb.append(", status=").append(status);
		sb.append(", createTime=").append(createTime);
		sb.append(", payTime=").append(payTime);
		sb.append(", producDetail=").append(producDetail);
		sb.append(", serialVersionUID=").append(serialVersionUID);
		sb.append("]");
		return sb.toString();
	}
}