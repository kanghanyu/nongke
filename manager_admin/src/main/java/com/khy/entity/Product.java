package com.khy.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.khy.utils.Utils;

public class Product extends BaseEntity implements Serializable {

	private static final long serialVersionUID = 9028604204908341993L;

	/** 商品的id */
	private Long productId;

	/**
	 * 商品名称
	 */
	private String productName;

	/**
	 * 价格
	 */
	private BigDecimal productPrice;
	
	private String productPriceStr;

	/**初始化商品的总数量*/
	private Integer initAmount;
	/**
	 * 库存数量
	 */
	private Integer stockAmount;

	/**
	 * 销售数量
	 */
	private Integer salesAmount;
	
	/**缩略图*/
	private String img;
	/**
	 * 封面图片多张按顺序,隔开
	 */
	private String coverImg;

	/**
	 * 详情文字描述
	 */
	private String detail;

	/**
	 * 0:未上架 ;1:上架,2已下架
	 */
	private Integer status;

	/**
	 * 创建时间
	 */
	private Date createTime;

	private String createTimeStr;
	/**
	 * 详情的图片多张,隔开
	 */
	private String detailImgs;

	public Integer getInitAmount() {
		return initAmount;
	}

	public void setInitAmount(Integer initAmount) {
		this.initAmount = initAmount;
	}

	public Long getProductId() {
		return productId;
	}
	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
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
		this.productPriceStr =  (null != productPrice ? String.valueOf(productPrice): "0.00");
	}

	public String getProductPriceStr() {
		return productPriceStr;
	}

	public void setProductPriceStr(String productPriceStr) {
		this.productPriceStr = productPriceStr;
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
		this.createTimeStr = (null != createTime?Utils.formatDate(createTime, "yyyy-MM-dd HH:mm:ss"):"");
	}

	public String getCreateTimeStr() {
		return createTimeStr;
	}

	public void setCreateTimeStr(String createTimeStr) {
		this.createTimeStr = createTimeStr;
	}

	public String getDetailImgs() {
		return detailImgs;
	}

	public void setDetailImgs(String detailImgs) {
		this.detailImgs = detailImgs;
	}
public static void main(String[] args) {
	SimpleDateFormat sdf= new SimpleDateFormat();
	sdf.applyPattern("yyyy-MM-dd HH:mm:ss");
	System.out.println(sdf.format(new Date()));
}
}
