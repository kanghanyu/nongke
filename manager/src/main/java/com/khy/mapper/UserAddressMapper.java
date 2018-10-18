package com.khy.mapper;

import com.khy.entity.UserAddress;

public interface UserAddressMapper {
	int insert(UserAddress record);

	int update(UserAddress userAddress);
	
	UserAddress getByUid(String uid);
}