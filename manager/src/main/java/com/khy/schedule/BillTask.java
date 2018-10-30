package com.khy.schedule;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.khy.common.Constants;
import com.khy.entity.OrderInfo;
import com.khy.service.OrderService;
import com.khy.service.impl.BaseService;

//@Component
public class BillTask extends BaseService{
	public final static Logger logger = LoggerFactory.getLogger(BillTask.class);
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private final static BigDecimal ZERO = new BigDecimal("0.00");

	@Autowired
	private OrderService orderService;
	
	@Scheduled(cron = "*/5 * * * * ?")
	public void setBill(){
		//先查询所有的符合条件的订单内容
		List<OrderInfo>list = orderService.listNotBillOrder();
		if(CollectionUtils.isNotEmpty(list)){
			logger.info("设置后台出账内容size={}",list.size());
			int num = 1;
			for (OrderInfo info : list) {
				try {
					orderService.saveBill(info);
					num ++;
				} catch (Exception e) {
					logger.error("处理出账的订单内容异常订单的orderInfo={}",JSON.toJSON(info));
					continue;
				}
			}
			logger.info("处理出账的订单内容异常订单的num={}",num);
		}
	}
	
	
	

//	@Scheduled(cron = "*/5 * * * * ?")
//	public void setBill(){
// 
//		for (int i = 0; i < 10; i++) {
//			try {
//				orderService.insert(i);
//			} catch (Exception e) {
//				logger.error("处理到过期的用户订单异常订单的orderId={}");
//				continue;
//			}
//			
//		}
//		 
//	}
	 
}
