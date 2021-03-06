package com.khy.mapper.dto;

import java.math.BigDecimal;

import io.swagger.annotations.ApiModelProperty;

public class PayProductDetailDTO {

	
	@ApiModelProperty(value="商品的id")
	private Long productId;

	@ApiModelProperty(value="商品名称")
	private String productName;

	@ApiModelProperty(value="价格")
	private BigDecimal productPrice;
	
	//进价
	private BigDecimal costPrice;

	@ApiModelProperty(value="缩略图")
	private String img;
	
	@ApiModelProperty(value="购买数量")
	private Integer amount;
	
	@ApiModelProperty(value="该商品总金额")
	private Double total;
	
	//商品的总成本 进价*数量
	private Double cost;

	public BigDecimal getCostPrice() {
		return costPrice;
	}
	public void setCostPrice(BigDecimal costPrice) {
		this.costPrice = costPrice;
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

	public Double getCost() {
		return cost;
	}
	public void setCost(Double cost) {
		this.cost = cost;
	}
	public void setProductPrice(BigDecimal productPrice) {
		this.productPrice = productPrice;
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
	}
	public Double getTotal() {
		return total;
	}
	public void setTotal(Double total) {
		this.total = total;
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((amount == null) ? 0 : amount.hashCode());
		result = prime * result + ((img == null) ? 0 : img.hashCode());
		result = prime * result + ((productId == null) ? 0 : productId.hashCode());
		result = prime * result + ((productName == null) ? 0 : productName.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PayProductDetailDTO other = (PayProductDetailDTO) obj;
		if (amount == null) {
			if (other.amount != null)
				return false;
		} else if (!amount.equals(other.amount))
			return false;
		if (img == null) {
			if (other.img != null)
				return false;
		} else if (!img.equals(other.img))
			return false;
		if (productId == null) {
			if (other.productId != null)
				return false;
		} else if (!productId.equals(other.productId))
			return false;
		if (productName == null) {
			if (other.productName != null)
				return false;
		} else if (!productName.equals(other.productName))
			return false;
		return true;
	}
	public static void main(String[] args) {
		PayProductDetailDTO dto = new PayProductDetailDTO();
		dto.setAmount(2);
		dto.setImg("http://nongke.oss-cn-beijing.aliyuncs.com/img/10101611/20181012151658.png");
		dto.setProductId(10L);
		dto.setProductName("洗发水");
		dto.setProductPrice(new BigDecimal("21.00"));
		dto.setTotal(24.0);
		
		PayProductDetailDTO dto2 = new PayProductDetailDTO();
		dto2.setAmount(10);
		dto2.setImg("1111");
		dto2.setProductId(10L);
		dto2.setProductName("测试");
		dto2.setProductPrice(new BigDecimal(100));
		dto2.setTotal(100D);
		System.out.println(dto.equals(dto2));
	}
}
