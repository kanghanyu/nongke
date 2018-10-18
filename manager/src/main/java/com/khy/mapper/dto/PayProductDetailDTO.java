package com.khy.mapper.dto;

import java.math.BigDecimal;

import com.khy.utils.BeanUtils;

import io.swagger.annotations.ApiModelProperty;

public class PayProductDetailDTO {

	
	@ApiModelProperty(value="商品的id")
	private Long productId;

	@ApiModelProperty(value="商品名称")
	private String productName;

	@ApiModelProperty(value="价格")
	private BigDecimal productPrice;

	@ApiModelProperty(value="缩略图")
	private String img;
	
	@ApiModelProperty(value="购买数量")
	private Integer amount;
	
	@ApiModelProperty(value="该商品总金额")
	private Double total;

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
		result = prime * result + ((productPrice == null) ? 0 : productPrice.hashCode());
		result = prime * result + ((total == null) ? 0 : total.hashCode());
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
		if (productPrice == null) {
			if (other.productPrice != null)
				return false;
		} else if (!productPrice.equals(other.productPrice))
			return false;
		if (total == null) {
			if (other.total != null)
				return false;
		} else if (!total.equals(other.total))
			return false;
		return true;
	}
	
	public static void main(String[] args) {
		PayProductDetailDTO dto = new PayProductDetailDTO();
		dto.setAmount(10);
		dto.setImg("1111");
		dto.setProductId(10L);
		dto.setProductName("测试");
		dto.setProductPrice(new BigDecimal(100));
		dto.setTotal(100D);
		PayProductDetailDTO dto2 = new PayProductDetailDTO();
		BeanUtils.copyProperties(dto, dto2);
		System.out.println(dto.equals(dto2));
	}
}
