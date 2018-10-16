package com.khy.mapper;

import java.util.List;

import com.khy.entity.UserCash;

public interface UserCashMapper {
	int insert(UserCash userCash);
	
	List<UserCash> listUserCash(String uid);
	
}