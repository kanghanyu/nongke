package com.khy.mapper;

import com.khy.entity.UserBank;

public interface UserBankMapper {
	UserBank getByUid(String uid);
	
	int insert(UserBank userBank);
	
	int update(UserBank userBank);
	
}