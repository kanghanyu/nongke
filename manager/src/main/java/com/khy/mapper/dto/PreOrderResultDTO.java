package com.khy.mapper.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.swagger.annotations.ApiModelProperty;

public class PreOrderResultDTO implements Serializable {

	private static final long serialVersionUID = -3979681842565440226L;

	private Long id;

	@ApiModelProperty(value="前置订单id")
	private String orderId;

	@ApiModelProperty(value="1:标识vip 购买 2:标识点卡购买 3:标识话费充值 4:标识购物内容")
    private Integer orderType;
    
	@ApiModelProperty(value="uid")
	private String uid;

	@ApiModelProperty(value="总金额没有折扣的")
	private BigDecimal totalMoney;

	@ApiModelProperty(value="折扣88折--> 0.88")
	private Float discount;

	@ApiModelProperty(value="折扣的描述内容")
	private String discountDetail;

	@ApiModelProperty(value="折扣后的总金额")
	private BigDecimal discountMoney;

	@ApiModelProperty(value="时间")
	private Date createTime;

	private String productDetail;
	
	@ApiModelProperty(value="折扣后的点卡的总金额")
	private BigDecimal totalCardMoney;
	
	@ApiModelProperty(value="商品集合的描述内容")
	private List<PayProductDetailDTO> list;
	
	@ApiModelProperty(value="该订单的邮费")
	private BigDecimal postage;
	
	public Long getId() {
		return id;
	}
	public List<PayProductDetailDTO> getList() {
		return list;
	}
	public BigDecimal getTotalCardMoney() {
		return totalCardMoney;
	}
	public Integer getOrderType() {
		return orderType;
	}
	public void setOrderType(Integer orderType) {
		this.orderType = orderType;
	}
	public void setTotalCardMoney(BigDecimal totalCardMoney) {
		this.totalCardMoney = totalCardMoney;
	}
	public void setList(List<PayProductDetailDTO> list) {
		this.list = list;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public BigDecimal getPostage() {
		return postage;
	}
	public void setPostage(BigDecimal postage) {
		this.postage = postage;
	}
	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
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
		this.discountDetail = discountDetail;
	}

	public BigDecimal getDiscountMoney() {
		return discountMoney;
	}

	public void setDiscountMoney(BigDecimal discountMoney) {
		this.discountMoney = discountMoney;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getProductDetail() {
		return productDetail;
	}

	public void setProductDetail(String productDetail) {
		this.productDetail = productDetail;
	}
	
	public static void main(String[] args) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		System.out.println(sdf.format(new Date()));
		Calendar calendar = Calendar.getInstance();
		
		System.out.println(sdf.format(calendar.getTime()));
		calendar.add(Calendar.MINUTE, 30);
		System.out.println(sdf.format(calendar.getTime()));
		
		
	}
}
