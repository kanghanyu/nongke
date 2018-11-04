package com.khy.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.khy.common.Constants;
import com.khy.common.JsonResponse;
import com.khy.entity.BaseEntity;
import com.khy.entity.Message;
import com.khy.entity.OnlineParame;
import com.khy.entity.OrderInfo;
import com.khy.entity.User;
import com.khy.entity.UserAddress;
import com.khy.entity.UserBank;
import com.khy.entity.UserCash;
import com.khy.entity.UserInviter;
import com.khy.entity.UserRecord;
import com.khy.exception.BusinessException;
import com.khy.interceptor.LoginInterceptor;
import com.khy.mapper.MessageMapper;
import com.khy.mapper.OrderInfoMapper;
import com.khy.mapper.UserAddressMapper;
import com.khy.mapper.UserBankMapper;
import com.khy.mapper.UserCashMapper;
import com.khy.mapper.UserInviterMapper;
import com.khy.mapper.UserMapper;
import com.khy.mapper.UserRecordMapper;
import com.khy.mapper.dto.CartMoneyDTO;
import com.khy.mapper.dto.UserBillDTO;
import com.khy.mapper.dto.UserInviterDTO;
import com.khy.mapper.dto.UserOrderInfoDTO;
import com.khy.mapper.dto.UserOrderProductDTO;
import com.khy.mapper.dto.UserPhoneRecord;
import com.khy.mapper.dto.UserRecordDTO;
import com.khy.service.UesrService;
import com.khy.utils.BeanUtils;
import com.khy.utils.FileUtils;
import com.khy.utils.SessionHolder;
import com.khy.utils.Utils;
@Service
@Transactional
public class UesrServiceImpl extends BaseService implements UesrService {
	public final static Logger logger = LoggerFactory.getLogger(LoginInterceptor.class);
	private final static BigDecimal ZERO = new BigDecimal("0.00");
	@Autowired
	private UserMapper userMapper;
	@Autowired
	private UserAddressMapper userAddressMapper;
	@Autowired
	private UserBankMapper userBankMapper;
	@Autowired
	private UserCashMapper userCashMapper;
	@Autowired
	private CacheService cacheService;
	@Autowired
	private UserInviterMapper userInviterMapper;
	@Autowired
	private UserRecordMapper userRecordMapper;
	@Autowired
	private MessageMapper messageMapper;
	@Autowired
	private OrderInfoMapper orderInfoMapper;
	@Override
	public JsonResponse<User> login(User user) {
		JsonResponse<User> jsonResponse = new JsonResponse<>();
		logger.info("登录接口请求参数内容user={}"+JSON.toJSONString(user));
		if(null == user){
			jsonResponse.setRetDesc("user不能为空");
			return jsonResponse;
		}
		String phone = user.getPhone();
		String password = user.getPassword();
		if(StringUtils.isBlank(phone)){
			jsonResponse.setRetDesc("手机号不能为空");
			return jsonResponse;
		}
		if(StringUtils.isBlank(password)){
			jsonResponse.setRetDesc("密码不能为空");
			return jsonResponse;
		}
		User userDb = userMapper.login(phone);
		if(null == userDb){
			jsonResponse.setRetDesc("当前账户不存在");
			return jsonResponse;
		}
		if(userDb.getStatus() == 1){
			jsonResponse.setRetDesc("您的账户被冻结请联系客服");
			return jsonResponse;
		}
		if(!userDb.getPassword().equals(DigestUtils.md5DigestAsHex(password.getBytes()))){
			jsonResponse.setRetDesc("密码错误");
			return jsonResponse;
		}
		userDb.hide(userDb);
		String token = Utils.getToken();
		userDb.setToken(userDb.getUid()+":"+token);
		cacheService.setString(Constants.USER_LOGIN.concat(userDb.getUid()+":").concat(token), JSON.toJSONString(userDb),Constants.SEVEN_DAY);
		jsonResponse.success(userDb,"用户登录成功");
		logger.info("登录接口响应的参数内容"+JSON.toJSONString(jsonResponse));
		return jsonResponse;
	}

	@Override
	public JsonResponse<User> getUserInfo() {
		JsonResponse<User> jsonResponse = new JsonResponse<>();
		User user = SessionHolder.currentUser();
		if(null == user){
			jsonResponse.setRetDesc("请重新登录");
			return jsonResponse;
		}
		String phone = user.getPhone();
		String token = user.getToken();
		User userDb = userMapper.getUserByPhone(phone);
		if(userDb == null){
			jsonResponse.setRetDesc("当前用户");
			return jsonResponse;
		}
		userDb.hide(userDb);
		userDb.setToken(token);
		cacheService.setString(Constants.USER_LOGIN.concat(token), JSON.toJSONString(userDb),Constants.SEVEN_DAY);
		jsonResponse.success(userDb);
		return jsonResponse;
	}
	@Override
	public JsonResponse<Boolean> uploadImg(MultipartRequest request) {
		JsonResponse<Boolean> jsonResponse = new JsonResponse<>();
		Iterator<String> fileNames = request.getFileNames();
		if(null == fileNames && !fileNames.hasNext()){
			jsonResponse.setRetDesc("文件不也能为空");
			return jsonResponse;
		}
		String next = fileNames.next();
		MultipartFile file = request.getFile(next);
		if(null == file || file.getSize() <= 0 ){
			jsonResponse.setRetDesc("文件不也能为空");
			return jsonResponse;
		}
		
		String mimetype = file.getContentType();
		String type = mimetype.split("/")[0];
		if(!type.equals("image")){
			jsonResponse.setRetDesc("文件必须是图片类型");
			return jsonResponse;
		}
		User user = SessionHolder.currentUser();
		if (null == user) {
			jsonResponse.setRetDesc("请重新登录");
			return jsonResponse;
		}
		String uid = user.getUid();
		String originalFilename = file.getOriginalFilename();
		String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));		
		String fileName ="img/"+uid+"/"+Utils.getFileName()+suffix;
		FileUtils.uploadImg(file,fileName);
		user.setImg(Constants.BASE_URL+fileName);
		int flag = userMapper.updateUser(user);
		if(flag > 0){
			jsonResponse.success(true,"修改头像成功");
		}else{
			jsonResponse.success(false);
		}
		return jsonResponse;
	}
	
	@Override
	public JsonResponse<Boolean> register(User user) {
		logger.info("注册接口请求参数内容user={}"+JSON.toJSONString(user));
		JsonResponse<Boolean> jsonResponse = new JsonResponse<>();
		if(null == user){
			jsonResponse.setRetDesc("user不能为空");
			return jsonResponse;
		}
		String phone = user.getPhone();
		String password = user.getPassword();
		String confirmPassword = user.getConfirmPassword();
		String code = user.getCode();
		if(StringUtils.isBlank(phone)){
			jsonResponse.setRetDesc("手机号不能为空");
			return jsonResponse;
		}
		if(StringUtils.isBlank(password)){
			jsonResponse.setRetDesc("密码不能为空");
			return jsonResponse;
		}
		if(StringUtils.isBlank(confirmPassword)){
			jsonResponse.setRetDesc("确认密码不能为空");
			return jsonResponse;
		}
		if(!password.equals(confirmPassword)){
			jsonResponse.setRetDesc("两次密码不一致");
			return jsonResponse;
		}
		if(StringUtils.isBlank(code)){
			jsonResponse.setRetDesc("验证码不能为空");
			return jsonResponse;
		}
		String key = Constants.USER_SMS_REGISTER+phone;
		String redisCode = cacheService.getString(key);
		if(StringUtils.isBlank(redisCode)){
			jsonResponse.setRetDesc("验证码已失效");
			return jsonResponse;
		}
		if(!code.equals(redisCode)){
			jsonResponse.setRetDesc("验证码错误");
			return jsonResponse;
		}
		User userDb = userMapper.getUserByPhone(phone);
		if(null != userDb){
			jsonResponse.setRetDesc("手机号已注册");
			return jsonResponse;
		}
		Date now = new Date();
		String uid = getUid();
		user.setUid(uid);
		//邀请人
		String inviterUid = user.getInviterUid();
		if(StringUtils.isNotBlank(inviterUid)){
			User inviterUser = userMapper.getUserByUid(inviterUid);
			if(null != inviterUser){
				if(inviterUser.getIsVip() == Constants.GENERAL_UER){
					jsonResponse.setRetDesc("非VIP用户不支持邀请");
					return jsonResponse;
				}
				user.setInviterPhone(inviterUser.getPhone());
				user.setInviterUid(inviterUid);
				//设置 邀请用户的列表数据
				UserInviter inviter = new UserInviter();
				inviter.setUid(inviterUid);
				inviter.setInvitedUid(uid);
				inviter.setCreateTime(now);
				userInviterMapper.insert(inviter);
			}
		}
		user.setCreateTime(now);
		user.setMoney(ZERO);
		user.setCardMoney(ZERO);
		user.setCommission(ZERO);
		user.setImgUrl(Constants.INVITE_USER_REGISTER+uid);
		user.setIsManager(0);
		user.setIsVip(0);
		user.setStatus(0);
		user.setPassword(DigestUtils.md5DigestAsHex(password.getBytes()));
		int flag = userMapper.insert(user);
		if(flag >= 1){
			cacheService.delKey(key);
			Map<String, String> online = getOnline();
			if(null != online){
				Map<String, Object> extra = new HashMap<String, Object>();
				extra.put("url", online.get(Constants.REGISTER_REDIRECT_URL));
				jsonResponse.setExtra(extra );
			}
			jsonResponse.success(true);
		}
		logger.info("注册接口响应的参数内容"+JSON.toJSONString(jsonResponse));
		return jsonResponse;
	}

	private String getUid() {
		String uid ;
		while(true){
			uid = Utils.getUid();
			User user = userMapper.getUserByUid(uid);
			if(user == null){
				break;
			}
		}
		return uid;
	}

	@Override
	public JsonResponse<Boolean> resetPassword(User user) {
		logger.info("修改密码的接口请求参数内容user={}"+JSON.toJSONString(user));
		JsonResponse<Boolean> jsonResponse = new JsonResponse<>();
		if(null == user){
			jsonResponse.setRetDesc("请求参数不能为空");
			return jsonResponse;
		}
		if(StringUtils.isAnyBlank(user.getPhone(),user.getPassword(),user.getConfirmPassword(),user.getCode())){
			jsonResponse.setRetDesc("请求参数不能为空");
			return jsonResponse;
		}
		if(!user.getPassword().equals(user.getConfirmPassword())){
			jsonResponse.setRetDesc("两次密码不一致");
			return jsonResponse;
		}
		
		String key = Constants.USER_SMS_FIND_PASSWORD+user.getPhone();
		String redisCode = cacheService.getString(key);
		if(StringUtils.isBlank(redisCode)){
			jsonResponse.setRetDesc("验证码已过期,重新获取");
			return jsonResponse;
		}
		if(!user.getCode().equals(redisCode)){
			jsonResponse.setRetDesc("验证码错误,请重新输入");
			return jsonResponse;
		}
		
		User userDb = userMapper.getUserByPhone(user.getPhone());
		if(null == userDb){
			jsonResponse.setRetDesc("当前用户不存在");
			return jsonResponse;
		}
		userDb.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
		int num = userMapper.updateUser(userDb);
		if(num>0){
			jsonResponse.success(true);
			jsonResponse.setRetDesc("修改密码成功");
			cacheService.delKey(key);
			return jsonResponse;
		}else{
			jsonResponse.setRetDesc("修改密码失败");
			jsonResponse.setRspBody(false);
		}
		return jsonResponse;
	}
	
	
	@Override
	public JsonResponse<Boolean> loginOut() {
		JsonResponse<Boolean> jsonResponse = new JsonResponse<>();
		User user = SessionHolder.currentUser();
		cacheService.delKey(Constants.USER_LOGIN.concat(user.getUid()+":").concat(user.getToken()));
		SessionHolder.cleanUser();
		jsonResponse.success(true);
		logger.info("退出登录接口响应的参数内容"+JSON.toJSONString(jsonResponse));
		return jsonResponse;
	}

	@Override
	public JsonResponse<Boolean> saveOrUpdateUserAddress(UserAddress userAddress) {
		logger.info("保存用户的地址请求参数内容user={}"+JSON.toJSONString(userAddress));
		JsonResponse<Boolean>jsonResponse = new JsonResponse<>();
		if(null == userAddress){
			jsonResponse.setRetDesc("参数不能为空");
			return jsonResponse;
		}
		User user = SessionHolder.currentUser();
		if(null == user){
			jsonResponse.setRetDesc("请重新登录");
			return jsonResponse;
		}
		String uid = user.getUid();
		UserAddress userAddressDb = userAddressMapper.getByUid(uid);
		Date now = new Date();
		int flag = 0;
		if(null == userAddressDb){
			userAddressDb = new UserAddress();
			BeanUtils.copyProperties(userAddress, userAddressDb);
			userAddressDb.setUid(uid);
			userAddressDb.setCreateTime(now);
			flag = userAddressMapper.insert(userAddressDb);
		}else{
			BeanUtils.copyProperties(userAddress, userAddressDb);
			userAddressDb.setCreateTime(now);
			flag = userAddressMapper.update(userAddressDb);
		}
		if(flag > 0){
			jsonResponse.success(true);
		}else{
			jsonResponse.success(false);
		}
		logger.info("新增/修改的地址接口响应的参数内容"+JSON.toJSONString(jsonResponse));
		return jsonResponse;
	}

	@Override
	public JsonResponse<UserAddress> getUserAddress() {
		JsonResponse<UserAddress> jsonResponse = new JsonResponse<>();
		User user = SessionHolder.currentUser();
		if(null == user){
			jsonResponse.setRetDesc("请重新登录");
			return jsonResponse;
		}
		String uid = user.getUid();
		
		UserAddress userAddress = userAddressMapper.getByUid(uid);
		if(null != userAddress){
			jsonResponse.success(userAddress);
		}else{
			jsonResponse.setRetDesc("您还没有设置收货地址");
		}
		logger.info("获取用户的地址接口响应的参数内容"+JSON.toJSONString(jsonResponse));
		return jsonResponse;
	}

	@Override
	public JsonResponse<Boolean> saveOrUpdateUserBank(UserBank userBank) {
		logger.info("新增/修改用户银行卡信息接口接受的参数内容"+JSON.toJSONString(userBank));
		JsonResponse<Boolean>jsonResponse = new JsonResponse<>();
		User user = SessionHolder.currentUser();
		if(null == user){
			jsonResponse.setRetDesc("请重新登录");
			return jsonResponse;
		}
		String key = Constants.USER_SMS_UPDATE_BANKINFO+user.getPhone();
		String redisCode = cacheService.getString(key);
		if(StringUtils.isBlank(redisCode)){
			jsonResponse.setRetDesc("验证码已失效");
			return jsonResponse;
		}
		if(!userBank.getCode().equals(redisCode)){
			jsonResponse.setRetDesc("验证码错误");
			return jsonResponse;
		}
		String uid = user.getUid();
		UserBank bank = userBankMapper.getByUid(uid);
		int flag = 0;
		Date date = new Date();
		if(null == bank){
			//插入
			bank = new UserBank();
			BeanUtils.copyProperties(userBank, bank);
			bank.setUid(uid);
			bank.setCreateTime(date);
			bank.setUpdateTime(date);
			flag = userBankMapper.insert(bank);
		}else{
			//更新
			BeanUtils.copyProperties(userBank, bank);
			bank.setUpdateTime(date);
			flag = userBankMapper.update(bank);
		}
		if(flag > 0){
			cacheService.delKey(key);
			jsonResponse.success(true);
		}else{
			jsonResponse.success(false);
		}
		logger.info("新增/修改用户银行卡信息接口响应的参数内容"+JSON.toJSONString(jsonResponse));
		return jsonResponse;
	}

	@Override
	public JsonResponse<UserBank> getUserBank() {
		JsonResponse<UserBank>jsonResponse = new JsonResponse<>();
		User user = SessionHolder.currentUser();
		if(null == user){
			jsonResponse.setRetDesc("请重新登录");
			return jsonResponse;
		}
		String uid = user.getUid();
		UserBank bank = userBankMapper.getByUid(uid);
		if(null != bank){
			jsonResponse.success(bank);
		}else{
			jsonResponse.setRspBody(null);
			jsonResponse.setRetDesc("暂无银行卡信息");
		}
		logger.info("获取用户银行卡信息接口响应的参数内容"+JSON.toJSONString(jsonResponse));
		return jsonResponse;
	}

	@Override
	public JsonResponse<Boolean> saveUserCash(UserCash userCash) {
		logger.info("体现接口请求的参数内容"+JSON.toJSONString(userCash));
		JsonResponse<Boolean>jsonResponse = new JsonResponse<>();
		if(null == userCash|| null== userCash.getAmount()){
			jsonResponse.setRetDesc("参数不能为空");
			return jsonResponse;
		}
		BigDecimal amount = userCash.getAmount();
		if(amount.compareTo(ZERO) <= 0){
			jsonResponse.setRetDesc("体现金额必须大于零");
			return jsonResponse;
		}
		if(amount.intValue()%100 != 0){
			jsonResponse.setRetDesc("体现金额必须为100整数倍");
			return jsonResponse;
		}
		if(StringUtils.isBlank(userCash.getBankNum())){
			jsonResponse.setRetDesc("银行账户不能为空");
			return jsonResponse;
		}
		User user = SessionHolder.currentUser();
		if(null == user){
			jsonResponse.setRetDesc("请重新登录");
			return jsonResponse;
		}
		//先去查询用户当前有多少可提现的金额-->否则体现失败
		String uid = user.getUid();
		JSONObject json = getUserByUidAndLock(uid);
		if(json.getIntValue("code")==1000){
			jsonResponse.setRetDesc(json.getString("msg"));
			return jsonResponse;
		}
		User userDb = json.getObject("user", User.class);
		if(null == userDb){
			jsonResponse.setRetDesc("当前用户不存在");
			return jsonResponse;
		}
		BigDecimal money = userDb.getMoney() != null ? userDb.getMoney():ZERO;
		if(money.compareTo(amount)<0){
			jsonResponse.setRetDesc("当前用户余额不足,体现失败");
			return jsonResponse;
		}
		Date now = new Date();
		userCash.setUid(uid);
		userCash.setFeeAmount(new BigDecimal("5.00"));;
		userCash.setRealAmount(amount.subtract(userCash.getFeeAmount()));
		userCash.setApplyTime(now);
		userCash.setStatus(0);
		try {
			int flag = userCashMapper.insert(userCash);
			//TODO 是否需要设置 体现的流水内容
			if(flag > 0){
				//更新用户的余额内容
				BigDecimal moneyDb = userDb.getMoney();
				userDb.setMoney(moneyDb.subtract(amount));
				userMapper.updateUser(userDb);
				//记录用户的余额流水内容
				String descr = "体现"+amount+"元";
				saveUserRecord(uid,Constants.RECORD_PAY,Constants.RECORD_MONEY,amount,moneyDb,userCash.getId().toString(),descr,now);
				jsonResponse.success(true);
			}else{
				jsonResponse.success(false);
			}
		} catch (Exception e) {
			logger.error("用户体现接口异常e={}",e.getMessage());
			throw new BusinessException("用户体现异常"+e.getMessage());
		}finally {
			cacheService.releaseLock(Constants.LOCK_USER+uid);
		}
		logger.info("体现接口响应的参数内容"+JSON.toJSONString(jsonResponse));
		return jsonResponse;
	}

	@Override
	public JsonResponse<List<UserCash>> listUserCash() {
		JsonResponse<List<UserCash>> jsonResponse = new JsonResponse<>();
		User user = SessionHolder.currentUser();
		if(null == user){
			jsonResponse.setRetDesc("请重新登录");
			return jsonResponse;
		}
		String uid = user.getUid();
		List<UserCash> list = userCashMapper.listUserCash(uid);
		if(CollectionUtils.isNotEmpty(list)){
			jsonResponse.success(list);
		}else{
			jsonResponse.setRetDesc("暂无体现记录");
		}
		logger.info("获取体现记录的接口响应的参数内容"+JSON.toJSONString(jsonResponse));
		return jsonResponse;
	}

	@Override
	public JsonResponse<Map<String, OnlineParame>> getOnlineParame() {
		JsonResponse<Map<String, OnlineParame>> jsonResponse = new JsonResponse<>();
		Map<String,OnlineParame>map = getOnlineParameInfo();
		if(null != map ){
			jsonResponse.success(map, "获取在线参数成功");
		}else{
			jsonResponse.setRetDesc("未获取到在线参数,请联系管理员");
		}
		return jsonResponse;
	}

	@Override
	public JsonResponse<List<UserInviterDTO>> listUserInviter(UserInviter userInviter) {
		JsonResponse<List<UserInviterDTO>>jsonResponse = new JsonResponse<>();
		if(null == userInviter || null == userInviter.getUid()){
			jsonResponse.setRetDesc("参数不能为空");
			return jsonResponse;
		}
		User user = SessionHolder.currentUser();
		if(null == user){
			jsonResponse.setRetDesc("请重新登录");
			return jsonResponse;
		}
		List<UserInviterDTO> list = userInviterMapper.listUserInviterByUid(userInviter.getUid());
		jsonResponse.success(list);
		return jsonResponse;
	}

	@Override
	public JsonResponse<Boolean> commissionToMoney(BigDecimal commission) {
		logger.info("用户佣金转成余额的接口 请求参数commission="+commission);
		JsonResponse<Boolean>jsonResponse = new JsonResponse<>();
		if(null == commission){
			jsonResponse.setRetDesc("请求参数不为空");
			return jsonResponse;
		}
		if(commission.compareTo(ZERO) == 0){
			jsonResponse.setRetDesc("转账佣金不为0");
			return jsonResponse;
		}
		User user = SessionHolder.currentUser();
		if(null == user){
			jsonResponse.setRetDesc("请重新登录");
			return jsonResponse;
		}
		//先去查询用户当前有多少可提现的金额-->否则体现失败
		String uid = user.getUid();
		JSONObject json = getUserByUidAndLock(uid);
		if(json.getIntValue("code")==1000){
			jsonResponse.setRetDesc(json.getString("msg"));
			return jsonResponse;
		}
		User userDb = json.getObject("user", User.class);
		if(null == userDb){
			jsonResponse.setRetDesc("当前用户不存在");
			return jsonResponse;
		}
		BigDecimal commissionDb = userDb.getCommission() != null ? userDb.getCommission():ZERO;
		BigDecimal money = userDb.getMoney() != null ? userDb.getMoney():ZERO;
		if(commissionDb.compareTo(commission) < 0){
			jsonResponse.setRetDesc("当前账户佣金不足");
			return jsonResponse;
		}
		try {
			//更新用户的
			userDb.setCommission(commissionDb.subtract(commission));
			userDb.setMoney(money.add(commission));
			userMapper.updateUser(userDb);
			Date now = new Date();
			String descr ="佣金转余额";
			saveUserRecord(uid,Constants.RECORD_PAY,Constants.RECORD_COMMISSION,commission,commissionDb,null,descr,now);
			saveUserRecord(uid,Constants.RECORD_INCOME,Constants.RECORD_MONEY,commission,money,null,descr,now);
			jsonResponse.success(true);
		} catch (Exception e) {
			logger.error("用户佣金转成余额的接口异常e={}",e.getMessage());
			throw new BusinessException("用户佣金转成余额的接口"+e.getMessage());
		}finally {
			cacheService.releaseLock(Constants.LOCK_USER+uid);
		}
		return jsonResponse;
	}

	@Override
	public JsonResponse<Boolean> cardMoneyToUser(CartMoneyDTO dto) {
		logger.info("用户点卡转给别人接口 请求参数"+JSONObject.toJSONString(dto));
		JsonResponse<Boolean>jsonResponse = new JsonResponse<>();
		if(null == dto){
			jsonResponse.setRetDesc("请求参数不为空");
			return jsonResponse;
		}
		String phone = dto.getPhone();
		BigDecimal amount = dto.getAmount();
		if(null == amount || StringUtils.isBlank(phone)){
			jsonResponse.setRetDesc("手机号/转账金额不为空");
			return jsonResponse;
		}
		if(amount.compareTo(ZERO) == 0){
			jsonResponse.setRetDesc("转账点卡金额不为0");
			return jsonResponse;
		}
		User user = SessionHolder.currentUser();
		if(null == user){
			jsonResponse.setRetDesc("请重新登录");
			return jsonResponse;
		}
		if(phone.equals(user.getPhone())){
			jsonResponse.setRetDesc("点卡转账不能转给自己");
			return jsonResponse;
		}
		//先去查询用户当前有多少可提现的金额-->否则体现失败
		String uid = user.getUid();
		JSONObject json = getUserByUidAndLock(uid);
		if(json.getIntValue("code")==1000){
			jsonResponse.setRetDesc(json.getString("msg"));
			return jsonResponse;
		}
		User userDb = json.getObject("user", User.class);
		if(null == userDb){
			jsonResponse.setRetDesc("当前用户不存在");
			return jsonResponse;
		}
		BigDecimal cardMoney = userDb.getCardMoney() != null ? userDb.getCardMoney():ZERO;
		if(cardMoney.compareTo(amount)< 0){
			jsonResponse.setRetDesc("账户的点卡余额不足");
			return jsonResponse;
		}
		JSONObject jsonUser = getUserByPhoneAndLock(phone);
		if(jsonUser.getIntValue("code")==1000){
			jsonResponse.setRetDesc(jsonUser.getString("msg"));
			return jsonResponse;
		}
		User userOther = jsonUser.getObject("user", User.class);
		if(null == userOther){
			jsonResponse.setRetDesc("对方账户不存在");
			return jsonResponse;
		}
		
		try {
			//更新当前用户的点卡金额
			userDb.setCardMoney(cardMoney.subtract(amount));
			userMapper.updateUser(userDb);
			
			Date now = new Date();
			saveUserRecord(uid,Constants.RECORD_PAY,Constants.RECORD_CARD_MONEY,amount,cardMoney,null,phone,now);
			BigDecimal cardMoneyOther = userOther.getCardMoney()!=null ?userOther.getCardMoney():ZERO;
			userOther.setCardMoney(cardMoneyOther.add(amount));
			userMapper.updateUser(userOther);
			String description =phone+"给您账户点卡转账"+amount+"元";
			saveUserRecord(userOther.getUid(),Constants.RECORD_INCOME,Constants.RECORD_CARD_MONEY,amount,cardMoneyOther,user.getPhone(),description,now);
			jsonResponse.success(true);
		} catch (Exception e) {
			logger.error("用户点卡转给别人的接口异常e={}",e.getMessage());
			throw new BusinessException("用户点卡转给别人的接口"+e.getMessage());
		}finally {
			cacheService.releaseLock(Constants.LOCK_USER+uid);
			cacheService.releaseLock(Constants.LOCK_USER+userOther.getUid());
		}
		return jsonResponse;
	}

	@Override
	public JsonResponse<List<UserRecordDTO>> listUserRecord(Integer type) {
		JsonResponse<List<UserRecordDTO>> jsonResponse = new JsonResponse<>();
		if(null == type){
			jsonResponse.setRetDesc("请求参数不为空");
			return jsonResponse;
		}
		User user = SessionHolder.currentUser();
		if(null == user){
			jsonResponse.setRetDesc("请重新登录");
			return jsonResponse;
		}
		String uid = user.getUid();
		UserRecord record = new UserRecord();
		if(type == 2 || type == 3){
			record.setUid(uid);
			record.setType(type);
			if(type == 2){
				record.setPayType(2);
			}
		}else{
			record.setType(-1);
		}		
		List<UserRecord> list = userRecordMapper.listUserRecord(record);
		List<UserRecordDTO> dtos = new ArrayList<>();
		if(CollectionUtils.isNotEmpty(list)){
			for (UserRecord source : list) {
				UserRecordDTO target = new UserRecordDTO();
				BeanUtils.copyProperties(source, target);
				dtos.add(target);
			}
		}
		jsonResponse.success(dtos);
		return jsonResponse;
	}

	
	
	@Override
	public JsonResponse<List<Message>> listMessage() {
		JsonResponse<List<Message>> jsonResponse = new JsonResponse<>();
		List<Message> list = messageMapper.listAll();
		jsonResponse.success(list);
		return jsonResponse;
	}

	
	@Override
	public JsonResponse<List<UserBillDTO>> listUserBill() {
		JsonResponse<List<UserBillDTO>> jsonResponse = new JsonResponse<>();
		User user = SessionHolder.currentUser();
		if(null == user){
			jsonResponse.setRetDesc("请重新登录");
			return jsonResponse;
		}
		//账单从订单过来---> vip订单/点卡购买订单
		String uid = user.getUid();
		List<OrderInfo> list = orderInfoMapper.getUserBill(uid);
		List<UserBillDTO>ret = new ArrayList<>();
		if(CollectionUtils.isNotEmpty(list)){
			for (OrderInfo info : list) {
				UserBillDTO dto = new UserBillDTO();
				Integer orderType = info.getOrderType();
				dto.setTime(info.getPayTime());
				dto.setDescription(info.getProductDetail());
				if(orderType == Constants.PAY_VIP){
					dto.setTotalDesc(info.getDescription());
				}else{
					BigDecimal totalPay = info.getTotalPay();
					dto.setTotalDesc(totalPay.toString()+"元");
				}
				ret.add(dto);
			}
		}
		jsonResponse.success(ret);
		return jsonResponse;
	}

	@Override
	public JsonResponse<PageInfo<UserOrderInfoDTO>> listUserOrderInfo(BaseEntity entity) {
		JsonResponse<PageInfo<UserOrderInfoDTO>>jsonResponse = new JsonResponse<>();
		if(null == entity){
			jsonResponse.setRetDesc("请求参数不为空");
			return jsonResponse;
		}
		User user = SessionHolder.currentUser();
		if(null == user){
			jsonResponse.setRetDesc("请重新登录");
			return jsonResponse;
		}
		//账单从订单过来---> 商品订单来
		String uid = user.getUid();
		OrderInfo info = new OrderInfo();
		info.setUid(uid);
		info.setOrderType(Constants.PAY_PRODUCT);
		info.setPageNum(entity.getPageNum());
		info.setPageSize(entity.getPageSize());
		PageHelper.startPage(info.getPageNum(), info.getPageSize());
		List<OrderInfo> list = orderInfoMapper.listOrderInfo(info);
		List<UserOrderInfoDTO>ret = new ArrayList<>();
		if(CollectionUtils.isNotEmpty(list)){
			UserOrderInfoDTO dto = null;
			for (OrderInfo orderInfo : list) {
				dto = new UserOrderInfoDTO();
				BeanUtils.copyProperties(orderInfo, dto);
				String productDetail = orderInfo.getProductDetail();
				if(StringUtils.isNotBlank(productDetail)){
					List<UserOrderProductDTO> products = JSONArray.parseArray(productDetail, UserOrderProductDTO.class);
					dto.setProducts(products);
				}
				ret.add(dto);
			}
		}
		PageInfo <UserOrderInfoDTO>pageInfo = new PageInfo<UserOrderInfoDTO>(ret);
		jsonResponse.success(pageInfo);
		return jsonResponse;
	}

	@Override
	public JsonResponse<List<UserPhoneRecord>> listUserPhoneRecord() {
		JsonResponse<List<UserPhoneRecord>>jsonResponse = new JsonResponse<>();
		User user = SessionHolder.currentUser();
		if(null == user){
			jsonResponse.setRetDesc("请重新登录");
			return jsonResponse;
		}
		String uid = user.getUid();
		OrderInfo info = new OrderInfo();
		info.setUid(uid);
		info.setOrderType(Constants.PAY_PHONE);
		List<OrderInfo> list = orderInfoMapper.listOrderInfo(info);
		List<UserPhoneRecord> ret = new ArrayList<>();
		if(CollectionUtils.isNotEmpty(list)){
			for (OrderInfo orderInfo : list) {
				UserPhoneRecord dto = new UserPhoneRecord();
				ret.add(dto);
				dto.setPhone(orderInfo.getDescription());
				dto.setCreateTime(orderInfo.getCreateTime());
				dto.setTotalMoney(orderInfo.getTotalMoney());
				dto.setDiscountMoney(orderInfo.getTotalPay());
				Integer s = orderInfo.getPayStatus();
				dto.setStatus(s==1?"未付款":(s==3?"已取消":"已付款"));
			}
		}
		jsonResponse.success(ret);
		return jsonResponse;
	}

	
	@Override
	public JsonResponse<Boolean> cancelUserOrderInfo(String orderId) {
		JsonResponse<Boolean>jsonResponse = new JsonResponse<>();
		if(StringUtils.isBlank(orderId)){
			jsonResponse.setRetDesc("orderId不为空");
			return jsonResponse;
		}
		User user = SessionHolder.currentUser();
		if(null == user){
			jsonResponse.setRetDesc("请重新登录");
			return jsonResponse;
		}
		String uid = user.getUid();
		try {
			OrderInfo info = new OrderInfo();
			info.setUid(uid);
			info.setOrderId(orderId);
			info.setStatus(Constants.ORDER_STATUS_WWC);
			info.setPayStatus(Constants.ORDER_PAYSTATUS_WFK);
			info.setOrderType(Constants.PAY_PRODUCT);
			OrderInfo orderInfo = orderInfoMapper.getPayOrder(info);
			if(null == orderInfo){
				jsonResponse.setRetDesc("当前记录不存在");
				return jsonResponse;
			}
			//更新订单状态
			info.setPayStatus(Constants.ORDER_PAYSTATUS_YQX);
			info.setStatus(Constants.ORDER_STATUS_WC);
			orderInfoMapper.update(info);
			setUserMoney(orderInfo);
			jsonResponse.success(true);
		} catch (Exception e) {
			throw new BusinessException(e.getMessage());
		}
		return jsonResponse;
	}

	
	@Override
	public JsonResponse<Boolean> confirmUserOrderInfo(String orderId) {
		JsonResponse<Boolean>jsonResponse = new JsonResponse<>();
		if(StringUtils.isBlank(orderId)){
			jsonResponse.setRetDesc("orderId不为空");
			return jsonResponse;
		}
		User user = SessionHolder.currentUser();
		if(null == user){
			jsonResponse.setRetDesc("请重新登录");
			return jsonResponse;
		}
		String uid = user.getUid();
		logger.info("用户确认收货orderId={}",orderId);
		try {
			OrderInfo info = new OrderInfo();
			info.setUid(uid);
			info.setOrderId(orderId);
			info.setStatus(Constants.ORDER_STATUS_WWC);
			info.setPayStatus(Constants.ORDER_PAYSTATUS_YFK);
			info.setOrderType(Constants.PAY_PRODUCT);
			OrderInfo orderInfo = orderInfoMapper.getPayOrder(info);
			if(null == orderInfo){
				jsonResponse.setRetDesc("当前记录不存在");
				return jsonResponse;
			}
			logger.info("用户确认收货获取的订单信息orderInfo={}",JSON.toJSON(orderInfo));
			//更新订单状态
			info.setPayStatus(Constants.ORDER_PAYSTATUS_YFK);
			info.setStatus(Constants.ORDER_STATUS_WC);
			orderInfoMapper.update(info);
			//设置分佣的内容;
			logger.info("用户确认收货orderId={}",orderId);
			jsonResponse.success(true);
		} catch (Exception e) {
			throw new BusinessException("用户确认收款失败");
		}
		return jsonResponse;
	}
}



