package com.khy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.github.pagehelper.PageInfo;
import com.khy.common.JsonResponse;
import com.khy.entity.Product;
import com.khy.mapper.dto.ProductDTO;
import com.khy.service.ProductService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/product")
@Api(value="商品相关的")
public class ProductController {

	@Autowired
	private ProductService productService;

	/**
	 * 根据条件分页查询商品内容
	 * @Description
	 * @author khy
	 * @date  2018年10月6日下午8:50:57
	 * @param product
	 * @return
	 */
	@RequestMapping(value = "/pageProduct",method = RequestMethod.POST)
	@ApiOperation(notes = "分页查询商品内容",value = "分页查询商品内容")
    @ApiImplicitParam(name = "dto", value = "用户登录的接口参数", required = true, paramType = "body", dataType = "ProductDTO")
	public JsonResponse<PageInfo<Product>> pageProduct(@RequestBody ProductDTO dto){
		JsonResponse<PageInfo<Product>> jsonResponse = productService.pageProduct(dto);
		return jsonResponse;
	}
	
	@RequestMapping(value = "/getProduct",method = RequestMethod.POST)
	@ApiOperation(value = "根据商品的id获取商品详情商品内容")
	@ApiImplicitParam(paramType = "query", dataType = "Long", name = "productId", value = "商品 的id", required = true)
	public JsonResponse<Product> getProduct(Long productId){
		JsonResponse<Product> jsonResponse = productService.getProduct(productId);
		return jsonResponse;
	}
	
}

