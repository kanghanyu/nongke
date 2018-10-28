package com.khy.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.khy.common.Constants;
import com.khy.entity.OnlineParame;
import com.khy.entity.OrderInfo;
import com.khy.entity.Product;
import com.khy.entity.User;
import com.khy.entity.UserRecord;
import com.khy.exception.BusinessException;
import com.khy.mapper.OnlineParameMapper;
import com.khy.mapper.OrderInfoMapper;
import com.khy.mapper.ProductMapper;
import com.khy.mapper.UserMapper;
import com.khy.mapper.UserRecordMapper;

public class BaseService {
	public final static Logger logger = LoggerFactory.getLogger(PayServiceImpl.class);
	private final static BigDecimal ZERO = new BigDecimal("0.00");
	private final static BigDecimal ONE_THOUSAND = new BigDecimal("1000");
	@Autowired
	private CacheService cacheService;
	@Autowired
	private OnlineParameMapper onlineParameMapper;
	@Autowired
	private UserMapper userMapper;
	@Autowired
	private UserRecordMapper userRecordMapper;
	@Autowired
	private ProductMapper productMapper;
	@Autowired
	private OrderInfoMapper orderInfoMapper;
	public Map<String,String> getOnline(){
		Map<String, String> map = cacheService.getHash(Constants.ONLINE_PARARME);
		if(null != map && map.size()>0){
			return map;
		}
		List<OnlineParame> list = onlineParameMapper.list();
		if(CollectionUtils.isNotEmpty(list)){
			map = new HashMap<>();
			for (OnlineParame dto : list) {
				map.put(dto.getTitle(),dto.getContent());
			}
			cacheService.setHash(Constants.ONLINE_PARARME, map,Constants.ONE_DAY);
		}
		return map;
	}
	
	public Map<String,OnlineParame> getOnlineParameInfo(){
		Map<String,OnlineParame> map = null;
		List<OnlineParame> list = onlineParameMapper.list();
		if(CollectionUtils.isNotEmpty(list)){
			map = new HashMap<>();
			for (OnlineParame dto : list) {
				map.put(dto.getTitle(),dto);
			}
		}
		return map;
	}
	
	public JSONObject getUserByUidAndLock(String uid){
		JSONObject json = new JSONObject();
		json.put("code",1000);
		boolean lock = cacheService.lock(Constants.LOCK_USER+uid, Constants.LOCK, Constants.FIVE_MINUTE);
		if(lock){
			json.put("msg","当前用户操作繁忙,请稍后再试");
			return json;
		}
		User user = userMapper.getUserByUid(uid);
		json.put("code",2000);
		json.put("user", user);
		return json;
	}
	
	public JSONObject getUserByPhoneAndLock(String phone){
		JSONObject json = new JSONObject();
		json.put("code",1000);
		User user = userMapper.getUserByPhone(phone);
		if(null == user){
			json.put("msg","对方账户不存在");
			return json;
		}
		String uid = user.getUid();
		boolean lock = cacheService.lock(Constants.LOCK_USER+uid, Constants.LOCK, Constants.FIVE_MINUTE);
		if(lock){
			json.put("msg","对方用户操作繁忙,请稍后再试");
			return json;
		}
		json.put("code",2000);
		json.put("user", user);
		return json;
	}
	
	public JSONObject getProductByProductIdAndLock(Long productId){
		JSONObject json = new JSONObject();
		json.put("code",1000);
		boolean lock = cacheService.lock(Constants.LOCK_PRODUCT+productId, Constants.LOCK, Constants.FIVE_MINUTE);
		if(lock){
			json.put("msg","当前商品id:"+productId+"的商品状态异常,请稍后再试");
			return json;
		}
		Product product = productMapper.findProduct(productId);
		if(null == product){
			json.put("msg","当前商品不存在/库存不足");
			return json;
		}
		json.put("code",2000);
		json.put("product", product);
		return json;
	}
	
	public boolean saveUserRecord(String uid,Integer payType,Integer type,BigDecimal amount,BigDecimal lastAmount,String targetId,String description,Date date){
		UserRecord record = new UserRecord();
		record.setUid(uid);
		record.setPayType(payType);
		record.setType(type);
		record.setAmount(amount);
		record.setLastAmount(lastAmount);
		record.setTargetId(targetId);
		record.setDescription(description);
		record.setCreateTime(date);
		int flag = userRecordMapper.insert(record);
		return flag>0?true:false;
	}
	
	/***
	 * 订单已完成/已付款
	 * @Description
	 * @author khy
	 * @date  2018年10月24日下午6:50:24
	 * @param uid
	 * @param orderId
	 */
	public void setCommission(String uid, String orderId) {
		if (StringUtils.isNoneBlank(uid, orderId)) {
			User user = userMapper.getUserByUid(uid);
			if (null == user || user.getIsVip() == Constants.GENERAL_UER) {
				return;
			}
			OrderInfo info = new OrderInfo();
			info.setOrderId(orderId);
			info.setUid(uid);
			info.setStatus(Constants.ORDER_STATUS_WC);
			info.setPayStatus(Constants.ORDER_PAYSTATUS_YFK);
			OrderInfo order = orderInfoMapper.getPayOrder(info);
			// 充值点卡是没有分佣的
			if (null == order && order.getOrderType() == Constants.PAY_CARD) {
				return;
			}
			Integer orderType = order.getOrderType();
			BigDecimal total = ZERO;
			if (orderType == Constants.PAY_PRODUCT) {
				total = order.getDiscountMoney();
			} else {
				total = order.getTotalPay();
			}
			// 总金额
			Map<String, String> online = getOnline();
			if (null == online || online.isEmpty()) {
				return;
			}
			String inviterUid = user.getInviterUid();
			int num = 1;
			BigDecimal money = ZERO;
			try {
				while(num <= 3 && StringUtils.isNotBlank(inviterUid)){
					String desc = "会员"+user.getPhone();
					if (orderType == Constants.PAY_VIP) {
						String amount = online.get(Constants.PAY_VIP_BILL_EXTRACT+num);
						if(StringUtils.isBlank(amount)){
							logger.error("用户uid = {} 的订单orderId={}交易完成第{}级邀请人uid={}获取vip分佣提成异常",uid,orderId,num,inviterUid);
							continue ;
						}
						money = new BigDecimal(amount);
						desc += "升级VIP";
					}else if(orderType == Constants.PAY_PHONE){
						String amount = online.get(Constants.PAY_PHONE_BILL_EXTRACT+num);
						if(StringUtils.isBlank(amount)){
							logger.error("用户uid = {} 的订单orderId={}交易完成第{}级邀请人uid={}获取话费分佣提成异常",uid,orderId,num,inviterUid);
							continue ;
						}
						money = total.multiply(new BigDecimal(amount)).divide(ONE_THOUSAND,2, BigDecimal.ROUND_HALF_UP);
						desc += "话费充值";
					}else if(orderType == Constants.PAY_PRODUCT){
						String amount = online.get(Constants.PAY_PRODUCT_BILL_EXTRACT);
						if(StringUtils.isBlank(amount)){
							logger.error("用户uid = {} 的订单orderId={}交易完成第{}级邀请人uid={}获取商品分佣提成异常",uid,orderId,num,inviterUid);
							continue ;
						}
						money = total.multiply(new BigDecimal(amount)).divide(ONE_THOUSAND,2, BigDecimal.ROUND_HALF_UP);
						desc += "购买商品";
					}
					
					if(money.compareTo(ZERO) == 0){
						throw new BusinessException("用户分佣异常,请联系关联员");
					}
					//添加到分佣记录里面去;
					inviterUid = setCommission(inviterUid,money,desc,orderId);
					num++;
				}
				//更新订单的状态为已发放佣金
				OrderInfo updateInfo = new OrderInfo();
				updateInfo.setOrderId(orderId);
				updateInfo.setUid(uid);
				updateInfo.setPayStatus(Constants.ORDER_PAYSTATUS_YFF);
				orderInfoMapper.update(info);
			} catch (Exception e) {
				logger.error("用户uid = {} 的订单orderId={}交易完成提成异常",uid,orderId);
				throw new BusinessException(e.getMessage());
			}
		}
	}

	
	private String setCommission(String uid, BigDecimal money, String desc, String orderId) {
		int num=0;
		JSONObject jsonObject = null;
		String inviterUid = null;
		try {
			while(num < 2){
				jsonObject = getUserByUidAndLock(uid);
				if(jsonObject.getInteger("code")== 1000){
					Thread.sleep(3000L);
				}
				num++;
			}
			if(jsonObject.getInteger("code")== 1000){
				logger.error("分佣操作当前用户操作繁忙请稍后再试orderId={},uid={}",orderId,uid);
			}
			User user = jsonObject.getObject("user", User.class);
			if(null == user || user.getIsVip() == Constants.GENERAL_UER){
				logger.error("分佣操作当前用户不是vip用户,orderId={},uid={}",orderId,uid);
			}
			inviterUid = user.getInviterUid();
			BigDecimal commission = user.getCommission()!=null?user.getCommission():ZERO;
			user.setCommission(commission.add(money));
			userMapper.updateUser(user);
			saveUserRecord(uid, Constants.RECORD_INCOME, Constants.RECORD_COMMISSION, money, commission, orderId, desc, new Date());
		} catch (InterruptedException e) {
			throw new BusinessException("用户分佣异常,请联系关联员uid={"+uid+"},orderId={"+orderId+"}");
		}finally{
			//释放当前用户的锁
			cacheService.releaseLock(Constants.LOCK_USER + uid);
		}
		return inviterUid;
	}
}
