package com.khy.mapper.dto;

import java.util.List;

import com.khy.entity.UserAddress;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
@ApiModel(value = "用户的收货地址列表对象", description = "存放用户收货地址的详情集合")
public class UserAddressListDTO {
	
	@ApiModelProperty(value="uid")
	 private String uid;
	@ApiModelProperty(value="收货地址对象")
	 List<UserAddress>list;
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public List<UserAddress> getList() {
		return list;
	}
	public void setList(List<UserAddress> list) {
		this.list = list;
	}
}
