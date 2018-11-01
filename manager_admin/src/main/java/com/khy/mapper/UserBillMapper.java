package com.khy.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.khy.entity.UserBill;

public interface UserBillMapper {
    int insert(UserBill userBill);

}