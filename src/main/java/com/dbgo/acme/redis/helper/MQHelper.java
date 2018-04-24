package com.dbgo.acme.redis.helper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dbgo.acme.redis.core.Producer;
import com.dbgo.acme.redis.model.MQModel;

/**
 * 基于Redis链表实现的消息队列发布帮助类
 * 
 * @author lixiao
 * @date 2018年4月20日
 * @version V1.0
 */
@Component
public class MQHelper {

	static Producer producer;

	@Autowired
	private MQHelper(Producer producer) {
		MQHelper.producer = producer;
	}

	/**
	 * 创建消息队列
	 * 
	 * @param mode
	 * @return
	 */
	public static boolean createMQ(MQModel mode) {
		return producer.createMessageQueue(mode);
	}

}
