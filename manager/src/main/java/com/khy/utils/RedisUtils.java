package com.khy.utils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import redis.clients.jedis.BinaryClient.LIST_POSITION;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.SortingParams;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.Tuple;
import redis.clients.jedis.exceptions.JedisException;
import redis.clients.util.SafeEncoder;

/***
 * redis对象需要不断的从jedispool 中获取
 * 然后关闭才能重复使用
 * 如果jedispool 设置的maxTotal 中的数量是100
 * 然后通过 Jedis jedis = jedispool.jedisPool.getResource()
 * 获取了100次之后再次获取则报错
 * 
 * @author kanghanyu
 *
 */
@Configuration
@PropertySource(value = "classpath:redis.properties", encoding = "UTF-8")
@ConfigurationProperties(prefix = "redis")
public class RedisUtils {
	
	private JedisPool jedisPool;
	private Jedis jedis;
	/** 操作Key的方法 */
	public Keys KEYS = new Keys();
	/** 对存储结构为String类型的操作 */
	public Strings STRINGS = new Strings();
	/** 对存储结构为List类型的操作 */
	public Lists LISTS = new Lists();
	/** 对存储结构为Set类型的操作 */
	public Sets SETS = new Sets();
	/** 对存储结构为HashMap类型的操作 */
	public Hash HASH = new Hash();
	/** 对存储结构为Set(排序的)类型的操作 */
	public SortSet SORTSET = new SortSet();
	
	public Khy Khy = new Khy();
	
	@Bean("jedisPool")
	public JedisPool getJedisPool() {
		JedisPoolConfig pool = new JedisPoolConfig();
		pool.setMaxIdle(maxIdle);
		pool.setMinIdle(minIdle);
		pool.setMaxTotal(maxTotal);
		jedisPool = new JedisPool(pool, host, port);
		return jedisPool;
	}
	
	/***
	 * 获取redis对象
	 * @Description
	 * @author khy
	 * @date  2018年7月25日下午2:50:39
	 * @return
	 */
	public Jedis getJedis() {
		Jedis jedis = null;
		if (null != jedisPool) {
			jedis = jedisPool.getResource();
			jedis.auth(password);
		}
		return jedis;
	}
	
	
	/**
	 * 释放redis
	 * @Description
	 * @author khy
	 * @date  2018年7月25日上午11:02:13
	 * @param jedis
	 */
	private void closeJedis(Jedis jedis) {
		if(null != jedis){
			jedis.close();
		}
	}

	
	/**
	 * 加锁的方法内容
	 * @Description
	 * @author khy
	 * @date  2018年7月25日上午11:40:13
	 * @param locaName 加锁的名称
	 * @param acquireTimeout 获取锁的超时时间，超过这个时间则放弃获取锁
	 * @param seconds 超时时间，上锁后超过此时间则自动释放锁
	 * @return
	 */
	public String lockWithTimeout(String locaName, long acquireTimeout, int seconds) {
		Jedis jedis = getJedis();
		String retIdentifier = null;
		try {
			// 随机生成一个value
			String identifier = UUID.randomUUID().toString();
			// 锁名，即key值
			String lockKey = "lock:" + locaName;

			// 获取锁的超时时间，超过这个时间则放弃获取锁
			long end = System.currentTimeMillis() + acquireTimeout;
			while (System.currentTimeMillis() < end) {
				if (jedis.setnx(lockKey, identifier) == 1) {//表示加锁成功
					jedis.expire(lockKey, seconds);
					// 返回value值，用于释放锁时间确认
					retIdentifier = identifier;
					return retIdentifier;
				}
				// 返回-1代表key没有设置超时时间，为key设置一个超时时间
				if (jedis.ttl(lockKey) == -1) {
					jedis.expire(lockKey, seconds);
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		} catch (JedisException e) {
			e.printStackTrace();
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		return retIdentifier;
	}
	
	
	/***
	 * 释放redis锁的内容
	 * @Description
	 * @author khy
	 * @date  2018年7月25日上午11:47:39
	 * @param lockName
	 * @param identifier
	 * @return
	 */
	public boolean releaseLock(String lockName, String identifier) {
		Jedis jedis = getJedis();
		String lockKey = "lock:" + lockName;
		boolean retFlag = false;
		try {
			if(StringUtils.isNotBlank(identifier)){
				while (true) {
					// 监视lock，准备开始事务
					jedis.watch(lockKey);
					// 通过前面返回的value值判断是不是该锁，若是该锁，则删除，释放锁
					if (identifier.equals(jedis.get(lockKey))) {
						Transaction transaction = jedis.multi();
						transaction.del(lockKey);
						List<Object> results = transaction.exec();
						if (results == null) {
							continue;
						}
						retFlag = true;
					}
					jedis.unwatch();
					break;
				}
			}
		} catch (JedisException e) {
			e.printStackTrace();
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		return retFlag;
	}
	
	
	
	
	
	

	
	public class Keys {
		
		public void save() {
			jedis = getJedis();
			jedis.bgsave();
			closeJedis(jedis);
		}
		
		public Set<String> keys(String pattern) {
			jedis = getJedis();
			Set<String> set = jedis.keys(pattern);
			closeJedis(jedis);
			return set;
		}


		/**
		 * 设置key的过期时间，以秒为单位
		 * 
		 * @param String
		 *            key
		 * @param 时间,已秒为单位
		 * @return 影响的记录数
		 */
		public long expire(String key, int seconds) {
			jedis = getJedis();
			Long ret = jedis.expire(key, seconds);
			closeJedis(jedis);
			return ret;
		}

		/**
		 * 设置key的过期时间，以秒为单位
		 * 
		 * @param byte[]
		 *            key
		 * @param 时间,已秒为单位
		 * @return 影响的记录数
		 */
		public long expire(byte[] key, int seconds) {
			jedis = getJedis();
			Long ret = jedis.expire(key, seconds);
			closeJedis(jedis);
			return ret;
		}

		/**
		 * 设置key的过期时间,它是距历元（即格林威治标准时间 1970 年 1 月 1 日的 00:00:00，格里高利历）的偏移量。
		 * 
		 * @param String
		 *            key
		 * @param 时间,已秒为单位
		 * @return 影响的记录数
		 */
		public long expireAt(String key, long timestamp) {
			jedis = getJedis();
			Long ret = jedis.expireAt(key, timestamp);
			closeJedis(jedis);
			return ret;
		}

		/**
		 * 查询key的过期时间
		 * 
		 * @param String
		 *            key
		 * @return 以秒为单位的时间表示
		 */
		public Long ttl(String key) {
			jedis = getJedis();
			Long ret = jedis.ttl(key);
			closeJedis(jedis);
			return ret;
		}

		/**
		 * 删除key对应的记录
		 * 
		 * @param String
		 *            key
		 * @return 删除的记录数
		 */
		public long del(String key) {
			jedis = getJedis();
			Long ret = jedis.del(key);
			closeJedis(jedis);
			return ret;
		}

		/**
		 * 删除key对应的记录
		 * 
		 * @param String
		 *            key
		 * @return 删除的记录数
		 */
		public long del(byte key[]) {
			jedis = getJedis();
			Long ret = jedis.del(key);
			closeJedis(jedis);
			return ret;
		}

		/**
		 * 判断key是否存在
		 * 
		 * @param String
		 *            key
		 * @return boolean
		 */
		public boolean exists(String key) {
			jedis = getJedis();
			Boolean ret = jedis.exists(key);
			closeJedis(jedis);
			return ret;
		}

		/**
		 * 对List,Set,SortSet进行排序,如果集合数据较大应避免使用这个方法
		 * 
		 * @param String
		 *            key
		 * @return List<String> 集合的全部记录
		 **/
		public List<String> sort(String key) {
			jedis = getJedis();
			List<String> ret = jedis.sort(key);
			closeJedis(jedis);
			return ret;
		}

		/**
		 * 对List,Set,SortSet进行排序或limit
		 * 
		 * @param String
		 *            key
		 * @param SortingParams
		 *            parame 定义排序类型或limit的起止位置.
		 * @return List<String> 全部或部分记录
		 **/
		public List<String> sort(String key, SortingParams parame) {
			jedis = getJedis();
			List<String> ret = jedis.sort(key, parame);
			closeJedis(jedis);
			return ret;
		}

		/**
		 * 返回指定key存储的类型
		 * 
		 * @param String
		 *            key
		 * @return String string|list|set|zset|hash
		 **/
		public String type(String key) {
			jedis = getJedis();
			String ret = jedis.type(key);
			closeJedis(jedis);
			return ret;
		}

		/** 通过pipeline的方式查询数据 **/
//		public List<Object> pipeGet(List<PipeParam> params) {
//			if (params == null || params.size() == 0) {
//				return Collections.emptyList();
//			} else {
//				Jedis jedis = null;
//				try {
//					jedis = jedisPool.getResource();
//					jedis.auth(password);
//					Pipeline pipeline = jedis.pipelined();
//					for (PipeParam param : params) {
//						switch (param.getRtype()) {
//						case PipeParam.STRING:
//							pipeline.get(param.getKey1());
//							break;
//						case PipeParam.SORTSET:
//							pipeline.zscore(param.getKey1(), param.getKey2());
//							break;
//						case PipeParam.LPUSH:
//							pipeline.lpush(param.getKey1(), param.getValue());
//							break;
//						case PipeParam.KEY:
//							pipeline.del(param.getKey1());
//							break;
//						case PipeParam.HASH:
//							pipeline.hdel(param.getKey1(), param.getKey2());
//							break;
//						case PipeParam.ZINCRBY:
//							pipeline.zincrby(param.getKey1(), param.getScore(), param.getValue());
//							break;
//						default:
//							new Throwable("Type " + param.getRtype() + " is Not define in PipeParam");
//						}
//					}
//					return pipeline.syncAndReturnAll();
//				} finally {
//					jedisPool.returnResourceObject(jedis);
//				}
//			}
//		}
	}

	public class Sets {

		/**
		 * 向Set添加一条记录，如果member已存在返回0,否则返回1
		 * 
		 * @param String
		 *            key
		 * @param String
		 *            member
		 * @return 操作码,0或1
		 */
		public long sadd(String key, String... member) {
			jedis = getJedis();
			Long ret = jedis.sadd(key, member);
			closeJedis(jedis);
			return ret;
		}

		/**
		 * 获取给定key中元素个数
		 * 
		 * @param String
		 *            key
		 * @return 元素个数
		 */
		public long scard(String key) {
			jedis = getJedis();
			Long ret = jedis.scard(key);
			closeJedis(jedis);
			return ret;
		}

		/**
		 * 确定一个给定的值是否存在
		 * 
		 * @param String
		 *            key
		 * @param String
		 *            member 要判断的值
		 * @return 存在返回1，不存在返回0
		 **/
		public boolean sismember(String key, String member) {
			jedis = getJedis();
			Boolean ret = jedis.sismember(key, member);
			closeJedis(jedis);
			return ret;
		}

		/**
		 * 返回集合中的所有成员
		 * 
		 * @param String
		 *            key
		 * @return 成员集合
		 */
		public Set<String> smembers(String key) {
			jedis = getJedis();
			Set<String> ret = jedis.smembers(key);
			closeJedis(jedis);
			return ret;
		}

		/**
		 * 从集合中删除成员
		 * 
		 * @param String
		 *            key
		 * @return 被删除的成员
		 */
		public String spop(String key) {
			jedis = getJedis();
			String ret = jedis.spop(key);
			closeJedis(jedis);
			return ret;
		}

		/**
		 * 从集合中删除指定成员
		 * 
		 * @param String
		 *            key
		 * @param String
		 *            member 要删除的成员
		 * @return 状态码，成功返回1，成员不存在返回0
		 */
		public long srem(String key, String member) {
			jedis = getJedis();
			Long ret = jedis.srem(key, member);
			closeJedis(jedis);
			return ret;
		}

		/**
		 * 随机返回集合中的一条记录
		 * 
		 * @param key
		 * @return
		 */
		public String srandmember(String key) {
			jedis = getJedis();
			String ret = jedis.srandmember(key);
			closeJedis(jedis);
			return ret;
		}
	}

	public class SortSet {

		/**
		 * 向集合中增加一条记录,如果这个值已存在，这个值对应的权重将被置为新的权重
		 * 
		 * @param String
		 *            key
		 * @param double
		 *            score 权重
		 * @param String
		 *            member 要加入的值，
		 * @return 状态码 1成功，0已存在member的值
		 */
		public long zadd(byte[] key, double score, byte[] member) {
			jedis = getJedis();
			Long ret = jedis.zadd(key, score, member);
			closeJedis(jedis);
			return ret;
		}

		/**
		 * 向集合中增加一条记录,如果这个值已存在，这个值对应的权重将被置为新的权重
		 * 
		 * @param String
		 *            key
		 * @param double
		 *            score 权重
		 * @param String
		 *            member 要加入的值，
		 * @return 状态码 1成功，0已存在member的值
		 */
		public long zadd(String key, double score, String member) {
			jedis = getJedis();
			Long ret = jedis.zadd(key, score, member);
			closeJedis(jedis);
			return ret;
		}

		/**
		 * 获取集合中元素的数量
		 * 
		 * @param String
		 *            key
		 * @return 如果返回0则集合不存在
		 */
		public long zcard(String key) {
			jedis = getJedis();
			Long ret = jedis.zcard(key);
			closeJedis(jedis);
			return ret;
		}

		/**
		 * 获取指定权重区间内集合的数量
		 * 
		 * @param String
		 *            key
		 * @param double
		 *            min 最小排序位置
		 * @param double
		 *            max 最大排序位置
		 */
		public long zcount(String key, double min, double max) {
			jedis = getJedis();
			Long ret = jedis.zcount(key, min, max);
			closeJedis(jedis);
			return ret;
		}

		/**
		 * 获得set的长度
		 * 
		 * @param key
		 * @return
		 */
		public long zlength(String key) {
			Set<String> set = zrange(key, 0, -1);
			long ret = set.size();
			return ret;
		}

		/**
		 * 权重增加给定值，如果给定的member已存在
		 * 
		 * @param String
		 *            key
		 * @param double
		 *            score 要增的权重
		 * @param String
		 *            member 要插入的值
		 * @return 增后的权重
		 */
		public double zincrby(String key, double score, String member) {
			jedis = getJedis();
			Double ret = jedis.zincrby(key, score, member);
			closeJedis(jedis);
			return ret;
		}

		/**
		 * 返回指定位置的集合元素,0为第一个元素，-1为最后一个元素
		 * 
		 * @param String
		 *            key
		 * @param int
		 *            start 开始位置(包含)
		 * @param int
		 *            end 结束位置(包含)
		 * @return Set<String>
		 */
		public Set<String> zrange(String key, int start, int end) {
			jedis = getJedis();
			Set<String> ret = jedis.zrange(key, start, end);
			closeJedis(jedis);
			return ret;
		}

		/**
		 * 返回指定权重区间的元素集合
		 * 
		 * @param String
		 *            key
		 * @param double
		 *            min 上限权重
		 * @param double
		 *            max 下限权重
		 * @return Set<String>
		 */
		public Set<String> zrangeByScore(String key, double min, double max) {
			jedis = getJedis();
			Set<String> ret = jedis.zrangeByScore(key, min, max);
			closeJedis(jedis);
			return ret;
		}

		/**
		 * 返回指定权重区间的元素集合
		 * 
		 * @param String
		 *            key
		 * @param double
		 *            min 上限权重
		 * @param double
		 *            max 下限权重
		 * @return Set<String>
		 */
		public Set<byte[]> zrangeByScore(byte[] key, double min, double max) {
			jedis = getJedis();
			Set<byte[]> ret = jedis.zrangeByScore(key, min, max);
			closeJedis(jedis);
			return ret;
		}

		/**
		 * 获取指定值在集合中的位置，集合排序从低到高
		 * 
		 * @see zrevrank
		 * @param String
		 *            key
		 * @param String
		 *            member
		 * @return long 位置
		 */
		public long zrank(String key, String member) {
			jedis = getJedis();
			Long ret = jedis.zrank(key, member);
			closeJedis(jedis);
			return ret;
		}

		/**
		 * 获取指定值在集合中的位置，集合排序从低到高
		 * 
		 * @see zrank
		 * @param String
		 *            key
		 * @param String
		 *            member
		 * @return long 位置
		 */
		public long zrevrank(String key, String member) {
			jedis = getJedis();
			Long ret = jedis.zrevrank(key, member);
			closeJedis(jedis);
			return ret;
		}

		/**
		 * 从集合中删除成员
		 * 
		 * @param String
		 *            key
		 * @param String
		 *            member
		 * @return 返回1成功
		 */
		public long zrem(String key, String... member) {
			jedis = getJedis();
			Long ret = jedis.zrem(key, member);
			closeJedis(jedis);
			return ret;
		}

		/**
		 * 删除
		 * 
		 * @param key
		 * @return
		 */
		public long zrem(String key) {
			jedis = getJedis();
			Long ret = jedis.del(key);
			closeJedis(jedis);
			return ret;
		}

		/**
		 * 删除给定位置区间的元素
		 * 
		 * @param String
		 *            key
		 * @param int
		 *            start 开始区间，从0开始(包含)
		 * @param int
		 *            end 结束区间,-1为最后一个元素(包含)
		 * @return 删除的数量
		 */
		public long zremrangeByRank(String key, int start, int end) {
			jedis = getJedis();
			Long ret = jedis.zremrangeByRank(key, start, end);
			closeJedis(jedis);
			return ret; 
		}

		/**
		 * 删除给定权重区间的元素
		 * 
		 * @param String
		 *            key
		 * @param double
		 *            min 下限权重(包含)
		 * @param double
		 *            max 上限权重(包含)
		 * @return 删除的数量
		 */
		public Long zremrangeByScore(String key, double min, double max) {
			jedis = getJedis();
			Long ret = jedis.zremrangeByScore(key, min, max);
			closeJedis(jedis);
			return ret;
		}

		/**
		 * 删除给定权重区间的元素
		 * 
		 * @param String
		 *            key
		 * @param double
		 *            min 下限权重(包含)
		 * @param double
		 *            max 上限权重(包含)
		 * @return 删除的数量
		 */
		public Long zremrangeByScore(byte[] key, double min, double max) {
			jedis = getJedis();
			Long ret = jedis.zremrangeByScore(key, min, max);
			closeJedis(jedis);
			return ret;
		}

		public Set<Tuple> zrevrangeWithScores(String key, long start, long end) {
			jedis = getJedis();
			Set<Tuple> ret = jedis.zrevrangeWithScores(key, start, end);
			closeJedis(jedis);
			return ret;
		}

		/**
		 * 获取给定区间的元素，原始按照权重由高到低排序
		 * 
		 * @param String
		 *            key
		 * @param int
		 *            start
		 * @param int
		 *            end
		 * @return Set<String>
		 */
		public Set<String> zrevrange(String key, long start, long end) {
			jedis = getJedis();
			Set<String> ret = jedis.zrevrange(key, start, end);
			closeJedis(jedis);
			return ret;
		}

		/**
		 * 获取给定值在集合中的权重
		 * 
		 * @param String
		 *            key
		 * @param memeber
		 * @return double 权重
		 */
		public Double zscore(String key, String memebr) {
			jedis = getJedis();
			Double ret = jedis.zscore(key, memebr);
			closeJedis(jedis);
			return ret;
		}
	}

	public class Hash {

		/**
		 * 从hash中删除指定的存储
		 * 
		 * @param String
		 *            key
		 * @param String
		 *            fieid 存储的名字
		 * @return 状态码，1成功，0失败
		 */
		public long hdel(String key, String fieid) {
			jedis = getJedis();
			Long ret = jedis.hdel(key, fieid);
			closeJedis(jedis);
			return ret;
		}

		public long hdel(String key) {
			jedis = getJedis();
			Long ret = jedis.hdel(key);
			closeJedis(jedis);
			return ret;
		}

		public long hdel(byte[] key) {
			jedis = getJedis();
			Long ret = jedis.hdel(key);
			closeJedis(jedis);
			return ret;
		}

		/**
		 * 测试hash中指定的存储是否存在
		 * 
		 * @param String
		 *            key
		 * @param String
		 *            fieid 存储的名字
		 * @return 1存在，0不存在
		 */
		public boolean hexists(String key, String fieid) {
			jedis = getJedis();
			Boolean ret = jedis.hexists(key, fieid);
			closeJedis(jedis);
			return ret;
		}

		/**
		 * 返回hash中指定存储位置的值
		 * 
		 * @param String
		 *            key
		 * @param String
		 *            fieid 存储的名字
		 * @return 存储对应的值
		 */
		public String hget(String key, String fieid) {
			jedis = getJedis();
			String ret = jedis.hget(key, fieid);
			closeJedis(jedis);
			return ret;
		}

		/**
		 * 以Map的形式返回hash中的存储和值
		 * 
		 * @param String
		 *            key
		 * @return Map<Strinig,String>
		 */
		public Map<String, String> hgetall(String key) {
			jedis = getJedis();
			Map<String, String> map = jedis.hgetAll(key);
			closeJedis(jedis);
			return map;
		}

		/**
		 * 在指定的存储位置加上指定的数字，存储位置的值必须可转为数字类型
		 * 
		 * @param String
		 *            key
		 * @param String
		 *            fieid 存储位置
		 * @param String
		 *            long value 要增加的值,可以是负数
		 * @return 增加指定数字后，存储位置的值
		 */
		public Long hincrby(String key, String fieid, long value) {
			jedis = getJedis();
			Long ret = jedis.hincrBy(key, fieid, value);
			closeJedis(jedis);
			return ret;
		}

		/**
		 * 返回指定hash中的所有存储名字,类似Map中的keySet方法
		 * 
		 * @param String
		 *            key
		 * @return Set<String> 存储名称的集合
		 */
		public Set<String> hkeys(String key) {
			jedis = getJedis();
			Set<String> ret = jedis.hkeys(key);
			closeJedis(jedis);
			return ret;
		}

		/**
		 * 获取hash中存储的个数，类似Map中size方法
		 * 
		 * @param String
		 *            key
		 * @return long 存储的个数
		 */
		public long hlen(String key) {
			jedis = getJedis();
			Long ret = jedis.hlen(key);
			closeJedis(jedis);
			return ret;
		}

		/**
		 * 根据多个key，获取对应的value，返回List,如果指定的key不存在,List对应位置为null
		 * 
		 * @param String
		 *            key
		 * @param String...
		 *            fieids 存储位置
		 * @return List<String>
		 */
		public List<String> hmget(String key, String... fieids) {
			jedis = getJedis();
			List<String> ret = jedis.hmget(key, fieids);
			closeJedis(jedis);
			return ret;
		}

		/**
		 * 添加对应关系，如果对应关系已存在，则覆盖
		 * 
		 * @param Strin
		 *            key
		 * @param Map<String,String>
		 *            对应关系
		 * @return 状态，成功返回OK
		 */
		public String hmset(String key, Map<String, String> map) {
			jedis = getJedis();
			String ret = jedis.hmset(key, map);
			closeJedis(jedis);
			return ret;
		}

		/**
		 * 添加一个对应关系
		 * 
		 * @param String
		 *            key
		 * @param String
		 *            fieid
		 * @param String
		 *            value
		 * @return 状态码 1成功，0失败，fieid已存在将更新，也返回0
		 **/
		public long hset(String key, String fieid, String value) {
			jedis = getJedis();
			Long ret = jedis.hset(key, fieid, value);
			closeJedis(jedis);
			return ret;
		}

		/**
		 * 添加对应关系，只有在fieid不存在时才执行
		 * 
		 * @param String
		 *            key
		 * @param String
		 *            fieid
		 * @param String
		 *            value
		 * @return 状态码 1成功，0失败fieid已存
		 **/
		public long hsetnx(String key, String fieid, String value) {
			jedis = getJedis();
			Long ret = jedis.hsetnx(key, fieid, value);
			closeJedis(jedis);
			return ret;
		}

		/**
		 * 获取hash中value的集合
		 * 
		 * @param String
		 *            key
		 * @return List<String>
		 */
		public List<String> hvals(String key) {
			jedis = getJedis();
			List<String> ret = jedis.hvals(key);
			closeJedis(jedis);
			return ret;
		}
	}

	public class Strings {
		/**
		 * 根据key获取记录
		 * 
		 * @param String
		 *            key
		 * @return 值
		 */
		public String get(String key) {
			jedis = getJedis();
			String ret = jedis.get(key);
			closeJedis(jedis);
			return ret;
		}

		/**
		 * 根据key获取记录
		 * 
		 * @param byte[]
		 *            key
		 * @return 值
		 */
		public byte[] get(byte[] key) {
			jedis = getJedis();
			byte[] ret = jedis.get(key);
			closeJedis(jedis);
			return ret;
		}

		/**
		 * 添加有过期时间的记录
		 * 
		 * @param String
		 *            key
		 * @param int
		 *            seconds 过期时间，以秒为单位
		 * @param String
		 *            value
		 * @return String 操作状态
		 */
		public String setEx(String key, int seconds, String value) {
			jedis = getJedis();
			String ret = jedis.setex(key, seconds, value);
			closeJedis(jedis);
			return ret;
		}

		/**
		 * 添加有过期时间的记录
		 * 
		 * @param String
		 *            key
		 * @param int
		 *            seconds 过期时间，以秒为单位
		 * @param String
		 *            value
		 * @return String 操作状态
		 */
		public String setEx(byte[] key, int seconds, byte[] value) {
			jedis = getJedis();
			String ret = jedis.setex(key, seconds, value);
			closeJedis(jedis);
			return ret;
		}

		/**
		 * 添加一条记录，仅当给定的key不存在时才插入
		 * 
		 * @param String
		 *            key
		 * @param String
		 *            value
		 * @return long 状态码，1插入成功且key不存在，0未插入，key存在
		 */
		public long setnx(String key, String value) {
			jedis = getJedis();
			Long ret = jedis.setnx(key, value);
			closeJedis(jedis);
			return ret;
		}

		/**
		 * 添加记录,如果记录已存在将覆盖原有的value
		 * 
		 * @param String
		 *            key
		 * @param String
		 *            value
		 * @return 状态码
		 */
		public String set(String key, String value) {
			return set(SafeEncoder.encode(key), SafeEncoder.encode(value));
		}

		/**
		 * 添加记录,如果记录已存在将覆盖原有的value
		 * 
		 * @param byte[]
		 *            key
		 * @param byte[]
		 *            value
		 * @return 状态码
		 */
		public String set(byte[] key, byte[] value) {
			jedis = getJedis();
			String ret = jedis.set(key, value);
			closeJedis(jedis);
			return ret;
		}

		/**
		 * 从指定位置开始插入数据，插入的数据会覆盖指定位置以后的数据<br/>
		 * 例:String str1="123456789";<br/>
		 * 对str1操作后setRange(key,4,0000)，str1="123400009";
		 * 
		 * @param String
		 *            key
		 * @param long
		 *            offset
		 * @param String
		 *            value
		 * @return long value的长度
		 */
		public long setRange(String key, long offset, String value) {
			jedis = getJedis();
			Long ret = jedis.setrange(key, offset, value);
			closeJedis(jedis);
			return ret;
		}

		/**
		 * 在指定的key中追加value
		 * 
		 * @param String
		 *            key
		 * @param String
		 *            value
		 * @return long 追加后value的长度
		 **/
		public long append(String key, String value) {
			jedis = getJedis();
			Long ret = jedis.append(key, value);
			closeJedis(jedis);
			return ret;
		}

		/**
		 * 将key对应的value减去指定的值，只有value可以转为数字时该方法才可用
		 * 
		 * @param String
		 *            key
		 * @param long
		 *            number 要减去的值
		 * @return long 减指定值后的值
		 */
		public long decrBy(String key, long number) {
			jedis = getJedis();
			Long ret = jedis.decrBy(key, number);
			closeJedis(jedis);
			return ret;
		}

		/**
		 * <b>可以作为获取唯一id的方法</b><br/>
		 * 将key对应的value加上指定的值，只有value可以转为数字时该方法才可用
		 * 
		 * @param String
		 *            key
		 * @param long
		 *            number 要增加的值
		 * @return long 相加后的值
		 */
		public long incrBy(String key, long number) {
			jedis = getJedis();
			Long ret = jedis.incrBy(key, number);
			closeJedis(jedis);
			return ret;
		}
		
		public long incr(String key) {
			jedis = getJedis();
			Long ret = jedis.incr(key);
			closeJedis(jedis);
			return ret;
		}

		/**
		 * 对指定key对应的value进行截取
		 * 
		 * @param String
		 *            key
		 * @param long
		 *            startOffset 开始位置(包含)
		 * @param long
		 *            endOffset 结束位置(包含)
		 * @return String 截取的值
		 */
		public String getrange(String key, long startOffset, long endOffset) {
			jedis = getJedis();
			String ret = jedis.getrange(key, startOffset, endOffset);
			closeJedis(jedis);
			return ret;
		}

		/**
		 * 获取并设置指定key对应的value<br/>
		 * 如果key存在返回之前的value,否则返回null
		 * 
		 * @param String
		 *            key
		 * @param String
		 *            value
		 * @return String 原始value或null
		 */
		public String getSet(String key, String value) {
			jedis = getJedis();
			String ret = jedis.getSet(key, value);
			closeJedis(jedis);
			return ret;
		}
	}

	public class Lists {
		/**
		 * List长度
		 * 
		 * @param String
		 *            key
		 * @return 长度
		 */
		public long llen(String key) {
			long ret = llen(SafeEncoder.encode(key));
			return ret;
		}

		/**
		 * List长度
		 * 
		 * @param byte[]
		 *            key
		 * @return 长度
		 */
		public long llen(byte[] key) {
			jedis = getJedis();
			Long ret = jedis.llen(key);
			closeJedis(jedis);
			return ret;
		}

		/**
		 * 覆盖操作,将覆盖List中指定位置的值
		 * 
		 * @param byte[]
		 *            key
		 * @param int
		 *            index 位置
		 * @param byte[]
		 *            value 值
		 * @return 状态码
		 */
		public String lset(byte[] key, int index, byte[] value) {
			jedis = getJedis();
			String ret = jedis.lset(key, index, value);
			closeJedis(jedis);
			return ret;
		}

		/**
		 * 覆盖操作,将覆盖List中指定位置的值
		 * 
		 * @param key
		 * @param int
		 *            index 位置
		 * @param String
		 *            value 值
		 * @return 状态码
		 */
		public String lset(String key, int index, String value) {
			return lset(SafeEncoder.encode(key), index, SafeEncoder.encode(value));
		}

		/**
		 * 在value的相对位置插入记录
		 * 
		 * @param key
		 * @param LIST_POSITION
		 *            前面插入或后面插入
		 * @param String
		 *            pivot 相对位置的内容
		 * @param String
		 *            value 插入的内容
		 * @return 记录总数
		 */
		public long linsert(String key, LIST_POSITION where, String pivot, String value) {
			jedis = getJedis();
			long ret = linsert(SafeEncoder.encode(key), where, SafeEncoder.encode(pivot), SafeEncoder.encode(value));
			closeJedis(jedis);
			return ret;
		}

		/**
		 * 在指定位置插入记录
		 * 
		 * @param String
		 *            key
		 * @param LIST_POSITION
		 *            前面插入或后面插入
		 * @param byte[]
		 *            pivot 相对位置的内容
		 * @param byte[]
		 *            value 插入的内容
		 * @return 记录总数
		 */
		public long linsert(byte[] key, LIST_POSITION where, byte[] pivot, byte[] value) {
			jedis = getJedis();
			Long ret = jedis.linsert(key, where, pivot, value);
			closeJedis(jedis);
			return ret;
		}

		/**
		 * 获取List中指定位置的值
		 * 
		 * @param String
		 *            key
		 * @param int
		 *            index 位置
		 * @return 值
		 **/
		public String lindex(String key, int index) {
			return SafeEncoder.encode(lindex(SafeEncoder.encode(key), index));
		}

		/**
		 * 获取List中指定位置的值
		 * 
		 * @param byte[]
		 *            key
		 * @param int
		 *            index 位置
		 * @return 值
		 **/
		public byte[] lindex(byte[] key, int index) {
			jedis = getJedis();
			byte[] ret = jedis.lindex(key, index);
			closeJedis(jedis);
			return ret;
		}

		/**
		 * 将List中的第一条记录移出List
		 * 
		 * @param String
		 *            key
		 * @return 移出的记录
		 */
		public String lpop(String key) {
			return SafeEncoder.encode(lpop(SafeEncoder.encode(key)));
		}

		/**
		 * 将List中的第一条记录移出List
		 * 
		 * @param byte[]
		 *            key
		 * @return 移出的记录
		 */
		public byte[] lpop(byte[] key) {
			jedis = getJedis();
			byte[] ret = jedis.lpop(key);
			closeJedis(jedis);
			return ret;
		}

		/**
		 * 将List中最后第一条记录移出List
		 * 
		 * @param String
		 *            key
		 * @return 移出的记录
		 */
		public String rpop(String key) {
			jedis = getJedis();
			String ret = jedis.rpop(key);
			closeJedis(jedis);
			return ret;
		}

		/**
		 * 将List中最后第一条记录移出List
		 * 
		 * @param byte[]
		 *            key
		 * @return 移出的记录
		 */
		public byte[] rpop(byte[] key) {
			jedis = getJedis();
			byte[] ret = jedis.rpop(key);
			closeJedis(jedis);
			return ret;
		}

		/**
		 * 向List尾部追加记录
		 * 
		 * @param String
		 *            key
		 * @param String
		 *            value
		 * @return 记录总数
		 */
		public long lpush(String key, String value) {
			return lpush(SafeEncoder.encode(key), SafeEncoder.encode(value));
		}

		/**
		 * 向List头部追加记录
		 * 
		 * @param String
		 *            key
		 * @param String
		 *            value
		 * @return 记录总数
		 */
		public long rpush(byte[] key, byte[] value) {
			jedis = getJedis();
			Long ret = jedis.rpush(key, value);
			closeJedis(jedis);
			return ret;
		}

		/**
		 * 向List头部追加记录
		 * 
		 * @param byte[]
		 *            key
		 * @param byte[]
		 *            value
		 * @return 记录总数
		 */
		public long rpush(String key, String value) {
			jedis = getJedis();
			Long ret = jedis.rpush(key, value);
			closeJedis(jedis);
			return ret;
		}

		/**
		 * 向List中追加记录
		 * 
		 * @param byte[]
		 *            key
		 * @param byte[]
		 *            value
		 * @return 记录总数
		 */
		public long lpush(byte[] key, byte[] value) {
			jedis = getJedis();
			Long ret = jedis.lpush(key, value);
			closeJedis(jedis);
			return ret;
		}

		/**
		 * 获取指定范围的记录，可以做为分页使用
		 * 
		 * @param String
		 *            key
		 * @param long
		 *            start
		 * @param long
		 *            end
		 * @return List
		 */
		public List<String> lrange(String key, long start, long end) {
			jedis = getJedis();
			List<String> ret = jedis.lrange(key, start, end);
			closeJedis(jedis);
			return ret;
		}

		/**
		 * 获取指定范围的记录，可以做为分页使用
		 * 
		 * @param byte[]
		 *            key
		 * @param int
		 *            start
		 * @param int
		 *            end 如果为负数，则尾部开始计算
		 * @return List
		 */
		public List<byte[]> lrange(byte[] key, int start, int end) {
			jedis = getJedis();
			List<byte[]> ret = jedis.lrange(key, start, end);
			closeJedis(jedis);
			return ret;
		}

		/**
		 * 删除List中c条记录，被删除的记录值为value
		 * 
		 * @param byte[]
		 *            key
		 * @param int
		 *            c 要删除的数量，如果为负数则从List的尾部检查并删除符合的记录
		 * @param byte[]
		 *            value 要匹配的值
		 * @return 删除后的List中的记录数
		 */
		public long lrem(byte[] key, int c, byte[] value) {
			jedis = getJedis();
			Long ret = jedis.lrem(key, c, value);
			closeJedis(jedis);
			return ret;
		}

		/**
		 * 删除List中c条记录，被删除的记录值为value
		 * 
		 * @param String
		 *            key
		 * @param int
		 *            c 要删除的数量，如果为负数则从List的尾部检查并删除符合的记录
		 * @param String
		 *            value 要匹配的值
		 * @return 删除后的List中的记录数
		 */
		public long lrem(String key, int c, String value) {
			return lrem(SafeEncoder.encode(key), c, SafeEncoder.encode(value));
		}

		/**
		 * 算是删除吧，只保留start与end之间的记录
		 * 
		 * @param byte[]
		 *            key
		 * @param int
		 *            start 记录的开始位置(0表示第一条记录)
		 * @param int
		 *            end 记录的结束位置（如果为-1则表示最后一个，-2，-3以此类推）
		 * @return 执行状态码
		 */
		public String ltrim(byte[] key, int start, int end) {
			jedis = getJedis();
			String ret = jedis.ltrim(key, start, end);
			closeJedis(jedis);
			return ret;
		}

		/**
		 * 算是删除吧，只保留start与end之间的记录
		 * 
		 * @param String
		 *            key
		 * @param int
		 *            start 记录的开始位置(0表示第一条记录)
		 * @param int
		 *            end 记录的结束位置（如果为-1则表示最后一个，-2，-3以此类推）
		 * @return 执行状态码
		 */
		public String ltrim(String key, int start, int end) {
			return ltrim(SafeEncoder.encode(key), start, end);
		}
	}
	
	private String password;// redis链接验证密码
	private String host;
	private int port;
	private int maxIdle;
	private int minIdle;
	private int maxTotal;

	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public int getMaxIdle() {
		return maxIdle;
	}
	public void setMaxIdle(int maxIdle) {
		this.maxIdle = maxIdle;
	}
	public int getMinIdle() {
		return minIdle;
	}
	public void setMinIdle(int minIdle) {
		this.minIdle = minIdle;
	}
	public int getMaxTotal() {
		return maxTotal;
	}
	public void setMaxTotal(int maxTotal) {
		this.maxTotal = maxTotal;
	}
	
	public class Khy{
		public String getValue(String key){
			Jedis jedis = getJedis();
			String ret = jedis.get(key);
			jedis.close();
			return ret;
		}
		
		public String get(String key) {
			Jedis jedis = getJedis();
			String ret = jedis.get(key);
			closeJedis(jedis);
			return ret;
		}
		
		public String setValue(String key,String value){
			Jedis jedis = getJedis();
			String ret = jedis.set(key, value);
			jedis.close();
			return ret;
		}
	}
}

