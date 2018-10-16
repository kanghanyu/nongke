package com.khy.mapper;

import java.util.List;

import com.khy.entity.UserInviter;
import com.khy.mapper.dto.UserInviterDTO;

public interface UserInviterMapper {
	
	int insert(UserInviter userInviter);
	
	List<UserInviterDTO>listUserInviterByType(UserInviter userInviter);
}