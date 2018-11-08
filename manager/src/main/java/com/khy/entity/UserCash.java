package com.khy.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import io.swagger.annotations.ApiModelProperty;

public class UserCash implements Serializable {
    private Long id;

    private String uid;

    @ApiModelProperty(value="体现金额")
    private BigDecimal amount;

    //手续费
    private BigDecimal feeAmount;

    //真实体现金额
    private BigDecimal realAmount;
    
    @ApiModelProperty(value="银行名称")
    private String bankName;

	@ApiModelProperty(value="银行卡号")
    private String bankNum;

	@ApiModelProperty(value="户主姓名")
    private String userName;

	@ApiModelProperty(value="支行地址")
    private String bankAdress;

	@ApiModelProperty(value="联系电话")
    private String phone;
	
	@ApiModelProperty(value="申请时间")
    private Date applyTime;

	@ApiModelProperty(value="0 申请提现  1提现完成")
    private Integer status;

    private Date updateTime;

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
        this.uid = uid == null ? null : uid.trim();
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getFeeAmount() {
        return feeAmount;
    }

    public void setFeeAmount(BigDecimal feeAmount) {
        this.feeAmount = feeAmount;
    }

    public BigDecimal getRealAmount() {
        return realAmount;
    }

    public void setRealAmount(BigDecimal realAmount) {
        this.realAmount = realAmount;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName == null ? null : bankName.trim();
    }

    public String getBankNum() {
        return bankNum;
    }

    public void setBankNum(String bankNum) {
        this.bankNum = bankNum == null ? null : bankNum.trim();
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName == null ? null : userName.trim();
    }

    public String getBankAdress() {
        return bankAdress;
    }

    public void setBankAdress(String bankAdress) {
        this.bankAdress = bankAdress == null ? null : bankAdress.trim();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
    }

    public Date getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(Date applyTime) {
        this.applyTime = applyTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", uid=").append(uid);
        sb.append(", amount=").append(amount);
        sb.append(", feeAmount=").append(feeAmount);
        sb.append(", realAmount=").append(realAmount);
        sb.append(", bankName=").append(bankName);
        sb.append(", bankNum=").append(bankNum);
        sb.append(", userName=").append(userName);
        sb.append(", bankAdress=").append(bankAdress);
        sb.append(", phone=").append(phone);
        sb.append(", applyTime=").append(applyTime);
        sb.append(", status=").append(status);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}