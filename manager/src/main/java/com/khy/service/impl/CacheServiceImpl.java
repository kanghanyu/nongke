package com.khy.service.impl;

import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.khy.utils.RedisUtils;

@Service
public class CacheServiceImpl implements CacheService{

	@Autowired
	private RedisUtils RedisUtils;
	
	public String getString(String key){
		String ret = RedisUtils.STRINGS.get(key);
		return ret;
	}
	
	public void setString(String key,String number,Integer seconds){
		RedisUtils.STRINGS.set(key, number);
		RedisUtils.KEYS.expire(key, seconds);
	}
	public void incr(String key,Long value,Integer seconds){
		RedisUtils.STRINGS.incrBy(key, value);
		RedisUtils.KEYS.expire(key, seconds);
	}
	
	public void decr(String key,Long number){
		RedisUtils.STRINGS.decrBy(key,number);
	}
	
	public void setString(String key,String value){
		RedisUtils.STRINGS.set(key, value);
	}
	
	public void setHash(String key,Map<String, String> map,Integer seconds){
		RedisUtils.HASH.hmset(key, map);
		RedisUtils.KEYS.expire(key, seconds);
	}
	
	public void setHash(String key,String fieid,String value){
		RedisUtils.HASH.hset(key, fieid, value);
	}
	
	public String getHash(String key,String fieid){
		String ret = RedisUtils.HASH.hget(key, fieid);
		return ret;
	}
	
	public Map<String, String> getHash(String key){
		 Map<String, String> ret = RedisUtils.HASH.hgetall(key);
		return ret;
	}
	
	public void delKey(String key){
		RedisUtils.KEYS.del(key);
	}
	
	public boolean lock(String key,String value,Integer seconds){
		long ret = RedisUtils.STRINGS.setnx(key, value);
		RedisUtils.KEYS.expire(key, seconds);
		return ret==0?true:false;
	}
	public boolean lock(String key,String value){
		long ret = RedisUtils.STRINGS.setnx(key, value);
		return ret==1?true:false;
	}
	
	public void releaseLock(String key){
		RedisUtils.KEYS.del(key);
	}
	
	public Set<String> keys(String pattern){
		Set<String> keys = RedisUtils.KEYS.keys(pattern);
		return keys;
	}
	
}
