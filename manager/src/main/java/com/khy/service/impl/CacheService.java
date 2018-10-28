package com.khy.service.impl;

import java.util.Map;
import java.util.Set;

public interface CacheService {

	public String getString(String key);
	public void setString(String key,String value,Integer seconds);
	public void setString(String key,String value);
	public void delKey(String key);
	public boolean lock(String key,String value,Integer seconds);
	public boolean lock(String key,String value);
	public void releaseLock(String key);
	public void setHash(String key,Map<String, String> map,Integer seconds);
	public void setHash(String key,String fieid,String value);
	public String getHash(String key,String fieid);
	public Map<String, String> getHash(String key);
	public Set<String> keys(String pattern);
}
