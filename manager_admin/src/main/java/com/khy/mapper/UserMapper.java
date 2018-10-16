package com.khy.mapper;

import java.util.List;

import com.khy.entity.User;

public interface UserMapper {

	User getUserByPhone(String phone);

	int insert(User user);

	List<User> list(User user);

	int updateUser(User userDb);

	User getUserByUid(String uid);

}
