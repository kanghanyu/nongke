package com.khy.service.impl;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.khy.common.JsonResponse;
import com.khy.entity.Cart;
import com.khy.entity.Product;
import com.khy.entity.User;
import com.khy.mapper.CartMapper;
import com.khy.mapper.ProductMapper;
import com.khy.mapper.dto.CartDTO;
import com.khy.mapper.dto.CartRet;
import com.khy.service.CartService;
import com.khy.utils.SessionHolder;
@Service
@Transactional
public class CartServiceImpl implements CartService {

	@Autowired
	private CartMapper cartMapper;
	@Autowired
	private ProductMapper productMapper;
	
	@Override
	public JsonResponse<Boolean> saveCart(CartDTO dto) {
		JsonResponse<Boolean>jsonResponse = new JsonResponse<>();
		User user = SessionHolder.currentUser();
		if(null == dto || null == dto.getAmount()|| null == dto.getProductId()){
			jsonResponse.setRetDesc("参数不能为空");
			return jsonResponse;
		}
		if(dto.getAmount().intValue() <= 0){
			jsonResponse.setRetDesc("数量必须大于0");
			return jsonResponse;
		}
		if(null == user){
			jsonResponse.setRetDesc("请重新登录");
			return jsonResponse;
		}
		//TODO 需不需要校验商品内容
		Long productId = dto.getProductId();
		Product product = productMapper.findProduct(productId);
		if(null == product){
			jsonResponse.setRetDesc("该商品库存不足/已下架");
			return jsonResponse;
		}
		String uid = user.getUid();
		//如果不存在则新增否则更新
		Cart cart = cartMapper.existProduct(productId,uid);
		int flag =0;
		if(null != cart){
			Integer amount = cart.getAmount()+dto.getAmount();
			if(amount.compareTo(product.getStockAmount())>0){
				jsonResponse.setRetDesc("该商品库存不足");
				return jsonResponse;
			}
			cart.setAmount(amount);
			flag = cartMapper.update(cart);
		}else{
			Integer amount = dto.getAmount();
			if(amount.compareTo(product.getStockAmount())>0){
				jsonResponse.setRetDesc("该商品库存不足");
				return jsonResponse;
			}
			//插入数据
			cart = new Cart();
			cart.setUid(uid);
			cart.setProductId(productId);
			cart.setProductName(product.getProductName());
			cart.setProductPrice(product.getProductPrice());
			cart.setImg(product.getImg());
			cart.setAmount(amount);
			cart.setCreateTime(new Date());
			cart.setStatus(0);
			flag = cartMapper.insert(cart);
		}
		if(flag > 0){
			jsonResponse.success(true);
		}else{
			jsonResponse.success(false);
		}
		return jsonResponse;
	}

	@Override
	public JsonResponse<Boolean> updateProductAmount(CartDTO dto) {
		JsonResponse<Boolean>jsonResponse = new JsonResponse<>();
		User user = SessionHolder.currentUser();
		if(null == dto || null == dto.getAmount()|| null == dto.getProductId()){
			jsonResponse.setRetDesc("参数不能为空");
			return jsonResponse;
		}
		if(dto.getAmount().intValue() <= 0){
			jsonResponse.setRetDesc("数量必须大于0");
			return jsonResponse;
		}
		if(null == user){
			jsonResponse.setRetDesc("请重新登录");
			return jsonResponse;
		}
		
		//TODO 需不需要校验商品内容
		Long productId = dto.getProductId();
		Product product = productMapper.findProduct(productId);
		if(null == product){
			jsonResponse.setRetDesc("该商品库存不足/已下架");
			return jsonResponse;
		}
		String uid = user.getUid();
		//如果不存在则新增否则更新
		Cart cart = cartMapper.existProduct(productId,uid);
		if(null == cart){
			jsonResponse.setRetDesc("购物车没有该条记录内容");
			return jsonResponse;
		}
		Integer amount = dto.getAmount();
		if(amount.compareTo(product.getStockAmount())>0){
			jsonResponse.setRetDesc("该商品库存不足");
			return jsonResponse;
		}
		cart.setAmount(amount);
		int flag = cartMapper.update(cart);
		if(flag > 0){
			jsonResponse.success(true);
		}else{
			jsonResponse.success(false);
		}
		return jsonResponse;
	}

	@Override
	public JsonResponse<Boolean> deleteProduct(CartDTO dto) {
		JsonResponse<Boolean>jsonResponse = new JsonResponse<>();
		User user = SessionHolder.currentUser();
		if(null == dto || null == dto.getProductId()){
			jsonResponse.setRetDesc("参数不能为空");
			return jsonResponse;
		}
		if(null == user){
			jsonResponse.setRetDesc("请重新登录");
			return jsonResponse;
		}
		//TODO 需不需要校验商品内容
		Long productId = dto.getProductId();
		String uid = user.getUid();
		int flag = cartMapper.delete(productId,uid);
		if(flag > 0){
			jsonResponse.success(true);
		}else{
			jsonResponse.success(false);
		}
		return jsonResponse;
	}

	@Override
	public JsonResponse<Boolean> clearCart() {
		JsonResponse<Boolean>jsonResponse = new JsonResponse<>();
		User user = SessionHolder.currentUser();
		if(null == user){
			jsonResponse.setRetDesc("请重新登录");
			return jsonResponse;
		}
		//TODO 需不需要校验商品内容
		String uid = user.getUid();
		int flag = cartMapper.delete(null,uid);
		if(flag > 0){
			jsonResponse.success(true);
		}else{
			jsonResponse.success(false);
		}
		return jsonResponse;
	}

	@Override
	public JsonResponse<CartRet> getCartProduct() {
		JsonResponse<CartRet>jsonResponse = new JsonResponse<>();
		User user = SessionHolder.currentUser();
		if(null == user){
			jsonResponse.setRetDesc("请重新登录");
			return jsonResponse;
		}
		//TODO 需不需要校验商品内容
		String uid = user.getUid();
		List<Cart> list = cartMapper.getCartProduct(uid);
		CartRet ret = null;
		if(CollectionUtils.isNotEmpty(list)){
			ret = new CartRet();
			ret.setUid(uid);
			ret.setList(list);
			Double totalPrice = list.stream().collect(Collectors.summingDouble(Cart::getTotal));
			ret.setTotalPrice(totalPrice);
		}
		jsonResponse.success(ret);
		return jsonResponse;
	}
}
