package com.khy.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.khy.entity.OrderInfo;
import com.khy.mapper.OrderInfoMapper;
import com.khy.mapper.dto.UserCommonDTO;
import com.khy.mapper.dto.UserOrderInfoDTO;
import com.khy.mapper.dto.UserOrderProductDTO;
import com.khy.service.OrderService;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

	@Autowired
	private OrderInfoMapper orderInfoMapper;

	@Override
	public PageInfo<UserOrderInfoDTO> page(UserCommonDTO dto) {
		if(null == dto){
			return null;
		}
		PageHelper.startPage(dto.getPageNum(), dto.getPageSize());
		List<OrderInfo> list = orderInfoMapper.list(dto);
		List<UserOrderInfoDTO>ret = new ArrayList<>();
		if(CollectionUtils.isNotEmpty(list)){
			UserOrderInfoDTO orderDto = null;
			for (OrderInfo orderInfo : list) {
				orderDto = new UserOrderInfoDTO();
				BeanUtils.copyProperties(orderInfo, orderDto);
				String productDetail = orderInfo.getProductDetail();
				if(StringUtils.isNotBlank(productDetail)){
					List<UserOrderProductDTO> products = JSONArray.parseArray(productDetail, UserOrderProductDTO.class);
					orderDto.setProducts(products);
				}
				ret.add(orderDto);
			}
		}
		PageInfo <UserOrderInfoDTO>pageInfo = new PageInfo<UserOrderInfoDTO>(ret);
		return pageInfo;
	}

	@Override
	public JSONObject countOrderMoney(UserCommonDTO dto) {
		if(null == dto){
			return null;
		}
		return orderInfoMapper.countOrderMoney(dto);
	}

	@Override
	public UserOrderInfoDTO getEntityById(UserCommonDTO dto) {
		UserOrderInfoDTO ret = new UserOrderInfoDTO();
		OrderInfo info = orderInfoMapper.getEntityById(dto);
		if(null != info){
			BeanUtils.copyProperties(info, ret);
			String productDetail = info.getProductDetail();
			if(StringUtils.isNotBlank(productDetail)){
				List<UserOrderProductDTO> products = JSONArray.parseArray(productDetail, UserOrderProductDTO.class);
				ret.setProducts(products);
			}
		}
		return ret;
	}
}
