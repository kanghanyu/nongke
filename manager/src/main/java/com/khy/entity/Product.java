package com.khy.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "响应的product对象", description = "商品相关的内容")
public class Product extends BaseEntity implements Serializable {

	private static final long serialVersionUID = 9028604204908341993L;

	@ApiModelProperty(value="商品的id")
	private Long productId;

	@ApiModelProperty(value="商品名称")
	private String productName;

	@ApiModelProperty(value="价格")
	private BigDecimal productPrice;
	
	//进价
	private BigDecimal costPrice;

	@ApiModelProperty(value="库存数量")
	private Integer stockAmount;

	@ApiModelProperty(value=" 销售数量")
	private Integer salesAmount;

	@ApiModelProperty(value=" 封面图片多张按顺序,隔开")
	private String coverImg;

	@ApiModelProperty(value="详情文字描述")
	private String detail;

	@ApiModelProperty(value="详情的图片多张,隔开")
	private String detailImgs;
	
	@ApiModelProperty(value="0:未上架 ;1:上架,2已下架")
	private Integer status;

	@ApiModelProperty(value="创建时间")
	private Date createTime;

	@ApiModelProperty(value="缩略图")
	private String img;
	
	/**0:标识普通 1:标识hot*/
	private Integer isHot;
	
	public Integer getIsHot() {
		return isHot;
	}

	public void setIsHot(Integer isHot) {
		this.isHot = isHot;
	}

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}
	public BigDecimal getCostPrice() {
		return costPrice;
	}

	public void setCostPrice(BigDecimal costPrice) {
		this.costPrice = costPrice;
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
	}

	public Integer getStockAmount() {
		return stockAmount;
	}

	public void setStockAmount(Integer stockAmount) {
		this.stockAmount = stockAmount;
	}

	public Integer getSalesAmount() {
		return salesAmount;
	}

	public void setSalesAmount(Integer salesAmount) {
		this.salesAmount = salesAmount;
	}

	public String getCoverImg() {
		return coverImg;
	}

	public void setCoverImg(String coverImg) {
		this.coverImg = coverImg;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getDetailImgs() {
		return detailImgs;
	}

	public void setDetailImgs(String detailImgs) {
		this.detailImgs = detailImgs;
	}

	public String getImg() {
		return img;
	}
	public void setImg(String img) {
		this.img = img;
	}
}
