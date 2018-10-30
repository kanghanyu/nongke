package com.khy.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.khy.common.Constants;
import com.khy.entity.OrderInfo;
import com.khy.entity.UserBill;
import com.khy.exception.BusinessException;
import com.khy.mapper.OrderInfoMapper;
import com.khy.mapper.UserBillMapper;
import com.khy.mapper.dto.BillInfoDTO;
import com.khy.schedule.BillTask;
import com.khy.service.OrderService;
@Service
@Transactional
public class OrderServiceImpl extends BaseService implements OrderService {
	public final static Logger logger = LoggerFactory.getLogger(BillTask.class);
	@Autowired
	private OrderInfoMapper orderInfoMapper;
	@Autowired
	private UserBillMapper userBillMapper;
	
	@Override
	public List<OrderInfo> listNotBillOrder() {
		return orderInfoMapper.listNotBillOrder();
	}

	@Override
	public List<OrderInfo> getNotPayOrder() {
		return orderInfoMapper.getNotPayOrder();
	}
	
	@Override
	public List<OrderInfo> getNotConfirmOrder() {
		return orderInfoMapper.getNotConfirmOrder();
	}
	
	
	@Override
	public synchronized  void saveBill(OrderInfo orderInfo) {
		logger.info("订单出账账单的设置开始--->orderInfo={}",JSON.toJSON(orderInfo));
		Integer isBill = orderInfo.getIsBill();
		if(isBill == Constants.ORDER_ISBILL_YCZ){
			return;
		}
		String orderId = orderInfo.getOrderId();
		Integer orderType = orderInfo.getOrderType();
		String uid = orderInfo.getUid();
		
		OrderInfo info = new OrderInfo();
		info.setOrderId(orderId);
		info.setUid(uid);
		info.setIsBill(Constants.ORDER_ISBILL_YCZ);
		info.setOrderType(orderType);
		if(orderType == Constants.PAY_VIP){
			info.setPayStatus(Constants.ORDER_PAYSTATUS_YFK);
		}
		
		List<UserBill>list = userBillMapper.list(orderId,uid);
//		OrderInfo info = new OrderInfo();
//		info.setOrderId(orderId);
//		info.setUid(uid);
//		info.setIsBill(Constants.ORDER_ISBILL_YCZ);
		orderInfoMapper.update(info);//先更新订单的状态内容;
		if(CollectionUtils.isEmpty(list)){
			list = getBills(orderInfo);
		} 
		
	}
	
	
	private List<UserBill> getBills(OrderInfo orderInfo) {
		Integer orderType = orderInfo.getOrderType();
		Integer payType = orderInfo.getPayType();
		String orderId = orderInfo.getOrderId();
		String uid = orderInfo.getUid();
		List<UserBill> bills = new ArrayList<>();
		List<BillInfoDTO> dtos = new ArrayList<>();
		Date now = new Date();
		if(orderType == Constants.PAY_VIP){
			UserBill bill = new UserBill();
			bill.setUid(uid);
			bill.setType(Constants.BILL_INCOME);
			bill.setBillType(Constants.BILL_VIP);
			bill.setOrderId(orderId);
			bill.setAmount(orderInfo.getRmb());
			bill.setDescription("VIP升级账单");
			bill.setCreateTime(now);
			BillInfoDTO dto = new BillInfoDTO();
			dto.setProductName("升级VIP");
			dto.setProductType("VIP");
			dto.setPrice(orderInfo.getRmb());
			dto.setAmount(1);
			dto.setTotal(orderInfo.getRmb());
			dto.setDescription("升级VIP");
			dtos.add(dto);
			bill.setInfo(JSON.toJSONString(dtos));
			bills.add(bill);
		}else if(orderType == Constants.PAY_CARD){
			if(payType != Constants.MONEY_PAY){
				UserBill bill = new UserBill();
				bill.setUid(uid);
				bill.setType(Constants.BILL_INCOME);
				bill.setBillType(Constants.BILL_CARD);
				bill.setOrderId(orderId);
				bill.setAmount(orderInfo.getRmb());
				bill.setDescription("购买稻粒");
				bill.setCreateTime(now);
				BillInfoDTO dto = new BillInfoDTO();
				dto.setProductName("购买"+orderInfo.getRmb()+"元稻粒");
				dto.setProductType("稻粒");
				dto.setPrice(orderInfo.getRmb());
				dto.setAmount(1);
				dto.setTotal(orderInfo.getRmb());
				dto.setDescription("购买稻粒");
				dtos.add(dto);
				bill.setInfo(JSON.toJSONString(dtos));
				bills.add(bill);
			}
		}else if(orderType == Constants.PAY_PHONE){
			//是不是要话费已经充值过之后才能产生账单内容
			UserBill bill = new UserBill();
			bill.setUid(uid);
			bill.setType(Constants.BILL_INCOME);
			bill.setBillType(Constants.BILL_PHONE);
			bill.setOrderId(orderId);
			bill.setAmount(orderInfo.getRmb());
			bill.setDescription("手机话费充值");
			bill.setCreateTime(now);
			bill.setDiscount(orderInfo.getDiscount());
			BillInfoDTO dto = new BillInfoDTO();
			dto.setProductName("充值了"+orderInfo.getTotalMoney()+"元话费");
			dto.setProductType("手机话费");
			dto.setPrice(orderInfo.getRmb());
			dto.setAmount(1);
			dto.setTotal(orderInfo.getRmb());
			dto.setDescription("手机话费");
			dtos.add(dto);
			bill.setInfo(JSON.toJSONString(dtos));
			bills.add(bill);
			//充值出去的支出的账单内容
			bill = new UserBill();
			bill.setUid(uid);
			bill.setType(Constants.BILL_PAY);
			bill.setBillType(Constants.BILL_PHONE);
			bill.setOrderId(orderId);
			bill.setAmount(orderInfo.getTotalMoney());
			bill.setDescription("给用户充值话费");
			bill.setCreateTime(now);
			dto = new BillInfoDTO();
			dto.setProductName("代充值了"+orderInfo.getTotalMoney()+"元话费");
			dto.setProductType("手机话费");
			dto.setPrice(orderInfo.getTotalMoney());
			dto.setAmount(1);
			dto.setTotal(orderInfo.getRmb());
			dto.setDescription("代冲手机话费");
			dtos = new ArrayList<>();
			dtos.add(dto);
			bill.setInfo(JSON.toJSONString(dtos));
			bills.add(bill);
		}else if(orderType == Constants.PAY_PRODUCT){
			
			
			
		}
		
		
		
		return null;
	}
	
	public void insert(int i) {
		UserBill bill = new UserBill();
		bill.setUid("测试uid"+i);;
		userBillMapper.insert(bill);
		if(i == 5){
			throw new BusinessException("测试异常内容");
		}
	}

	@Override
	public int setNotPayOrder(OrderInfo orderInfo) {
		int i = 0;
		if(null == orderInfo){
			return i;
		}
		
		//如果是商品订单未支付-->还需要将用户已经扣除的余额抵扣加回来;
		Integer orderType = orderInfo.getOrderType();
		OrderInfo updateInfo = new OrderInfo();
		String orderId = orderInfo.getOrderId();
		updateInfo.setPayStatus(Constants.ORDER_PAYSTATUS_YQX);
		updateInfo.setStatus(Constants.ORDER_STATUS_WC);
		updateInfo.setUid(orderInfo.getUid());
		updateInfo.setOrderId(orderId);
		int flag = orderInfoMapper.update(updateInfo);
		if(orderType == Constants.PAY_PRODUCT){
			//需要还原用户购物下单时扣除的余额抵扣内容
			setUserMoney(orderInfo);
		}
		return flag;
	}

	@Override
	public int setConfirmOrder(OrderInfo orderInfo) {
		int i = 0;
		if(null == orderInfo){
			return i;
		}
		String orderId = orderInfo.getOrderId();
		OrderInfo updateInfo = new OrderInfo();
		updateInfo.setUid(orderInfo.getUid());
		updateInfo.setOrderId(orderId);
		updateInfo.setPayStatus(Constants.ORDER_PAYSTATUS_YFK);
		updateInfo.setStatus(Constants.ORDER_STATUS_WC);
		int flag = orderInfoMapper.update(updateInfo);
		return flag;
	}
}
