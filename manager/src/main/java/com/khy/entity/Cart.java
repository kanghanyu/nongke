package com.khy.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
public class Cart extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	private Long id;
	/**
	 * 用户的uid
	 */
	private String uid;

	@ApiModelProperty(value="商品的id")
    private Long productId;

     
	@ApiModelProperty(value=" 商品名称")
    private String productName;
   
	@ApiModelProperty(value="商品价格")
    private BigDecimal productPrice;

	@ApiModelProperty(value="缩略图片")
    private String img;
    
	@ApiModelProperty(value="购买数量")
    private Integer amount;
	
	@ApiModelProperty(value="时间")
	private Date createTime;

	@ApiModelProperty(value="0 标识没添加到订单 1:标识添加到订单")
	private Integer status;
	
	private Double total;
	public Double getTotal() {
		return total;
	}

	public void setTotal(Double total) {
		this.total = total;
	}

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

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public BigDecimal getProductPrice() {
		return productPrice;
	}

	public void setProductPrice(BigDecimal productPrice) {
		this.productPrice = productPrice;
		if(null != productPrice && null != amount ){
			this.total = productPrice.multiply(new BigDecimal(amount)).doubleValue();
		}
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public Integer getAmount() {
		return amount;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
		if(null != productPrice && null != amount ){
			this.total = productPrice.multiply(new BigDecimal(amount)).doubleValue();
		}
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
	
}