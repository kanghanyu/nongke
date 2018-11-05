package com.khy.mapper.dto;

import java.io.Serializable;
import java.util.Date;

import io.swagger.annotations.ApiModelProperty;

public class UserBillDTO implements Serializable{

	private static final long serialVersionUID = 5812596389552522123L;

	@ApiModelProperty(value="uid")
	private String uid;
	
	@ApiModelProperty(value="时间")
	private Date time;
	
	@ApiModelProperty(value="描述")
	private String description;
	
	@ApiModelProperty(value="金额")
	private String totalDesc;

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTotalDesc() {
		return totalDesc;
	}

	public void setTotalDesc(String totalDesc) {
		this.totalDesc = totalDesc;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
}
