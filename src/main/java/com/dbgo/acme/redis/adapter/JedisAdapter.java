package com.dbgo.acme.redis.adapter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dbgo.acme.redis.util.StackTraceUtil;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Redis客户端适配器（基于JedisPool - jedis池）
 *
 * @author lixiao
 * @version V1.0
 * @date 2018年4月20日
 */

@Service
public class JedisAdapter {

	private static final Logger logger = LoggerFactory.getLogger(JedisAdapter.class);

	private JedisPool jedisPool = null;

	/**
	 * 初始化
	 */
	@PostConstruct
	public void jedisPoolInit() throws Exception {
		jedisPool = new JedisPool("localhost", 6379);

	}


	/**
	 * 将一个或多个值插入到列表头部
	 *
	 * @param key
	 *            列表KEY
	 * @param value
	 * @return
	 */
	public long lpush(String key, String value) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			long result = jedis.lpush(key, value);
			return result;
		} catch (Exception e) {
			logger.error("Jedis.lpush{},{}异常 {}", key, value, StackTraceUtil.getStackTrace(e));
			return 0;
		} finally {
			if (jedis != null) {
				try {
					jedis.close();
				} catch (Exception e) {
					logger.error(StackTraceUtil.getStackTrace(e));
				}
			}
		}
	}

	/**
	 * 移出并获取列表的最后一个元素， 如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止
	 *
	 * @param timeout
	 *            超时时间 0：永不失效
	 * @param key
	 *            列表KEY
	 * @return
	 */
	public List<String> brpop(int timeout, String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.brpop(timeout, key);
		} catch (Exception e) {
			logger.error("Jedis.brpop{}异常 {}", key, StackTraceUtil.getStackTrace(e));
			return null;
		} finally {
			if (jedis != null) {
				try {
					jedis.close();
				} catch (Exception e) {
					logger.error(StackTraceUtil.getStackTrace(e));
				}
			}
		}
	}

	/**
	 * 移除列表的最后一个元素，并将该元素添加到另一个列表并返回
	 *
	 * @param srckey
	 *            列表KEY
	 * @param dstkey
	 *            副本列表KEY
	 * @return
	 */
	public String rpoplpush(String srckey, String dstkey) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.rpoplpush(srckey, dstkey);
		} catch (Exception e) {
			logger.error("Jedis.rpoplpush{},{}异常{}", srckey, dstkey, StackTraceUtil.getStackTrace(e));
			return null;
		} finally {
			if (jedis != null) {
				try {
					jedis.close();
				} catch (Exception e) {
					logger.error(StackTraceUtil.getStackTrace(e));
				}
			}
		}
	}

	/**
	 * 从列表中弹出一个值，将弹出的元素插入到另外一个列表中并返回它；
	 * <p>
	 * 如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止。
	 *
	 * @param srckey
	 *            列表KEY
	 * @param dstkey
	 *            副本列表KEY
	 * @return
	 */
	public String brpoplpush(String srckey, String dstkey) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.brpoplpush(srckey, dstkey, 0);
		} catch (Exception e) {
			logger.error("Jedis.brpoplpush{},{}异常{}", srckey, dstkey, StackTraceUtil.getStackTrace(e));
			return null;
		} finally {
			if (jedis != null) {
				try {
					jedis.close();
				} catch (Exception e) {
					logger.error(StackTraceUtil.getStackTrace(e));
				}
			}
		}
	}

	/**
	 * 移除列表元素
	 *
	 * @param key
	 *            列表KEY
	 * @param value
	 *            列表元素VALUE
	 * @return
	 */
	public long lrem(String key, String value) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.lrem(key, 0, value);
		} catch (Exception e) {
			logger.error("Jedis.lrem{},{}异常{}", key, value, StackTraceUtil.getStackTrace(e));
			return -1;
		} finally {
			if (jedis != null) {
				try {
					jedis.close();
				} catch (Exception e) {
					logger.error(StackTraceUtil.getStackTrace(e));
				}
			}
		}
	}

	/**
	 * 移除并获取列表最后一个元素
	 *
	 * @param key
	 * @return
	 */
	public String rpop(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.rpop(key);
		} catch (Exception e) {
			logger.error("Jedis.rpop{}异常{}", key, StackTraceUtil.getStackTrace(e));
			return null;
		} finally {
			if (jedis != null) {
				try {
					jedis.close();
				} catch (Exception e) {
					logger.error(StackTraceUtil.getStackTrace(e));
				}
			}
		}
	}

	/**
	 * 给Redis中Set集合中某个key值设值
	 *
	 * @param key
	 * @param value
	 */
	public void set(String key, String value) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			jedis.set(key, value);
		} catch (Exception e) {
			logger.error("Jedis.set{},{}异常{}", key, value, StackTraceUtil.getStackTrace(e));
		} finally {
			if (jedis != null) {
				try {
					jedis.close();
				} catch (Exception e) {
					logger.error(StackTraceUtil.getStackTrace(e));
				}
			}
		}
	}

	/**
	 * 获取key对应value值
	 *
	 * @param key
	 */
	public String get(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.get(key);
		} catch (Exception e) {
			logger.error("Jedis.get{}异常{}", key, StackTraceUtil.getStackTrace(e));
			return null;
		} finally {
			if (jedis != null) {
				try {
					jedis.close();
				} catch (Exception e) {
					logger.error(StackTraceUtil.getStackTrace(e));
				}
			}
		}
	}

	/**
	 * 序列化
	 *
	 * @param key
	 * @param object
	 */
	public void setObject(String key, Object object) {
		set(key, JSONObject.toJSONString(object));
	}

	public <T> T getObject(String key, Class<T> clazz) {
		String value = get(key);
		if (value != null) {
			return JSON.parseObject(value, clazz);
		}
		return null;
	}
}
