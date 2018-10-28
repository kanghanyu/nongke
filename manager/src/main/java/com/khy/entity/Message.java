package com.khy.entity;

import java.util.Date;

import com.khy.utils.Utils;

import io.swagger.annotations.ApiModelProperty;

public class Message{
	private Long id;
	@ApiModelProperty(value="公告的标题")
	private String title;
	@ApiModelProperty(value="公告的内容")
	private String content;
	@ApiModelProperty(value="公告的时间")
	private Date createTime;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
}
