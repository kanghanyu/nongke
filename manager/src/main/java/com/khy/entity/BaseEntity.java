package com.khy.entity;

import io.swagger.annotations.ApiModelProperty;

public class BaseEntity {

	@ApiModelProperty(value="当前页")
	private int pageNum = 1;
	@ApiModelProperty(value="当前页记录")
	private int pageSize = 20;
	public int getPageNum() {
		return pageNum;
	}
	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	
}
