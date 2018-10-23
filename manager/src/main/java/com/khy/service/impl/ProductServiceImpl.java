package com.khy.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.khy.common.JsonResponse;
import com.khy.entity.Product;
import com.khy.mapper.ProductMapper;
import com.khy.mapper.dto.ProductDTO;
import com.khy.service.ProductService;
@Service
@Transactional
public class ProductServiceImpl implements ProductService {
	@Autowired
	private ProductMapper productMapper;

	@Override
	public JsonResponse<PageInfo<Product>> pageProduct(ProductDTO dto) {
		JsonResponse<PageInfo<Product>> jsonResponse = new JsonResponse<PageInfo<Product>>();
		if(null == dto){
			jsonResponse.setRetDesc("参数不能为空");
			return jsonResponse;
		}
		PageHelper.startPage(dto.getPageNum(), dto.getPageSize());
		List<Product> list = productMapper.list(dto);
		PageInfo <Product>pageInfo = new PageInfo<Product>(list);
		jsonResponse.success(pageInfo);
		return jsonResponse;
	}

	@Override
	public JsonResponse<Product> getProduct(Long productId) {
		JsonResponse<Product> jsonResponse = new JsonResponse<Product>();
		if(null == productId){
			jsonResponse.setRetDesc("商品id不能为空");
			return jsonResponse;
		}
		Product product = productMapper.findByProductId(productId);
		if(null != product){
			jsonResponse.setRetDesc("当前商品不存在");
			return jsonResponse;
		}
		jsonResponse.success(product);
		return jsonResponse;
	}

	@Override
	public JsonResponse<List<Product>> getBannerProduct() {
		JsonResponse<List<Product>> jsonResponse = new JsonResponse<>();
		List<Product> list = productMapper.getBannerProduct();
		jsonResponse.success(list);
		return jsonResponse;
	}

}
