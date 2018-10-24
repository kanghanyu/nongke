package com.khy.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartRequest;

import com.khy.common.JsonResponse;
import com.khy.entity.OnlineParame;
import com.khy.entity.User;
import com.khy.entity.UserAddress;
import com.khy.entity.UserBank;
import com.khy.entity.UserCash;
import com.khy.entity.UserInviter;
import com.khy.mapper.dto.CartMoneyDTO;
import com.khy.mapper.dto.UserInviterDTO;
import com.khy.mapper.dto.UserRecordDTO;

public interface UesrService {

	JsonResponse<User> login(User user);
	
	JsonResponse<User> getUserInfo();

	JsonResponse<Boolean> uploadImg(MultipartRequest request);
	
	JsonResponse<Boolean> register(User user);

	JsonResponse<Boolean> loginOut();

	JsonResponse<Boolean> resetPassword(User user);
	
	JsonResponse<Boolean> saveOrUpdateUserAddress(UserAddress userAddress);

	JsonResponse<UserAddress> getUserAddress();

	JsonResponse<Boolean> saveOrUpdateUserBank(UserBank userBank);

	JsonResponse<UserBank> getUserBank();

	JsonResponse<Boolean> saveUserCash(UserCash userCash);

	JsonResponse<List<UserCash>> listUserCash();

	JsonResponse<Map<String, OnlineParame>> getOnlineParame();

	JsonResponse<List<UserInviterDTO>> listUserInviter(UserInviter userInviter);

	JsonResponse<Boolean> commissionToMoney(BigDecimal commission);

	JsonResponse<Boolean> cardMoneyToUser(CartMoneyDTO dto);

	JsonResponse<List<UserRecordDTO>> listUserRecord(Integer type);

}
