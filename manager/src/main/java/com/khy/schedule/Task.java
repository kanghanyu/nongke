package com.khy.schedule;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import com.alibaba.fastjson.JSONObject;
import com.khy.common.Constants;
import com.khy.entity.OrderInfo;
import com.khy.entity.User;
import com.khy.entity.UserRecord;
import com.khy.exception.BusinessException;
import com.khy.mapper.OrderInfoMapper;
import com.khy.mapper.UserMapper;
import com.khy.mapper.UserRecordMapper;
import com.khy.service.impl.BaseService;
import com.khy.service.impl.CacheService;

import scala.collection.immutable.Stream.Cons;

//@Component
public class Task extends BaseService{
	public final static Logger logger = LoggerFactory.getLogger(Task.class);
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private final static BigDecimal ZERO = new BigDecimal("0.00");

	@Autowired
	private OrderInfoMapper orderInfoMapper;
	
	
	@Scheduled(cron = "*/5 * * * * ?")
	public void updateNotPayOrder(){
		//先查询所有的符合条件的订单内容
		List<OrderInfo>list = orderInfoMapper.getNotPayOrder();
		String orderId = null;
		if(CollectionUtils.isNotEmpty(list)){
			logger.info("获取到过期的用户订单size={}",list.size());
			int num = 1;
			for (OrderInfo orderInfo : list) {
				try {
					//如果是商品订单未支付-->还需要将用户已经扣除的余额抵扣加回来;
					Integer orderType = orderInfo.getOrderType();
					if(orderType == Constants.PAY_PRODUCT){
						//需要还原用户购物下单时扣除的余额抵扣内容
						setUserMoney(orderInfo);
					}
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
		//先查询所有的符合条件的订单内容
		List<OrderInfo>list = orderInfoMapper.getNotConfirmOrder();
		if(CollectionUtils.isNotEmpty(list)){
			String orderId = null;
			logger.info("获取到的用户付款时间超过7天的商品订单size={}",list.size());
			int num = 1;
			for (OrderInfo orderInfo : list) {
				try {
					orderId = orderInfo.getOrderId();
					OrderInfo updateInfo = new OrderInfo();
					updateInfo.setUid(orderInfo.getUid());
					updateInfo.setOrderId(orderId);
					updateInfo.setPayStatus(Constants.ORDER_PAYSTATUS_YFK);
					updateInfo.setStatus(Constants.ORDER_STATUS_WC);
					logger.info("定时更新用户付款时间超过7天的订单orderInfo={}",JSONObject.toJSON(orderInfo));
					int flag = orderInfoMapper.update(updateInfo);
					if(flag>0){
						num++;
					}
				} catch (Exception e) {
					logger.error("定时更新用户付款时间超过7天的订单orderId={}",orderId);
					continue;
				}
			}
			logger.info("定时更新用户付款时间超过7天的订单num={}",num);
		}
	}
}
