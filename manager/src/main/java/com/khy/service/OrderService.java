package com.khy.service;

import java.util.List;

import com.khy.entity.OrderInfo;

public interface OrderService {

	List<OrderInfo> listNotBillOrder();

	int saveBill(OrderInfo orderInfo);

	List<String> getNotPayOrder();

	int setNotPayOrder(String orderId);

	List<OrderInfo> getNotConfirmOrder();

	int setConfirmOrder(OrderInfo orderInfo);

	List<String> noRechargeOrder();

	int recharge(String orderId);

	List<String> getNotCommission();

	int setOrderCommission(String orderId);

}
