package com.khy.mapper;

import java.util.List;

import com.khy.entity.UserRecord;

public interface UserRecordMapper {
	int insert(UserRecord record);

	List<UserRecord> listUserRecord(UserRecord record);

	void delete(UserRecord record);
	
}