package com.khy.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.khy.common.Constants;
import com.khy.entity.OrderInfo;
import com.khy.entity.UserBill;
import com.khy.exception.BusinessException;
import com.khy.mapper.OrderInfoMapper;
import com.khy.mapper.UserBillMapper;
import com.khy.mapper.dto.BillInfoDTO;
import com.khy.mapper.dto.PayProductDetailDTO;
import com.khy.schedule.BillTask;
import com.khy.service.OrderService;
import com.khy.utils.PhoneUtils;
@Service
@Transactional
public class OrderServiceImpl extends BaseService implements OrderService {
	public final static Logger logger = LoggerFactory.getLogger(BillTask.class);
	@Autowired
	private OrderInfoMapper orderInfoMapper;
	@Autowired
	private UserBillMapper userBillMapper;
	@Autowired
	private PhoneUtils PhoneUtils;
	
	@Override
	public List<OrderInfo> listNotBillOrder() {
		return orderInfoMapper.listNotBillOrder();
	}

	@Override
	public List<String> getNotPayOrder() {
		return orderInfoMapper.getNotPayOrder();
	}
	
	@Override
	public List<OrderInfo> getNotConfirmOrder() {
		return orderInfoMapper.getNotConfirmOrder();
	}
	@Override
	public List<String> noRechargeOrder() {
		return orderInfoMapper.noRechargeOrder();
	}
	
	@Override
	public List<String> getNotCommission() {
		return orderInfoMapper.notCommissionOrder();
	}
	
	
	@Override
	public synchronized int saveBill(OrderInfo orderInfo) {
		logger.info("订单出账账单的设置开始--->orderInfo={}",JSON.toJSON(orderInfo));
		int i =0;
		if(null == orderInfo){
			return i;
		}
		orderInfo = orderInfoMapper.notSaveBillOrder(orderInfo);
		if(null == orderInfo){
			return i;
		}
		String orderId = orderInfo.getOrderId();
		String uid = orderInfo.getUid();
		OrderInfo info = new OrderInfo();
		info.setOrderId(orderId);
		info.setUid(uid);
		info.setIsBill(Constants.ORDER_ISBILL_YCZ);
		orderInfoMapper.update(info); 
		List<UserBill>list = getBills(orderInfo);
		if(CollectionUtils.isNotEmpty(list)){
			//保存账单内容
			for (UserBill userBill : list) {
				i = userBillMapper.insert(userBill);
			}
		} else{
			return i;
		}
		return i;
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
			//点卡/余额抵扣/rmb 
			//商品订单都有一个进价的支出账单内容
			String productDetail = orderInfo.getProductDetail();
			if(StringUtils.isBlank(productDetail)){
				return bills;
			}
			UserBill bill = new UserBill();
			bill.setUid(uid);
			bill.setType(Constants.BILL_PAY);
			bill.setBillType(Constants.BILL_PRODUCT);
			bill.setOrderId(orderId);
			bill.setDescription("商品进价账单");
			bill.setCreateTime(now);
			dtos = setProductBillInfo(productDetail,Constants.BILL_PAY);//设置商品出账
			bill.setAmount(orderInfo.getTotalCost());
			if(CollectionUtils.isEmpty(dtos)){
				return  bills;
			}
			bill.setInfo(JSON.toJSONString(dtos));
			bills.add(bill);
			
			if(payType == Constants.CARD_PAY){//付款方式等于点卡的只有出账的内容;
				return  bills;
			}
			if(null == orderInfo.getRmb() || orderInfo.getRmb().compareTo(new BigDecimal("0.00")) == 0){
				//表示全部余额抵扣的
				return  bills;
			}
			bill = new UserBill();
			bill.setUid(uid);
			bill.setType(Constants.BILL_INCOME);
			bill.setBillType(Constants.BILL_PRODUCT);
			bill.setOrderId(orderId);
			bill.setAmount(orderInfo.getRmb());
			bill.setDiscount(orderInfo.getDiscount());
			bill.setPostage(orderInfo.getPostage());
			bill.setDescription("RMB购买商品");
			bill.setCreateTime(now);
			dtos = setProductBillInfo(productDetail,Constants.BILL_INCOME);//设置商品出账
			if(CollectionUtils.isEmpty(dtos)){
				return  bills;
			}
			bill.setInfo(JSON.toJSONString(dtos));
			bills.add(bill);
		}
		return bills;
	}
	
	private List<BillInfoDTO> setProductBillInfo(String productDetail, int type) {
		List<BillInfoDTO> dtos = new ArrayList<>();
		List<PayProductDetailDTO> productList = JSONArray.parseArray(productDetail, PayProductDetailDTO.class);
		BigDecimal amount = new BigDecimal("0.00");
		if(CollectionUtils.isNotEmpty(productList)){
			for (PayProductDetailDTO product : productList) {
				BillInfoDTO dto = new BillInfoDTO();
				dto.setProductName(product.getProductName());
				dto.setProductType("商品");
				dto.setAmount(product.getAmount());
				if(type == Constants.BILL_PAY){//标识支出的
					dto.setPrice(product.getCostPrice());
					BigDecimal cost = new BigDecimal(product.getCost().toString());
					dto.setTotal(cost);
					dto.setDescription("商品的进价成本");
					amount = amount.add(cost);
				}else{
					dto.setPrice(product.getProductPrice());
					dto.setTotal(new BigDecimal(product.getTotal().toString()));
					dto.setDescription("商品销售");
				}
				dtos.add(dto);
			}
		}
		return dtos;
	}

	@Override
	public synchronized int setNotPayOrder(String orderId) {
		int i = 0;
		if(StringUtils.isBlank(orderId)){
			return i;
		}
		OrderInfo orderInfo = orderInfoMapper.getNotPayOrderById(orderId);
		if(null == orderInfo){
			return i;
		}
		Integer orderType = orderInfo.getOrderType();
		if(orderType == Constants.PAY_PRODUCT){
			//需要还原用户购物下单时扣除的余额抵扣内容
			setUserMoney(orderInfo);
		}
		//如果是商品订单未支付-->还需要将用户已经扣除的余额抵扣加回来;
		OrderInfo updateInfo = new OrderInfo();
		updateInfo.setPayStatus(Constants.ORDER_PAYSTATUS_YQX);
		updateInfo.setStatus(Constants.ORDER_STATUS_WC);
		updateInfo.setUid(orderInfo.getUid());
		updateInfo.setOrderId(orderId);
		int flag = orderInfoMapper.update(updateInfo);
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

	@Override
	public synchronized int recharge(String orderId) {
		int i = 0;
		if(StringUtils.isBlank(orderId)){
			return i;
		}
		OrderInfo info = orderInfoMapper.noRechargeOrderById(orderId);
		if(null == info){
			return i;
		}
		try {
			//先给用户手机充值话费内容
			int cardnum = info.getTotalMoney().intValue();
			String phone = info.getDescription();
			logger.info("给用户phone={},的手机号码充值money={},订单的orderId={}",phone,cardnum,orderId);
			JSONObject json = PhoneUtils.onlineOrder(phone, cardnum, orderId);
			Integer error_code = json.getInteger("error_code");
			if(null != error_code || error_code.intValue() == 0){
				//如果手机话费充值正常更新订单的状态
				OrderInfo updateInfo = new OrderInfo();
				updateInfo.setUid(info.getUid());
				updateInfo.setOrderId(orderId);
				updateInfo.setPayStatus(Constants.ORDER_PAYSTATUS_YFK);
				updateInfo.setStatus(Constants.ORDER_STATUS_WC);
				i = orderInfoMapper.update(updateInfo);
			}else{
				//表示话费充值异常
				logger.error("定时给用户代充异常e={}",json.toString());
				throw new BusinessException("话费充值异常"+json.getString("reason"));
			}
		} catch (Exception e) {
			logger.error("定时给用户代充异常"+e.getMessage());
			throw new BusinessException("定时给用户代充异常"+e.getMessage());
		}
		return i;
	}

	@Override
	public synchronized int setOrderCommission(String orderId) {
		int i = 0;
		if(StringUtils.isBlank(orderId)){
			return i;
		}
		OrderInfo info = orderInfoMapper.notCommissionOrderById(orderId);
		if(null == info){
			return i;
		}
		//
		setCommission(orderId);
		return 0;
	}

	public static void main(String[] args) {
		BigDecimal b1 = new  BigDecimal("0.00");
		UserBill bill = new UserBill();
		bill.setAmount(b1);
		System.out.println(JSON.toJSON(bill));
		int num =1;
		while(true){
			if(num >10){
				break;
			}
			b1 = b1.add(new BigDecimal("1.22"));
			num++;
		}
		bill.setAmount(b1);
		System.out.println(JSON.toJSON(bill));
	}
}
