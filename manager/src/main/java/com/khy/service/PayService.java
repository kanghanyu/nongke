package com.khy.service;

import com.khy.common.JsonResponse;
import com.khy.mapper.dto.PreOrderDTO;
import com.khy.mapper.dto.PreOrderResultDTO;
import com.khy.mapper.dto.SubmitOrderDTO;
import com.khy.mapper.dto.SubmitOrderResultDTO;

public interface PayService {

	JsonResponse<PreOrderResultDTO> createPreOrder(PreOrderDTO dto);

	JsonResponse<PreOrderResultDTO> buyVip();

	JsonResponse<SubmitOrderResultDTO> payForProductOnline(SubmitOrderDTO dto);

}
