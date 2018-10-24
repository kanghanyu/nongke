package com.khy.mapper;

import java.util.List;

import com.khy.entity.OrderInfo;
import com.khy.mapper.dto.UserBillDTO;

public interface OrderInfoMapper {
	int insert(OrderInfo info);
	
	OrderInfo getPayOrder(OrderInfo info);
	
	int update(OrderInfo info);

	List<OrderInfo> getNotPayOrder();

	List<UserBillDTO> getUserBill(OrderInfo info);
	
}