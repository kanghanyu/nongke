package com.khy.mapper.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import io.swagger.annotations.ApiModelProperty;

public class UserInviterDTO implements Serializable {
    private Long id;

    @ApiModelProperty(value="当前用户的uid")
    private String uid;
    
    @ApiModelProperty(value="被邀请人的uid")
    private String invitedUid;
    
    @ApiModelProperty(value="被邀请人的手机号")
	private String phone;
    
    @ApiModelProperty(value="被邀请人的头像")
	private String img;
	
    @ApiModelProperty(value="被邀请人的佣金")
	private BigDecimal commission;
    
    @ApiModelProperty(value="会员信息")
    private String isVip;
    
    private Date createTime;

    private static final long serialVersionUID = 1L;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getInvitedUid() {
		return invitedUid;
	}

	public void setInvitedUid(String invitedUid) {
		this.invitedUid = invitedUid;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public BigDecimal getCommission() {
		return commission;
	}

	public void setCommission(BigDecimal commission) {
		this.commission = commission;
	}

	public String getIsVip() {
		return isVip;
	}

	public void setIsVip(String isVip) {
		this.isVip = isVip;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

  
}