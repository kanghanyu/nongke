package com.khy.mapper.dto;

import java.io.Serializable;

import com.khy.entity.BaseEntity;

public class UserCommonDTO extends BaseEntity implements Serializable{

	private static final long serialVersionUID = -1608424265164756308L;
	private Long id;
	private String uid;
	private String phone;
	private Integer status;
	private Integer type;
	public String getUid() {
		return uid;
	}
	public String getPhone() {
		return phone;
	}
	
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Integer getStatus() {
		return status;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}

}