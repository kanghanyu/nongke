package com.khy.service;

import com.github.pagehelper.PageInfo;
import com.khy.mapper.dto.UserCommonDTO;
import com.khy.mapper.dto.UserOrderInfoDTO;

public interface OrderService {

	PageInfo<UserOrderInfoDTO> page(UserCommonDTO dto);

}
