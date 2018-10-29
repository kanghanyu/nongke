package com.khy.service;

import javax.servlet.http.HttpServletRequest;

import com.khy.common.JsonResponse;
import com.khy.mapper.dto.PreOrderDTO;
import com.khy.mapper.dto.PreOrderResultDTO;
import com.khy.mapper.dto.RechargeResultDTO;
import com.khy.mapper.dto.RechargeSubmitDTO;
import com.khy.mapper.dto.SubmitOrderDTO;
import com.khy.mapper.dto.SubmitOrderResultDTO;

public interface PayService {

	JsonResponse<PreOrderResultDTO> createPreOrder(PreOrderDTO dto);

	JsonResponse<SubmitOrderResultDTO> payForProductOnline(SubmitOrderDTO dto);

	JsonResponse<RechargeResultDTO> recharge(RechargeSubmitDTO dto);

	JsonResponse<Boolean> payNotify(String payType, HttpServletRequest request);

}
