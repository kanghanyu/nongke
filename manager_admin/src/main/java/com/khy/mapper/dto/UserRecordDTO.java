package com.khy.mapper.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class UserRecordDTO implements Serializable {
	private static final long serialVersionUID = -5406697925224175744L;
	
	private String uid;
	
	private Integer type;
	
	private BigDecimal amount;
	
    private Integer payType;

	private String description;

	private Date createTime;

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public Integer getPayType() {
		return payType;
	}

	public void setPayType(Integer payType) {
		this.payType = payType;
		if(null != payType && null != amount){
			this.amount = payType ==1 ? amount:amount.negate();
		}
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
		if(null != payType && null != amount){
			this.amount = payType ==1 ? amount:amount.negate();
		}
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
}
