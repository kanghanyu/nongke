package com.khy.mapper;

import java.util.List;

import com.khy.entity.OnlineParame;

public interface OnlineParameMapper {
	List<OnlineParame> list();
	
	OnlineParame findByKey(String title);
	
}