package com.khy.mapper;

import java.util.List;

import com.khy.entity.UserAddress;

public interface UserAddressMapper {
	int insert(UserAddress record);

	void batchUpdate(String uid);
	
	List<UserAddress> listUserAddress(String uid);
	
	int update(UserAddress userAddress);
	
	UserAddress getById(Long id);
	
	int deleteUserAddress(Long id);
}