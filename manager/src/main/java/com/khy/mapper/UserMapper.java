package com.khy.mapper;

import com.khy.entity.User;

public interface UserMapper {

	User getUserByPhone(String phone);

	int insert(User user);

	int updateUser(User user);

	User getUserByUid(String uid);

}
