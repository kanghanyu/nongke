package com.khy.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.khy.common.Constants;
import com.khy.entity.OnlineParame;
import com.khy.entity.Product;
import com.khy.entity.User;
import com.khy.entity.UserRecord;
import com.khy.mapper.OnlineParameMapper;
import com.khy.mapper.ProductMapper;
import com.khy.mapper.UserMapper;
import com.khy.mapper.UserRecordMapper;

public class BaseService {

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
}
