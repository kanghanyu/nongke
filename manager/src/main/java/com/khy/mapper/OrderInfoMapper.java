package com.khy.mapper;

import java.util.List;

import com.khy.entity.OrderInfo;

public interface OrderInfoMapper {
	int insert(OrderInfo info);
	
	OrderInfo getPayOrder(OrderInfo info);
	
	int update(OrderInfo info);

	List<OrderInfo> getNotPayOrder();

	List<OrderInfo> getUserBill(String uid);

	List<OrderInfo> listOrderInfo(OrderInfo info);
	
}