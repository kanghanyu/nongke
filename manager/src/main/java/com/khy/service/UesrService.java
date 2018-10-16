package com.khy.service;

import java.util.List;

import org.springframework.web.multipart.MultipartRequest;

import com.khy.common.JsonResponse;
import com.khy.entity.User;
import com.khy.entity.UserAddress;
import com.khy.entity.UserBank;
import com.khy.entity.UserCash;
import com.khy.mapper.dto.UserAddressListDTO;

public interface UesrService {

	JsonResponse<User> login(User user);
	
	JsonResponse<User> getUserInfo();

	JsonResponse<Boolean> uploadImg(MultipartRequest request);
	
	JsonResponse<Boolean> register(User user);

	JsonResponse<Boolean> loginOut();

	JsonResponse<Boolean> resetPassword(User user);
	
	JsonResponse<Boolean> saveUserAddress(UserAddress userAddress);

	JsonResponse<UserAddressListDTO> listUserAddress();

	JsonResponse<Boolean> updateUserAddress(UserAddress userAddress);

	JsonResponse<Boolean> deleteUserAddress(Long id);

	JsonResponse<Boolean> saveOrUpdateUserBank(UserBank userBank);

	JsonResponse<UserBank> getUserBank();

	JsonResponse<Boolean> saveUserCash(UserCash userCash);

	JsonResponse<List<UserCash>> listUserCash();




}
