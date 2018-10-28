package com.khy.mapper.dto;

import java.math.BigDecimal;
import java.util.Date;

import io.swagger.annotations.ApiModelProperty;

public class UserPhoneRecord {
	
	@ApiModelProperty(value="充值金额")
	private BigDecimal totalMoney;
	
	@ApiModelProperty(value="折后金额")
	private BigDecimal discountMoney;

	@ApiModelProperty(value="提交时间")
	private Date createTime;
	
	@ApiModelProperty(value="充值号码")
	private String phone;
	
	@ApiModelProperty(value="状态")
	private String status;

	public BigDecimal getTotalMoney() {
		return totalMoney;
	}

	public BigDecimal getDiscountMoney() {
		return discountMoney;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public String getPhone() {
		return phone;
	}

	public String getStatus() {
		return status;
	}

	public void setTotalMoney(BigDecimal totalMoney) {
		this.totalMoney = totalMoney;
	}

	public void setDiscountMoney(BigDecimal discountMoney) {
		this.discountMoney = discountMoney;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
