package com.khy.schedule;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.khy.entity.OrderInfo;
import com.khy.service.OrderService;
import com.khy.service.impl.BaseService;

//@Component
public class Task extends BaseService{
	public final static Logger logger = LoggerFactory.getLogger(Task.class);
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private final static BigDecimal ZERO = new BigDecimal("0.00");
	@Autowired
	private OrderService orderService;
	
	
	@Scheduled(cron = "* */1 * * * ?")
	public void updateNotPayOrder(){
		List<String>list = orderService.getNotPayOrder();
		if(CollectionUtils.isNotEmpty(list)){
			logger.info("获取到过期的用户订单size={}",list.size());
			int num = 1;
			for (String orderId : list) {
				try {
					logger.info("定时更新用户已过期的订单orderId={}",orderId);
					int flag = orderService.setNotPayOrder(orderId);
					if(flag>0){
						num++;
					}else{
						logger.error("定时更新用户已过期的订单orderId={}",orderId);
					}
				} catch (Exception e) {
					logger.error("处理到过期的用户订单异常订单的orderId={}",orderId);
					continue;
				}
			}
			logger.info("处理到过期的用户订单num={}",num);
		}
	}

	
	//更新付款完7天未确认收款的
	@Scheduled(cron = "*/5 * * * * ?")
	public void updateProductOrder(){
		List<OrderInfo>list = orderService.getNotConfirmOrder();
		if(CollectionUtils.isNotEmpty(list)){
			String orderId = null;
			logger.info("获取到的用户付款时间超过7天的商品订单size={}",list.size());
			int num = 1;
			for (OrderInfo orderInfo : list) {
				try {
					logger.info("定时更新用户付款时间超过7天的订单orderInfo={}",JSONObject.toJSON(orderInfo));
					int flag = orderService.setConfirmOrder(orderInfo);
					if(flag>0){
						num++;
					}else{
						logger.error("定时更新用户付款时间超过7天的订单orderInfo={}",JSONObject.toJSON(orderInfo));
					}
				} catch (Exception e) {
					logger.error("定时更新用户付款时间超过7天的订单orderId={}",orderId);
					continue;
				}
			}
			logger.info("定时更新用户付款时间超过7天的订单num={}",num);
		}
	}
	
	//已付款的充值订单开始充值
	@Scheduled(cron = "*/5 * * * * ?")
	public void noRechargeUserPhone(){
		List<String>list = orderService.noRechargeOrder();
		if(CollectionUtils.isNotEmpty(list)){
			logger.info("定时获取到的还未充值的话费订单size={}",list.size());
			int num = 1;
			for (String orderId : list) {
				try {
					logger.info("获取到的还未充值的话费订单orderId={}",orderId);
					int flag = orderService.recharge(orderId);
					if(flag>0){
						num++;
					}else{
						logger.error("还未充值的订单充值失败orderId={}",orderId);
					}
				} catch (Exception e) {
					logger.error("还未充值的话费订单-->充值异常orderId={}",orderId);
					continue;
				}
			}
			logger.info("定时更新还未充值的话费订单完成num={}",num);
		}
	}
	
	
	@Scheduled(cron = "*/5 * * * * ?")
	public void setOrderCommission(){
		List<String>list = orderService.getNotCommission();
		if(CollectionUtils.isNotEmpty(list)){
			logger.info("定时处理订单分佣的内容size={}",list.size());
			int num = 1;
			for (String orderId : list) {
				try {
					logger.info("定时处理订单分佣的订单内容orderId={}",orderId);
					int flag = orderService.setOrderCommission(orderId);
					if(flag>0){
						num++;
					}
				} catch (Exception e) {
					logger.error("定时处理订单分佣的订单内容orderId={}",orderId);
					continue;
				}
			}
			logger.info("定时处理订单分佣的订单内容num={}",num);
		}
	}
}
