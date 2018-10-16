package com.khy.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.khy.entity.Cart;

public interface CartMapper {
	Cart existProduct(@Param(value = "productId")Long productId, @Param(value = "uid")String uid);
	int update(Cart cart);
	int insert(Cart cart);

	int delete(@Param(value = "productId")Long productId, @Param(value = "uid")String uid);
	List<Cart> getCartProduct(String uid);
}
