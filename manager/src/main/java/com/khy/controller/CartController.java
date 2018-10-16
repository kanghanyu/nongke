package com.khy.controller;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.khy.common.JsonResponse;
import com.khy.mapper.dto.CartDTO;
import com.khy.mapper.dto.CartRet;
import com.khy.service.CartService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/cart")
@Api(value="购物车相关的")
public class CartController {

	@Autowired
	private CartService cartService;
	
	@RequestMapping(value = "/saveCart",method = RequestMethod.POST)
	@ApiOperation(notes = "添加商品到购物车",value = "添加商品到购物车")
    @ApiImplicitParam(name = "dto", value = "购物车相关参数", required = true, paramType = "body", dataType = "CartDTO")
	public JsonResponse<Boolean> saveCart(@RequestBody CartDTO dto){
		JsonResponse<Boolean> jsonResponse = cartService.saveCart(dto);
		return jsonResponse;
	}
	
	@RequestMapping(value = "/updateProductAmount",method = RequestMethod.POST)
	@ApiOperation(notes = "更新购物车某商品的数量",value = "更新购物车某商品的数量")
	@ApiImplicitParam(name = "dto", value = "购物车相关参数", required = true, paramType = "body", dataType = "CartDTO")
	public JsonResponse<Boolean> updateProductAmount(@RequestBody CartDTO dto){
		JsonResponse<Boolean> jsonResponse = cartService.updateProductAmount(dto);
		return jsonResponse;
	}
	
	@RequestMapping(value = "/deleteProduct",method = RequestMethod.POST)
	@ApiOperation(notes = "删除购物车某商品",value = "删除购物车某商品")
	@ApiImplicitParam(name = "dto", value = "购物车相关参数", required = true, paramType = "body", dataType = "CartDTO")
	public JsonResponse<Boolean> deleteProduct(@RequestBody CartDTO dto){
		JsonResponse<Boolean> jsonResponse = cartService.deleteProduct(dto);
		return jsonResponse;
	}
	
	@RequestMapping(value = "/clearCart",method = RequestMethod.POST)
	@ApiOperation(notes = "清空购物车",value = "清空购物车")
	public JsonResponse<Boolean> clearCart( ){
		JsonResponse<Boolean> jsonResponse = cartService.clearCart();
		return jsonResponse;
	}
	
	@RequestMapping(value = "/getCartProduct",method = RequestMethod.POST)
	@ApiOperation(notes = "获取购物车商品列表",value = "获取购物车商品列表")
	public JsonResponse<CartRet> getCartProduct( ){
		JsonResponse<CartRet> jsonResponse = cartService.getCartProduct();
		return jsonResponse;
	}
}

