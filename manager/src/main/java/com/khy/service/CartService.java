package com.khy.service;

import com.khy.common.JsonResponse;
import com.khy.mapper.dto.CartDTO;
import com.khy.mapper.dto.CartRet;

public interface CartService {

	JsonResponse<Boolean> saveCart(CartDTO dto);

	JsonResponse<Boolean> updateProductAmount(CartDTO dto);

	JsonResponse<Boolean> deleteProduct(CartDTO dto);

	JsonResponse<Boolean> clearCart();

	JsonResponse<CartRet> getCartProduct();

}
