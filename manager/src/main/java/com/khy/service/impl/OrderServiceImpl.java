package com.khy.service.impl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.khy.common.Constants;
import com.khy.entity.OrderInfo;
import com.khy.entity.UserBill;
import com.khy.exception.BusinessException;
import com.khy.mapper.OrderInfoMapper;
import com.khy.mapper.UserBillMapper;
import com.khy.schedule.BillTask;
import com.khy.service.OrderService;
@Service
@Transactional
public class OrderServiceImpl implements OrderService {
	public final static Logger logger = LoggerFactory.getLogger(BillTask.class);
	@Autowired
	private OrderInfoMapper orderInfoMapper;
	@Autowired
	private UserBillMapper userBillMapper;
	@Override
	public List<OrderInfo> listNotBillOrder() {
		return orderInfoMapper.listNotBillOrder();
	}
	
	@Override
	public synchronized  void saveBill(OrderInfo orderInfo) {
		logger.info("订单出账账单的设置开始--->orderInfo={}",JSON.toJSON(orderInfo));
		Integer isBill = orderInfo.getIsBill();
		if(isBill == Constants.ORDER_ISBILL_YCZ){
			return;
		}
		Integer orderType = orderInfo.getOrderType();
		if(orderType == Constants.PAY_CARD){
			return;
		}
		Integer payType = orderInfo.getPayType();
		if(payType == Constants.MONEY_PAY){
			return;
		}
		String orderId = orderInfo.getOrderId();
		String uid = orderInfo.getUid();
		List<UserBill>list = userBillMapper.list(orderId,uid);
		OrderInfo info = new OrderInfo();
		info.setOrderId(orderId);
		info.setUid(uid);
		info.setIsBill(Constants.ORDER_ISBILL_YCZ);
		orderInfoMapper.update(info);//先更新订单的状态内容;
		if(CollectionUtils.isEmpty(list)){
			if(orderType == Constants.PAY_VIP){
				
			}
			
			
			
		} 
		
		
		
		
		
		// TODO Auto-generated method stub
		
	}
	
	
	@Override
	public void insert(int i) {
		UserBill bill = new UserBill();
		bill.setUid("测试uid"+i);;
		userBillMapper.insert(bill);
		if(i == 5){
			throw new BusinessException("测试异常内容");
		}
	}

}
