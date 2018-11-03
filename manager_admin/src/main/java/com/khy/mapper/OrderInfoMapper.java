package com.khy.mapper;

import java.util.List;

import com.khy.entity.OrderInfo;
import com.khy.mapper.dto.UserCommonDTO;
import com.khy.mapper.dto.UserOrderInfoDTO;

public interface OrderInfoMapper {
	List<OrderInfo> getUserBill(String uid);

	List<OrderInfo> listOrderInfo(OrderInfo info);

	List<OrderInfo> list(UserCommonDTO dto);
}