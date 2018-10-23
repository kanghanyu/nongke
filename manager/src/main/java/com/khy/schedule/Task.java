package com.khy.schedule;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.khy.common.Constants;
import com.khy.entity.OrderInfo;
import com.khy.mapper.OrderInfoMapper;

@Component
public class Task {
	public final static Logger logger = LoggerFactory.getLogger(Task.class);
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	@Autowired
	private OrderInfoMapper orderInfoMapper;
	
//	@Scheduled(cron = "*/5 * * * * ?")
	public void updateNotPayOrder(){
		//先查询所有的符合条件的订单内容
		List<OrderInfo>list = orderInfoMapper.getNotPayOrder();
		String orderId = null;
		try {
			if(CollectionUtils.isNotEmpty(list)){
				logger.info("获取到过期的用户订单size={}",list.size());
				int num = 1;
				for (OrderInfo orderInfo : list) {
					OrderInfo updateInfo = new OrderInfo();
					orderId = orderInfo.getOrderId();
					updateInfo.setPayStatus(Constants.ORDER_PAYSTATUS_YQX);
					updateInfo.setStatus(Constants.ORDER_STATUS_WC);
					updateInfo.setUid(orderInfo.getUid());
					updateInfo.setOrderId(orderId);
					logger.info("定时更新用户已过期的订单orderInfo={}",JSONObject.toJSON(orderInfo));
					int flag = orderInfoMapper.update(updateInfo);
					if(flag>0){
						num++;
					}
				}
				logger.info("处理到过期的用户订单num={}",num);
			}
		} catch (Exception e) {
			logger.error("处理到过期的用户订单异常订单的orderId={}",orderId);
		}
	}
}
