package com.khy.mapper;

import java.util.List;

import com.khy.entity.Product;
import com.khy.mapper.dto.ProductDTO;

public interface ProductMapper {

	List<Product> list(ProductDTO product);

	Product findByProductId(Long productId);

	int updateProduct(Product product);

	Product findProduct(Long productId);
}