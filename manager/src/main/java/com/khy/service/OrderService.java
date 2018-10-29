package com.khy.service;

import java.util.List;

import com.khy.entity.OrderInfo;

public interface OrderService {

	List<OrderInfo> listNotBillOrder();

	void insert(int i);

	void saveBill(OrderInfo orderInfo);

}
