package com.khy.service;

import java.util.List;

import com.github.pagehelper.PageInfo;
import com.khy.common.JsonResponse;
import com.khy.entity.Product;
import com.khy.mapper.dto.ProductDTO;

public interface ProductService {

	JsonResponse<PageInfo<Product>> pageProduct(ProductDTO dto);

	JsonResponse<Product> getProduct(Long productId);

	JsonResponse<List<Product>> getBannerProduct();

}
