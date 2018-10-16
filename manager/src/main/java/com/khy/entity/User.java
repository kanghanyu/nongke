package com.khy.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
public class User extends BaseEntity implements Serializable{
	private static final long serialVersionUID = -3138377795422632271L;
	/**主键id*/
	@ApiModelProperty(value="主键标识id")
	private Long id;
	/**uid标识*/
	@ApiModelProperty(value="uid")
	private String uid;
	/**手机号*/
	@ApiModelProperty(value="手机号")
	private String phone;
	/**密码*/
	@ApiModelProperty(value="密码")
	private String password;
	/**注册时间*/
	@ApiModelProperty(value="注册时间")
	private Date createTime;
	/**人民币 单位:元 */
	@ApiModelProperty(value="余额 单位:元")
	private BigDecimal money;
	/**二维码链接*/
	@ApiModelProperty(value="二维码链接")
	private String imgUrl;
	/**邀请人的uid*/
	@ApiModelProperty(value="邀请人的uid")
	private String inviterUid;
	/**邀请人的手机号*/
	@ApiModelProperty(value="邀请人的手机号")
	private String inviterPhone;
	/**是否是管理员  0 :普通用户 1:管理员*/
	@ApiModelProperty(value="是否是管理员  0 :普通用户 1:管理员")
	private Integer isManager;
	/**是否是管理员  0 :普通用户 1:vip*/
	@ApiModelProperty(value="是否是管理员  0 :普通用户 1:vip")
	private Integer isVip;
	@ApiModelProperty(value="0:标识正常状态 1:标识冻结")
	private Integer status;
	
	@ApiModelProperty(value="确认密码")
	private String confirmPassword;
	@ApiModelProperty(value="验证码")
	private String code;
	
	@ApiModelProperty(value="头像")
	private String img;
	
	@ApiModelProperty(value="点卡")
	private BigDecimal cardMoney;
	
	@ApiModelProperty(value="佣金")
	private BigDecimal commission;
	
	
	/**登录返回的token值*/
	@ApiModelProperty(value="登录返回的token值")
	private String token;
	
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public BigDecimal getCardMoney() {
		return cardMoney;
	}
	public String getImg() {
		return img;
	}
	public void setImg(String img) {
		this.img = img;
	}
	public void setCardMoney(BigDecimal cardMoney) {
		this.cardMoney = cardMoney;
	}
	public BigDecimal getCommission() {
		return commission;
	}
	public void setCommission(BigDecimal commission) {
		this.commission = commission;
	}
	public String getConfirmPassword() {
		return confirmPassword;
	}
	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public Integer getIsManager() {
		return isManager;
	}
	public void setIsManager(Integer isManager) {
		this.isManager = isManager;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public Integer getIsVip() {
		return isVip;
	}
	public void setIsVip(Integer isVip) {
		this.isVip = isVip;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public BigDecimal getMoney() {
		return money;
	}
	public void setMoney(BigDecimal money) {
		this.money = money;
	}
	public String getImgUrl() {
		return imgUrl;
	}
	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}
	public String getInviterUid() {
		return inviterUid;
	}
	public void setInviterUid(String inviterUid) {
		this.inviterUid = inviterUid;
	}
	public String getInviterPhone() {
		return inviterPhone;
	}
	public void setInviterPhone(String inviterPhone) {
		this.inviterPhone = inviterPhone;
	}
	
	public void hide(User user){
		user.setPassword(null);
	}
	public static User getUser(){
		return new User();
	}
}
