package com.khy.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.khy.common.Constants;
import com.khy.common.JsonResponse;
import com.khy.entity.OnlineParame;
import com.khy.entity.User;
import com.khy.entity.UserAddress;
import com.khy.entity.UserBank;
import com.khy.entity.UserCash;
import com.khy.entity.UserInviter;
import com.khy.entity.UserRecord;
import com.khy.exception.BusinessException;
import com.khy.interceptor.LoginInterceptor;
import com.khy.mapper.UserAddressMapper;
import com.khy.mapper.UserBankMapper;
import com.khy.mapper.UserCashMapper;
import com.khy.mapper.UserInviterMapper;
import com.khy.mapper.UserMapper;
import com.khy.mapper.dto.CartMoneyDTO;
import com.khy.mapper.dto.UserAddressListDTO;
import com.khy.mapper.dto.UserInviterDTO;
import com.khy.service.UesrService;
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
		User userDb = userMapper.getUserByPhone(phone);
		if(null == userDb){
			jsonResponse.setRetDesc("当前账户不存在");
			return jsonResponse;
		}
		if(!userDb.getPassword().equals(DigestUtils.md5DigestAsHex(password.getBytes()))){
			jsonResponse.setRetDesc("密码错误");
			return jsonResponse;
		}
		userDb.hide(userDb);
		String token = Utils.getToken();
		userDb.setToken(token);
		cacheService.setString(Constants.USER_LOGIN.concat(token), JSON.toJSONString(userDb));
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
		cacheService.setString(Constants.USER_LOGIN.concat(token), JSON.toJSONString(userDb));
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
		//邀请人
		String inviterUid = user.getInviterUid();
		if(StringUtils.isNotBlank(inviterUid)){
			User inviterUser = userMapper.getUserByUid(inviterUid);
			if(null != inviterUser){
				user.setInviterPhone(inviterUser.getPhone());
				user.setInviterUid(inviterUid);
			}
		}
		String uid = getUid();
		user.setUid(uid);
		user.setCreateTime(new Date());
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
		cacheService.delKey(Constants.USER_LOGIN.concat(user.getToken()));
		SessionHolder.cleanUser();
		jsonResponse.success(true);
		logger.info("退出登录接口响应的参数内容"+JSON.toJSONString(jsonResponse));
		return jsonResponse;
	}

	@Override
	public JsonResponse<Boolean> saveUserAddress(UserAddress userAddress) {
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
		userAddress.setUid(uid);
		userAddress.setCreateTime(new Date());
		if(null == userAddress.getIsDefault()){
			userAddress.setIsDefault(0);
		}else if(userAddress.getIsDefault().intValue()==1){
			userAddressMapper.batchUpdate(uid);
		}
		int flag = userAddressMapper.insert(userAddress);
		if(flag > 0){
			jsonResponse.success(true);
		}else{
			jsonResponse.success(false);
		}
		logger.info("保存用户的地址接口响应的参数内容"+JSON.toJSONString(jsonResponse));
		return jsonResponse;
	}

	@Override
	public JsonResponse<UserAddressListDTO> listUserAddress() {
		JsonResponse<UserAddressListDTO> jsonResponse = new JsonResponse<>();
		User user = SessionHolder.currentUser();
		if(null == user){
			jsonResponse.setRetDesc("请重新登录");
			return jsonResponse;
		}
		String uid = user.getUid();
		List<UserAddress> list = userAddressMapper.listUserAddress(uid);
		UserAddressListDTO dto = null;
		if(CollectionUtils.isNotEmpty(list)){
			dto = new UserAddressListDTO();
			dto.setUid(uid);
			dto.setList(list);
		}
		jsonResponse.success(dto);
		logger.info("获取用户的地址接口响应的参数内容"+JSON.toJSONString(jsonResponse));
		return jsonResponse;
	}

	@Override
	public JsonResponse<Boolean> updateUserAddress(UserAddress userAddress) {
		logger.info("更新用户的地址请求参数内容user={}"+JSON.toJSONString(userAddress));
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
		userAddress.setUid(uid);
		
		UserAddress userAddressDb = userAddressMapper.getById(userAddress.getId());
		if(null == userAddressDb || null == userAddressDb.getUid() || !userAddressDb.getUid().equals(uid)){
			jsonResponse.setRetDesc("当前记录不存在");
			return jsonResponse;
		}
		if(null == userAddress.getIsDefault()){
			userAddress.setIsDefault(0);
		}else if(userAddress.getIsDefault().intValue()==1){
			userAddressMapper.batchUpdate(uid);
		}
		
		int flag = userAddressMapper.update(userAddress);
		if(flag > 0){
			jsonResponse.success(true);
		}else{
			jsonResponse.success(false);
		}
		logger.info("更新用户的地址接口响应的参数内容"+JSON.toJSONString(jsonResponse));
		return jsonResponse;
	}

	@Override
	public JsonResponse<Boolean> deleteUserAddress(Long id) {
		JsonResponse<Boolean>jsonResponse = new JsonResponse<>();
		User user = SessionHolder.currentUser();
		if(null == user){
			jsonResponse.setRetDesc("请重新登录");
			return jsonResponse;
		}
		String uid = user.getUid();
		UserAddress userAddress = userAddressMapper.getById(id);
		if(null == userAddress || null == userAddress.getUid() || !userAddress.getUid().equals(uid)){
			jsonResponse.setRetDesc("当前记录不存在");
			return jsonResponse;
		}
		int flag = userAddressMapper.deleteUserAddress(id);
		if(flag > 0){
			jsonResponse.success(true);
		}else{
			jsonResponse.success(false);
		}
		logger.info("删除用户的地址接口响应的参数内容"+JSON.toJSONString(jsonResponse));
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
			BeanUtils.copyProperties(userBank, bank);
			bank.setUpdateTime(date);
			flag = userBankMapper.update(bank);
		}
		if(flag > 0){
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
	public JsonResponse<List<UserInviterDTO>> lisUserInviter(UserInviter userInviter) {
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
		if(json.getIntValue("code")==1000){
			jsonResponse.setRetDesc(json.getString("msg"));
			return jsonResponse;
		}
		User userOther = json.getObject("user", User.class);
		if(null == userOther){
			jsonResponse.setRetDesc("对方账户不存在");
			return jsonResponse;
		}
		
		try {
			//更新当前用户的点卡金额
			userDb.setCardMoney(cardMoney.subtract(amount));
			userMapper.updateUser(userDb);
			
			Date now = new Date();
			UserRecord record = new UserRecord();
			String descr = "给"+phone+"账户点卡转账"+amount+":元";
			saveUserRecord(uid,Constants.RECORD_PAY,Constants.RECORD_CARD_MONEY,amount,cardMoney,phone,descr,now);

			BigDecimal cardMoneyOther = userOther.getCardMoney()!=null ?userOther.getCardMoney():ZERO;
			userOther.setCardMoney(cardMoneyOther.add(amount));
			userMapper.updateUser(userOther);
			String description ="给"+phone+"账户点卡转账"+amount+":元";
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

}



