package com.khy.mapper;

import java.util.List;

import com.khy.entity.OrderInfo;

public interface OrderInfoMapper {
	int insert(OrderInfo info);
	
	OrderInfo getPayOrder(OrderInfo info);
	
	int update(OrderInfo info);

	List<String> getNotPayOrder();

	List<OrderInfo> getUserBill(String uid);

	List<OrderInfo> listOrderInfo(OrderInfo info);

	List<OrderInfo> getNotConfirmOrder();

	List<OrderInfo> listNotBillOrder();

	List<String> noRechargeOrder();

	OrderInfo getNotPayOrderById(String orderId);

	OrderInfo noRechargeOrderById(String orderId);

	List<String> notCommissionOrder();

	OrderInfo notCommissionOrderById(String orderId);

	OrderInfo notSaveBillOrderById(String orderId);
	
}