package com.khy.mapper;

import com.khy.entity.OrderInfo;

public interface OrderInfoMapper {
	int insert(OrderInfo info);
	
	OrderInfo getPayOrder(OrderInfo info);
	
	int update(OrderInfo info);
	
}