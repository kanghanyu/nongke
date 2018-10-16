package com.khy.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.khy.common.Constants;
import com.khy.entity.OnlineParame;
import com.khy.mapper.OnlineParameMapper;

public class BaseService {

	@Autowired
	private CacheService cacheService;
	@Autowired
	private OnlineParameMapper onlineParameMapper;
	
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
}
